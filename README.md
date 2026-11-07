# API AgroConecta 🌾

API REST del e-commerce **AgroConecta**: catálogo, carrito, pedidos con control de stock y panel de administración con roles.

- **Frontend (SPA):** [CesarEspa/AgroConecta](https://github.com/CesarEspa/AgroConecta) · demo: https://cesarespa.github.io/AgroConecta/
- **Documentación interactiva:** `/swagger-ui/index.html`

## Stack

Java 17 · Spring Boot 3.5 · Spring Security + JWT (jjwt) · Spring Data JPA / Hibernate · PostgreSQL (Supabase) · Bean Validation · springdoc OpenAPI · Docker

## Endpoints principales (`/api/v1`)

| Recurso | Público | CLIENTE | ADMIN |
|---|---|---|---|
| `POST /auth/login`, `POST /auth/register` | ✅ | | |
| `GET /productos`, `/productos/promociones`, `/categorias` | ✅ | | |
| `POST /pedidos`, `GET /pedidos/mis-pedidos` | | ✅ | |
| `GET/POST/PUT /productos/admin`, `/productos/{id}` | | | ✅ |
| `GET/PUT/PATCH /pedidos/**` | | | ✅ |
| `GET/PUT/PATCH /usuarios/**` | | | ✅ |

El contrato completo está en [FRONTEND_API_GUIDE.md](https://github.com/CesarEspa/AgroConecta/blob/main/FRONTEND_API_GUIDE.md).

## Configuración (variables de entorno)

Ningún secreto vive en el código. Copia [`.env.example`](.env.example) y completa:

| Variable | Para qué |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | PostgreSQL (Supabase → **Connect** → *Session pooler* → JDBC) |
| `JWT_SECRET` | Clave para firmar los tokens (texto largo y aleatorio) |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | Cuenta administradora inicial |
| `DEMO_CLIENT_EMAIL`, `DEMO_CLIENT_PASSWORD` | Cuenta cliente de demostración (opcional) |
| `SUPABASE_URL`, `SUPABASE_SERVICE_KEY`, `SUPABASE_BUCKET` | Subida de imágenes a Supabase Storage |
| `CORS_ORIGINS` | Orígenes permitidos (por defecto GitHub Pages y `localhost:5520`) |

Al arrancar por primera vez, `DataSeeder` crea las categorías, 12 productos de ejemplo y las cuentas indicadas, solo si no existen.

## Ejecutar en local

```bash
# PostgreSQL local con una base llamada agroconecta
export DB_URL=jdbc:postgresql://localhost:5432/agroconecta DB_USERNAME=postgres DB_PASSWORD=...
export JWT_SECRET=$(openssl rand -hex 32)
./mvnw spring-boot:run
```

Para probarla con el frontend: `http://localhost:5520/?modo=api&api=http://localhost:8080/api/v1`

## Despliegue (Render + Supabase)

1. **Supabase:** crea un proyecto (con la *Data API* desactivada: el acceso es solo por JDBC desde esta API).
2. **Render:** *New + → Blueprint* y elige este repositorio; `render.yaml` define el servicio Docker.
3. Completa en Render las variables marcadas como secretas (`sync: false`).
4. En el frontend, cambia `API_BASE_PROD` y `API_MODE = 'auto'` en `js/services/api.js`.

## Mejoras sobre la versión del bootcamp

- Credenciales de base de datos y JWT movidas a variables de entorno.
- CORS restringido a los orígenes configurados, con `PATCH` habilitado para el panel.
- Imágenes en **Supabase Storage** (Google Cloud Storage queda como opción); la API arranca aunque no haya almacenamiento configurado.
- El total del pedido aplica el descuento del producto, igual que el catálogo.
- Lecturas de pedidos dentro de transacciones (evita errores de carga *lazy*) y registro en el log de los errores no controlados.
- Datos iniciales automáticos y Dockerfile ajustado a 512 MB de RAM.

## Equipo

Proyecto final del bootcamp **Generation Colombia (Java Full Stack)**: Cesar España, Ivan Chavez, Juan Castro y Daniel Vega.
Repositorio original del backend: [idchavez/api-agroconecta](https://github.com/idchavez/api-agroconecta).
