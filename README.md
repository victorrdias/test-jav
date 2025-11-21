# Product Catalog Microservice

A REST API microservice for managing a product catalog, built with Java 25 and Spring Boot 3.5.

## Features

- ✅ Full CRUD operations for products
- ✅ Search and filter products by name, description, and price range
- ✅ Duplicate product validation (name + description)
- ✅ Delete all products endpoint
- ✅ Bean Validation for input validation
- ✅ MySQL database with Docker support
- ✅ Swagger/OpenAPI documentation
- ✅ Unit tests with 80%+ code coverage (JaCoCo)
- ✅ Java 25 with Spring Boot 3.5.7
- ✅ Lombok 1.18.40 for clean code

## Technologies

- **Java 25** (Latest LTS)
- **Spring Boot 3.5.7**
- **Spring Data JPA**
- **MySQL 8.0**
- **Docker & Docker Compose**
- **Lombok 1.18.40** (Java 25 compatible)
- **Swagger/OpenAPI (SpringDoc)**
- **JaCoCo 0.8.14** (Code Coverage)
- **JUnit 5 & Mockito** (Testing)

## Prerequisites

Make sure you have the following installed on your machine:

- Java JDK 25
- Maven 3.8+
- Docker Desktop
- Git

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd test-jav
```

### 2. Start MySQL Database

Start the MySQL database using Docker Compose:

```bash
docker-compose up -d
```

This will start a MySQL container on port `3306` with the database `product_catalog`.

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8085**

### 5. Access Swagger Documentation

Once the application is running, access the API documentation at:

**Swagger UI**: http://localhost:8085/swagger-ui.html

**OpenAPI JSON**: http://localhost:8085/api-docs

## API Endpoints

| HTTP Method | Endpoint | Description |
|-------------|----------|-------------|
| `POST` | `/products` | Create a new product |
| `PUT` | `/products/{id}` | Update an existing product |
| `GET` | `/products/{id}` | Get a product by ID |
| `GET` | `/products` | Get all products |
| `GET` | `/products/search` | Search products with filters |
| `DELETE` | `/products/{id}` | Delete a product |
| `DELETE` | `/products` | Delete all products |

### Product Model

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "price": 59.99
}
```

### Validation Rules

- `name`: Required, cannot be blank
- `description`: Required, cannot be blank
- `price`: Required, must be positive (>= 0.01)

### Examples

#### Create a Product

```bash
curl -X POST http://localhost:8085/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High performance laptop",
    "price": 1299.99
  }'
```

#### Update a Product

```bash
curl -X PUT http://localhost:8085/products/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Pro",
    "description": "Updated high performance laptop",
    "price": 1499.99
  }'
```

#### Get All Products

```bash
curl http://localhost:8085/products
```

#### Search Products

Search by query (name or description):
```bash
curl "http://localhost:8085/products/search?q=laptop"
```

Search by price range:
```bash
curl "http://localhost:8085/products/search?min_price=100&max_price=2000"
```

Combined search:
```bash
curl "http://localhost:8085/products/search?q=laptop&min_price=1000&max_price=2000"
```

#### Delete a Product

```bash
curl -X DELETE http://localhost:8085/products/{id}
```

#### Delete All Products

```bash
curl -X DELETE http://localhost:8085/products
```

## Error Responses

### Validation Error (400 Bad Request)

```json
{
  "status_code": 400,
  "message": "Price must be positive"
}
```

### Duplicate Product (400 Bad Request)

```json
{
  "status_code": 400,
  "message": "Product with name 'Laptop' and description 'High performance laptop' already exists"
}
```

### Not Found (404 Not Found)

When a product is not found, the API returns HTTP 404 with no response body.

## Running Tests

Run all tests:

```bash
mvn test
```

### Code Coverage Report

Generate JaCoCo code coverage report:

```bash
mvn clean test jacoco:report
```

The coverage report will be available at: `target/site/jacoco/index.html`

### Coverage Requirements

The project is configured to enforce a minimum of **80% code coverage**. The build will fail if coverage falls below this threshold.

To check coverage without failing the build:

```bash
mvn test jacoco:report
```

To enforce coverage rules:

```bash
mvn verify
```

## Project Structure

```
product-ms/
├── src/
│   ├── main/
│   │   ├── java/com/catalog/productms/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── exception/       # Custom exceptions and handlers
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── ProductMsApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/catalog/productms/
│       │   ├── controller/      # Controller tests
│       │   ├── repository/      # Repository tests
│       │   ├── service/         # Service tests
│       │   └── ProductMsApplicationTest.java
│       └── resources/
│           └── application-test.properties
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Database Configuration

The application uses MySQL in production and H2 (in-memory) for tests.

### MySQL Connection

- **Host**: localhost
- **Port**: 3306
- **Database**: product_catalog
- **Username**: productuser
- **Password**: productpass

You can modify these settings in `src/main/resources/application.properties`.

## Stopping the Application

1. Stop the Spring Boot application: Press `Ctrl+C` in the terminal
2. Stop the MySQL container:

```bash
docker-compose down
```

To remove the database volume as well:

```bash
docker-compose down -v
```

## Troubleshooting

### Port 8085 Already in Use

If port 8085 is already in use, you can change it in `application.properties`:

```properties
server.port=8086
```

### MySQL Connection Issues

Make sure Docker Desktop is running and the MySQL container is healthy:

```bash
docker-compose ps
```

Check MySQL logs:

```bash
docker-compose logs mysql
```

### Build Failures

Clean and rebuild:

```bash
mvn clean install -U
```

## License

This project is developed as part of a technical assessment.

---

**Happy Coding!** 🚀

