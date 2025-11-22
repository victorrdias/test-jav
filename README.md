# Product Catalog Microservice

A REST API microservice for managing a product catalog, built with Java 25 and Spring Boot 3.5.

## Features

- ✅ Full CRUD operations for products
- ✅ Search and filter products by name, description, and price range
- ✅ **Pagination support** for large datasets (GET /products and /products/search)
- ✅ Duplicate product validation (name + description)
- ✅ Delete all products endpoint
- ✅ **Optimized database queries** (N+1 query prevention)
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

| HTTP Method | Endpoint           | Description                  |
| ----------- | ------------------ | ---------------------------- |
| `POST`      | `/products`        | Create a new product         |
| `PUT`       | `/products/{id}`   | Update an existing product   |
| `GET`       | `/products/{id}`   | Get a product by ID          |
| `GET`       | `/products`        | Get all products             |
| `GET`       | `/products/search` | Search products with filters |
| `DELETE`    | `/products/{id}`   | Delete a product             |
| `DELETE`    | `/products`        | Delete all products          |

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
- `price`: Required, must be positive (>= 0.01), maximum 99,999,999.99 (8 digits before decimal, 2 after)

---

## 🚨 Error Handling

The API uses standardized error responses for all error scenarios. All errors (except 404 for product not found) return a JSON response with the following format:

```json
{
  "status_code": 400,
  "message": "Error description"
}
```

### HTTP Status Codes

| Status Code                    | Description          | When it occurs                                      |
| ------------------------------ | -------------------- | --------------------------------------------------- |
| **200 OK**                     | Success              | GET, PUT, DELETE operations successful              |
| **201 Created**                | Resource created     | POST successful                                     |
| **204 No Content**             | Success with no body | DELETE all products                                 |
| **400 Bad Request**            | Invalid input        | Validation errors, malformed JSON, wrong data types |
| **404 Not Found**              | Resource not found   | Product ID doesn't exist or invalid endpoint        |
| **405 Method Not Allowed**     | Wrong HTTP method    | Using unsupported HTTP method (e.g., PATCH)         |
| **415 Unsupported Media Type** | Wrong content type   | Using content type other than `application/json`    |
| **500 Internal Server Error**  | Server error         | Unexpected server-side errors                       |

---

### Error Scenarios

#### 1. Validation Errors (400 Bad Request)

**Scenario:** Missing required fields, invalid data types, or constraint violations.

**Examples:**

- Missing `name`, `description`, or `price`
- Empty string for required fields
- Negative or zero price
- Price exceeding maximum (99,999,999.99)
- Duplicate product (same name and description)

**Request:**

```bash
POST /products
{
  "name": "",
  "description": "Test",
  "price": -10
}
```

**Response:**

```json
{
  "status_code": 400,
  "message": "Name is required, Price must be positive"
}
```

**Price Too Large Example:**

```bash
POST /products
{
  "name": "Expensive Item",
  "description": "Very expensive",
  "price": 999999999.99
}
```

**Response:**

```json
{
  "status_code": 400,
  "message": "Price must not exceed 99999999.99 (max 8 digits before decimal, 2 after)"
}
```

---

#### 2. Product Not Found (404 Not Found)

**Scenario:** Attempting to GET, PUT, or DELETE a product that doesn't exist.

**Request:**

```bash
GET /products/invalid-id-123
```

**Response:**

```
HTTP 404 Not Found
(no response body)
```

> **Note:** 404 errors for products return **no body** - only the HTTP status code.

---

#### 3. Malformed JSON (400 Bad Request)

**Scenario:** Sending invalid JSON syntax or wrong data types in request body.

**Examples:**

- Missing quotes, commas, or braces
- Sending string instead of number for price
- Invalid JSON structure

**Request:**

```bash
POST /products
{
  "name": "Product",
  "price": INVALID_JSON
}
```

**Response:**

```json
{
  "status_code": 400,
  "message": "Invalid request format. Please check your JSON structure and data types."
}
```

---

#### 4. Wrong Parameter Type (400 Bad Request)

**Scenario:** Sending wrong data type for query parameters (e.g., text instead of number for page/size).

