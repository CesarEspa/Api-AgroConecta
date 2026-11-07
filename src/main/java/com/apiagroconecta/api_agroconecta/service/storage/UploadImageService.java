package com.apiagroconecta.api_agroconecta.service.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

/**
 * Sube imágenes de productos y devuelve su URL pública.
 *
 * Proveedor según las variables de entorno disponibles:
 *  1. Supabase Storage  (SUPABASE_URL + SUPABASE_SERVICE_KEY)  ← recomendado
 *  2. Google Cloud Storage (GCP_CREDENTIALS_JSON)
 * Si no hay ninguno configurado la API arranca igual; solo falla la subida
 * de imágenes nuevas con un mensaje claro.
 */
@Service
public class UploadImageService {

    private static final Logger log = LoggerFactory.getLogger(UploadImageService.class);

    private final String supabaseUrl;
    private final String supabaseKey;
    private final String supabaseBucket;
    private final String gcpBucket;
    private final Storage gcpStorage;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private volatile boolean bucketVerificado = false;

    public UploadImageService(
            @Value("${app.storage.supabase.url:}") String supabaseUrl,
            @Value("${app.storage.supabase.key:}") String supabaseKey,
            @Value("${app.storage.supabase.bucket:productos}") String supabaseBucket,
            @Value("${gcp.bucket.name:}") String gcpBucket,
            @Value("${gcp.credentials.json:}") String gcpCredentialsJson) throws IOException {

        this.supabaseUrl = supabaseUrl == null ? "" : supabaseUrl.replaceAll("/+$", "");
        this.supabaseKey = supabaseKey;
        this.supabaseBucket = supabaseBucket;
        this.gcpBucket = gcpBucket;

        if (gcpCredentialsJson != null && !gcpCredentialsJson.isBlank()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(gcpCredentialsJson.getBytes(StandardCharsets.UTF_8)));
            this.gcpStorage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        } else {
            this.gcpStorage = null;
        }

        if (usaSupabase()) log.info("Imágenes: Supabase Storage (bucket '{}')", supabaseBucket);
        else if (gcpStorage != null) log.info("Imágenes: Google Cloud Storage (bucket '{}')", gcpBucket);
        else log.warn("Imágenes: sin almacenamiento configurado. Define SUPABASE_URL y SUPABASE_SERVICE_KEY.");
    }

    private boolean usaSupabase() {
        return !supabaseUrl.isBlank() && supabaseKey != null && !supabaseKey.isBlank();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename() == null ? "imagen" : file.getOriginalFilename();
        String fileName = UUID.randomUUID() + "-" + original.replaceAll("[^A-Za-z0-9._-]", "_");
        String contentType = file.getContentType() == null ? "application/octet-stream" : file.getContentType();
        return subir(fileName, contentType, file.getBytes());
    }

    public String uploadBase64(String base64Image) throws IOException {
        String contentType = "image/png";
        String extension = "png";
        byte[] decodedBytes;

        if (base64Image.startsWith("data:")) {
            String[] parts = base64Image.split(",", 2);
            String metadata = parts[0];
            contentType = metadata.substring(metadata.indexOf(":") + 1, metadata.indexOf(";"));
            extension = contentType.substring(contentType.indexOf("/") + 1).replace("jpeg", "jpg");
            decodedBytes = Base64.getDecoder().decode(parts[1]);
        } else {
            decodedBytes = Base64.getDecoder().decode(base64Image);
        }

        if (!contentType.startsWith("image/")) {
            throw new IOException("Solo se permiten imágenes");
        }
        return subir(UUID.randomUUID() + "." + extension, contentType, decodedBytes);
    }

    private String subir(String fileName, String contentType, byte[] bytes) throws IOException {
        if (usaSupabase()) return subirASupabase(fileName, contentType, bytes);
        if (gcpStorage != null) {
            BlobInfo blobInfo = BlobInfo.newBuilder(gcpBucket, fileName).setContentType(contentType).build();
            gcpStorage.create(blobInfo, bytes);
            return String.format("https://storage.googleapis.com/%s/%s", gcpBucket, fileName);
        }
        throw new IOException("No hay almacenamiento de imágenes configurado en el servidor");
    }

    // ------------------------------------------------------------------
    // Supabase Storage (API REST)
    // ------------------------------------------------------------------
    private String subirASupabase(String fileName, String contentType, byte[] bytes) throws IOException {
        asegurarBucket();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(supabaseUrl + "/storage/v1/object/" + supabaseBucket + "/" + fileName))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .header("Content-Type", contentType)
                .header("x-upsert", "true")
                .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                .build();
        HttpResponse<String> res = enviar(req);
        if (res.statusCode() >= 300) {
            throw new IOException("Supabase Storage respondió " + res.statusCode() + ": " + res.body());
        }
        return supabaseUrl + "/storage/v1/object/public/" + supabaseBucket + "/" + fileName;
    }

    /** Crea el bucket público la primera vez (si ya existe, Supabase responde 400/409 y se ignora). */
    private void asegurarBucket() throws IOException {
        if (bucketVerificado) return;
        String body = "{\"id\":\"" + supabaseBucket + "\",\"name\":\"" + supabaseBucket
                + "\",\"public\":true,\"file_size_limit\":5242880,"
                + "\"allowed_mime_types\":[\"image/png\",\"image/jpeg\",\"image/webp\",\"image/gif\"]}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(supabaseUrl + "/storage/v1/bucket"))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = enviar(req);
        if (res.statusCode() < 300 || res.statusCode() == 400 || res.statusCode() == 409) {
            bucketVerificado = true;
        } else {
            throw new IOException("No se pudo preparar el bucket de imágenes: " + res.statusCode());
        }
    }

    private HttpResponse<String> enviar(HttpRequest req) throws IOException {
        try {
            return http.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Subida interrumpida", e);
        }
    }
}
