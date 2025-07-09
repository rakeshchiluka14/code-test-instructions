// README.md for URL Shortener

# URL Shortener

A simple Spring Boot application to shorten URLs with support for custom aliases, Swagger documentation, H2 database, and Docker.

## Features

- Accepts a full URL and returns a shortened version
- Supports user-defined custom aliases
- Provides redirect endpoint for aliases
- Allows deletion of a shortened URL
- Lists all stored shortened URLs
- REST API (Swagger/OpenAPI)
- Minimal HTML UI for demo
- In-memory H2 database
- Docker support

## Technologies Used

- Java 17
- Spring Boot 3
- JPA with H2 Database
- Springdoc OpenAPI for Swagger
- JUnit 5 and Mockito for tests
- Docker (containerization)

## Getting Started

### 🔧 Build and Run Locally

```bash
# Clone the repo
git clone https://github.com/your-repo/url-shortener.git
cd url-shortener

# Build the app
./mvnw clean package -DskipTests

# Run the app
java -jar target/url-shortener-0.0.1-SNAPSHOT.jar
```

Then open your browser:
- UI: http://localhost:8080/index.html
- Swagger UI: http://localhost:8080/swagger-ui.html

### 🐳 Run with Docker

```bash
# Build the Docker image
docker build -t url-shortener .

# Run the container
docker run -p 8080:8080 url-shortener
```

## 🧪 Testing

```bash
# Run unit and integration tests
./mvnw test
```

### What to Test
- `/shorten` – Create short URL
- `/urls` – List all URLs
- `/{alias}` – Redirect to original URL
- `DELETE /{alias}` – Delete URL

## Example Usage

### Request:
```http
POST /shorten
Content-Type: application/json
{
  "fullUrl": "https://example.com",
  "customAlias": "myalias"
}
```

### Response:
```json
{
  "alias": "myalias",
  "fullUrl": "https://example.com",
  "shortUrl": "http://localhost:8080/myalias"
}
```

---

## Notes
- H2 is used for simplicity. For production, switch to PostgreSQL or MySQL.
- Swagger is automatically available at `/swagger-ui.html`.
- Use `docker-compose` if you want to add a database in future.

---