**Request:**

```bash
GET /products?page=abc&size=xyz
```

**Response:**

```json
{
  "status_code": 400,
  "message": "Invalid parameter type for 'page'. Expected type: Integer"
}
```

---

#### 5. Wrong Endpoint (404 Not Found)

**Scenario:** Accessing an endpoint that doesn't exist.

**Request:**

```bash
GET /products/search/invalid-endpoint
```

**Response:**

```json
{
  "status_code": 404,
  "message": "The requested endpoint does not exist"
}
```

---

#### 6. Wrong HTTP Method (405 Method Not Allowed)

**Scenario:** Using an HTTP method not supported by the endpoint.

**Request:**

```bash
PATCH /products/123
```

**Response:**

```json
{
  "status_code": 405,
  "message": "HTTP method PATCH is not supported for this endpoint"
}
```

---

#### 7. Unsupported Content Type (415 Unsupported Media Type)

**Scenario:** Sending request with wrong `Content-Type` header.

**Request:**

```bash
POST /products
Content-Type: text/xml
```

**Response:**

```json
{
  "status_code": 415,
  "message": "Unsupported media type. Please use 'application/json'"
}
```

---

#### 8. Database Constraint Violation (400 Bad Request)

**Scenario:** Violating database constraints (e.g., duplicate unique fields).

**Response:**

```json
{
  "status_code": 400,
  "message": "Data integrity violation. Please check your input data."
}
```

---

#### 9. Server Error (500 Internal Server Error)

**Scenario:** Unexpected server-side errors.

**Response:**

```json
{
  "status_code": 500,
  "message": "An unexpected error occurred"
}
```

> **Note:** Internal error details are **never exposed** to clients for security reasons.

---

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

#### Pagination Support

Both `GET /products` and `GET /products/search` support pagination to efficiently handle large datasets.

**Query Parameters:**

- `page`: Page number (0-based, default: 0)
- `size`: Page size (default: 20, max: 100)

**Example - Paginated Product List:**

```bash
# Get first page with 10 items
curl "http://localhost:8085/products?page=0&size=10"

# Get second page with 10 items
curl "http://localhost:8085/products?page=1&size=10"
```

**Example - Paginated Search:**

```bash
# Search with pagination
curl "http://localhost:8085/products/search?q=laptop&page=0&size=5"

# Search by price range with pagination
curl "http://localhost:8085/products/search?min_price=100&max_price=1000&page=0&size=10"
```

**Paginated Response Format:**

```json
{
  "content": [
    {
      "id": "123",
      "name": "Product Name",
      "description": "Product Description",
      "price": 99.99
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 50,
  "totalPages": 5,
  "isFirst": true,
  "isLast": false,
  "hasNext": true,
  "hasPrevious": false
}
```

**Benefits:**

- ✅ Prevents memory issues with large datasets
- ✅ Faster response times
- ✅ Better user experience
- ✅ Backward compatible (works without pagination params)

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

## Security

This application implements several security best practices:

### ✅ Implemented Security Measures

#### 1. **SQL Injection Protection**

- Uses **JPA with parameterized queries** (`@Param`)
- All database queries use prepared statements
- No raw SQL string concatenation

#### 2. **Input Validation**

- **Bean Validation** on all input data
- `@NotBlank` validation for required text fields
- `@DecimalMin` validation for positive prices
- Type-safe data structures (BigDecimal for money)

#### 3. **Mass Assignment Protection**

- Uses **DTOs** (Data Transfer Objects) instead of exposing entities
- Entity IDs are server-generated, not user-provided
- Clear separation between API models and database entities

#### 4. **Error Information Disclosure Prevention**

- Generic error messages for unexpected errors
- No stack traces exposed to clients
- Structured error responses with appropriate HTTP status codes

#### 5. **XSS (Cross-Site Scripting) Protection**

- Spring Boot's Jackson automatically escapes JSON output
- Content-Type headers properly set

#### 6. **Data Integrity**

- Database constraints (NOT NULL, precision, length limits)
- Transactional operations for data consistency
- Duplicate product validation

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
