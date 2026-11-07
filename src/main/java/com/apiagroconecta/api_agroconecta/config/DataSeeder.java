package com.apiagroconecta.api_agroconecta.config;

import com.apiagroconecta.api_agroconecta.auth.model.Rol;
import com.apiagroconecta.api_agroconecta.auth.model.Usuario;
import com.apiagroconecta.api_agroconecta.auth.repository.UsuarioRepository;
import com.apiagroconecta.api_agroconecta.model.Categoria;
import com.apiagroconecta.api_agroconecta.model.Producto;
import com.apiagroconecta.api_agroconecta.repository.CategoriaRepository;
import com.apiagroconecta.api_agroconecta.repository.ProductoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Carga datos iniciales al arrancar, solo si faltan:
 *  - Categorías y productos de ejemplo (si la tabla de productos está vacía).
 *  - Cuenta ADMIN (ADMIN_EMAIL / ADMIN_PASSWORD) y cuenta CLIENTE de demostración
 *    (DEMO_CLIENT_EMAIL / DEMO_CLIENT_PASSWORD), si están definidas y no existen.
 * Las contraseñas nunca se escriben en el código: vienen de variables de entorno.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Value("${app.seed.admin.email:}") private String adminEmail;
    @Value("${app.seed.admin.password:}") private String adminPassword;
    @Value("${app.seed.client.email:}") private String clientEmail;
    @Value("${app.seed.client.password:}") private String clientPassword;
    @Value("${app.seed.image-base:}") private String imageBase;
    @Value("${app.seed.admin.sync-password:false}") private boolean syncAdminPassword;

    public DataSeeder(CategoriaRepository categoriaRepository, ProductoRepository productoRepository,
                      UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                      ObjectMapper objectMapper) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    private record Semilla(String nombre, String precio, String descripcion, String imagen,
                           boolean enDescuento, Integer porcentaje, int cantidad, int stockMinimo,
                           String descripcionLong, String categoria) {}

    private static Semilla p(String nombre, String precio, String descripcion, String imagen,
                             boolean enDescuento, Integer porcentaje, int cantidad, int stockMinimo,
                             String descripcionLong, String categoria) {
        return new Semilla(nombre, precio, descripcion, imagen, enDescuento, porcentaje,
                cantidad, stockMinimo, descripcionLong, categoria);
    }

    private static final List<Semilla> PRODUCTOS = List.of(
            p("Semillas de Chía", "25000.00", "Semillas premium ricas en Omega-3 para alto rendimiento.", "semillas-chia.jpg", true, 10, 50, 10, "Semillas de chía seleccionadas con altos estándares de pureza. Ideales para suplementación alimenticia de alta calidad.", "SEMILLAS"),
            p("Semillas de Arroz", "38000.00", "Variedad certificada de gran rendimiento y alta germinación.", "semillas-arroz.jpg", false, null, 30, 8, "Semillas de arroz de grano largo adaptadas para sistemas de riego y secano, garantizando un alto porcentaje de germinación.", "SEMILLAS"),
            p("Semillas de Maíz", "42000.00", "Híbrido optimizado para climas cálidos y templados.", "semillas-maiz.jpg", false, null, 25, 6, "Maíz híbrido de alto potencial de rendimiento. Excelente vigor inicial y tolerancia a las principales enfermedades foliares.", "SEMILLAS"),
            p("Semillas de Frijol", "35000.00", "Grano seleccionado con excelente resistencia a plagas.", "semillas-frijol.jpg", true, 15, 40, 7, "Variedad de frijol arbustivo de ciclo corto. Grano uniforme de excelente cocción y alta demanda en el mercado comercial.", "SEMILLAS"),
            p("Concentrado Ceba", "85000.00", "Suplemento para ganancia de peso acelerada en bovinos.", "concentrado-ceba.jpg", false, null, 15, 6, "Alimento balanceado diseñado para la etapa de finalización o ceba de ganado vacuno, maximizando la conversión alimenticia.", "CONCENTRADOS"),
            p("Concentrado Ganado", "85000.00", "Equilibrio nutricional para ganado de leche de calidad.", "concentrado-ganado.jpg", false, null, 28, 9, "Fórmula especializada que aporta los nutrientes, vitaminas y minerales necesarios para mantener una alta producción láctea.", "CONCENTRADOS"),
            p("Concentrado Pollos", "55000.00", "Alimento balanceado para engorde rápido y saludable.", "concentrado-aves.jpg", true, 5, 40, 12, "Micropeletizado completo para aves de corral en fase de engorde. Promueve un desarrollo óseo y muscular óptimo.", "CONCENTRADOS"),
            p("Concentrado Cerdos", "78000.00", "Fórmula para desarrollo muscular y salud digestiva óptima.", "concentrado-cerdos.jpg", false, null, 20, 6, "Harina balanceada con aminoácidos esenciales para cerdos en etapa de levante y desarrollo, mejorando la digestibilidad.", "CONCENTRADOS"),
            p("Kit Herramientas", "120000.00", "Herramientas manuales básicas reforzadas.", "kit-herramientas.jpg", false, null, 15, 6, "Conjunto de herramientas agrícolas manuales esenciales. Fabricadas en acero templado para mayor durabilidad en campo.", "HERRAMIENTAS"),
            p("Esparcidor", "95000.00", "Herramientas de precisión para distribución de abono.", "esparcidor.jpg", true, 20, 8, 6, "Dispositivo manual ergonómico para la dosificación y esparcido uniforme de fertilizantes granulados y semillas pequeñas.", "HERRAMIENTAS"),
            p("Sistema Ordeño", "450000.00", "Equipo de alta eficiencia para medianas fincas.", "sistema-ordeno.jpg", false, null, 3, 6, "Unidad de ordeño mecánico portátil con pulsador neumático y cantina de acero inoxidable. Fácil limpieza y mantenimiento.", "HERRAMIENTAS"),
            p("Pala Profesional", "35000.00", "Acero de alta resistencia con mango ergonómico.", "pala-profesional.jpg", false, null, 22, 10, "Pala redonda forjada en una sola pieza de acero al carbono, ideal para excavación pesada y movimiento de tierras compactas.", "HERRAMIENTAS")
    );

    @Override
    @Transactional
    public void run(String... args) {
        sembrarUsuario(adminEmail, adminPassword, "Administrador", Rol.ADMIN, null);
        if (syncAdminPassword) sincronizarPassword(adminEmail, adminPassword);
        sembrarUsuario(clientEmail, clientPassword, "Cliente Demo", Rol.CLIENTE, "3001234567");

        if (productoRepository.count() > 0) {
            return;
        }

        Map<String, Categoria> categorias = new HashMap<>();
        for (Categoria c : categoriaRepository.findAll()) {
            categorias.put(c.getNombre().toUpperCase(), c);
        }

        for (Semilla s : PRODUCTOS) {
            Categoria categoria = categorias.computeIfAbsent(s.categoria(), clave -> {
                Categoria c = new Categoria();
                c.setNombre(clave.charAt(0) + clave.substring(1).toLowerCase());
                return categoriaRepository.save(c);
            });

            ObjectNode detalles = objectMapper.createObjectNode();
            detalles.put("enDescuento", s.enDescuento());
            if (s.porcentaje() != null) detalles.put("porcentajeDescuento", s.porcentaje());
            else detalles.putNull("porcentajeDescuento");

            Producto producto = new Producto();
            producto.setNombre(s.nombre());
            producto.setPrecio(new BigDecimal(s.precio()));
            producto.setDescripcion(s.descripcion());
            producto.setImagen(imageBase + s.imagen());
            producto.setDetalles(detalles);
            producto.setCantidad(s.cantidad());
            producto.setStockMinimo(s.stockMinimo());
            producto.setDescripcionLong(s.descripcionLong());
            producto.setActivo(true);
            producto.setCategoria(categoria);
            productoRepository.save(producto);
        }
        log.info("Datos iniciales: {} categorías y {} productos creados", categorias.size(), PRODUCTOS.size());
    }

    /**
     * Con ADMIN_SYNC_PASSWORD=true, la contraseña del admin se iguala a ADMIN_PASSWORD
     * en cada arranque. Sirve para cambiarla desde Render sin tocar la base de datos.
     */
    private void sincronizarPassword(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.length() < 8) {
            return;
        }
        usuarioRepository.findByEmail(email.trim().toLowerCase()).ifPresent(u -> {
            if (!passwordEncoder.matches(password, u.getPassword())) {
                u.setPassword(passwordEncoder.encode(password));
                usuarioRepository.save(u);
                log.info("Contraseña de la cuenta ADMIN actualizada desde ADMIN_PASSWORD");
            }
        });
    }

    private void sembrarUsuario(String email, String password, String nombre, Rol rol, String telefono) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return;
        }
        String normalizado = email.trim().toLowerCase();
        if (usuarioRepository.findByEmail(normalizado).isPresent()) {
            return;
        }
        if (password.length() < 8) {
            log.warn("No se creó la cuenta {}: la contraseña debe tener al menos 8 caracteres", rol);
            return;
        }
        usuarioRepository.save(new Usuario(normalizado, passwordEncoder.encode(password), nombre, rol, telefono));
        log.info("Cuenta {} inicial creada", rol);
    }
}
