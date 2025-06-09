##  URL Shortener
A simple, clean, and scalable URL shortening service built with **Spring Boot**, **Redis**, and **Testcontainers**. Designed to demonstrate backend engineering skills, test coverage, and clean architecture.

---

### How to Run the Project

#### Prerequisites
- Java 
- Docker 
- Gradle

### Run with Docker Compose

Service is containerized with a multi-stage Dockerfile and uses Redis as a service. To build and run both the Redis server and the Spring Boot application, use Docker Compose:

1. Build and start containers:
```bash
docker-compose up --build
```
2. This command will:
- Build the Spring Boot app image using Gradle and JDK 21
- Pull and run Redis 7 container
- Start both containers with Redis ready before the app starts (health check enabled)

3. Access the URL shortener service at:
```bash
http://localhost:8080
```

#### Environment Variables

Make sure to define the following environment variables in your .env file or a shell before running Docker Compose, or update docker-compose.yml accordingly:

* APP_BASE_URL — base URL for the service, e.g. http://localhost:8080
* REDIS_HOST — hostname of Redis service, usually redis (service name in docker-compose)
* REDIS_PORT — Redis port, typically 6379

**Example .env:**
```
APP_BASE_URL=http://localhost:8080
REDIS_HOST=redis
REDIS_PORT=6379
```

#### Stopping Containers
Stop and remove the containers by running:
```
docker-compose down
```

---

## Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Redis** 
- **JUnit 5 + MockMvc + Mockito**
- **Testcontainers** for Redis integration
- **Gradle** 
- **Docker**
- **Swagger**

---

### API Endpoints

#### `POST /api/v1/shorten`
Create a short URL for the given original URL.

**Request:**
```json
{
  "originalUrl": "https://example.com"
}
```

**Response:**

```
200 OK
"http://localhost:8080/abc123"
```

#### `GET /api/v1/shorten/{code}`
Redirects to the original URL or returns 404 if not found.

**Response:**
```
302 FOUND with redirect
404 NOT FOUND if code does not exist
```

## Features

- Generate short URLs with TTL
- Redirect to original URLs
- Input validation for safety and correctness
- In-memory Redis storage
- Error handling via global exception handler
- Integration and unit testing
---

###  Test Coverage

#### Unit Tests

- UrlShortenerServiceImplTest
- UrlShortenerControllerTest
- UrlValidatorTest

#### Integration Tests
- UrlRepositoryIntegrationTest
- UrlServiceIntegrationTest 
- UrlControllerIntegrationTest

**Run Tests:**
```
./gradlew clean test
```

### Example API calls

1. POST
```bash
curl -X POST http://localhost:8080/api/v1/shorten \
-H "Content-Type: application/json" \
-d '{"originalUrl": "https://example.com"}'
```

2. Redirect - change cf8544 to your shorten code
```bash
curl -v http://localhost:8080/api/v1/shorten/cf8544
```

#### Screenshots:
![photo_2025-06-09_23-03-35](https://github.com/user-attachments/assets/dc5a896c-f6b6-4a14-af45-11f1807fb83d)
![photo_2025-06-09_23-03-57](https://github.com/user-attachments/assets/73045333-464b-4734-a65e-24726a314d69)
![photo_2025-06-09_23-03-54](https://github.com/user-attachments/assets/6704a284-72c6-478c-9ab0-c246da8ea99f)
![photo_2025-06-09_23-03-49](https://github.com/user-attachments/assets/a6f88204-9d03-4eef-b353-1741eb281258)
![photo_2025-06-09_23-03-45](https://github.com/user-attachments/assets/ad749446-282a-44e1-82ae-3f9a05f7ad1f)

