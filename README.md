# Spring Boot Student Management API

A RESTful Student Management API built with Java and Spring Boot. This project demonstrates backend development concepts including REST API design, database integration, CRUD operations, validation, exception handling, pagination, sorting, and automated testing.

The application uses Spring Data JPA and MySQL for data persistence, with H2 as the in-memory database for automated tests.

## Technologies Used

- **Java 17** – Programming language
- **Spring Boot 4.1.1** – Application framework
- **Spring Data JPA** – Database interaction and persistence
- **Hibernate** – ORM (Object-Relational Mapping)
- **MySQL 9.1** – Main application database
- **H2 Database** – In-memory database for automated tests
- **Maven** – Dependency management and build automation
- **JUnit** – Automated testing
- **IntelliJ IDEA Community Edition** – Development environment
- **Postman / HTTP Client** – API testing

## Application Architecture

The application follows a layered architecture that separates responsibilities:

- **Controller Layer (`StudentController`)** – Receives HTTP requests, delegates operations to the service layer, and returns HTTP responses.
- **Service Layer (`StudentService`)** – Contains application and business logic, coordinates operations, and communicates with the repository layer.
- **Repository Layer (`StudentRepository`)** – Uses Spring Data JPA to perform database operations.

This separation improves code organisation, maintainability, and testability.

## Features

- Create new student records
- Retrieve all students
- Retrieve a student by ID
- Update student records using PUT
- Partially update student records using PATCH
- Delete student records
- Validate incoming request data
- Handle exceptions centrally with meaningful HTTP responses
- Retrieve students using pagination
- Sort student records by selected fields
- Run automated tests using an in-memory H2 database

## API Endpoints

The API runs locally at `http://localhost:8081`.

| HTTP Method | Endpoint | Description |
|---|---|---|
| POST | `/students` | Create a new student |
| GET | `/students` | Retrieve all students with pagination and sorting |
| GET | `/students/{id}` | Retrieve a student by ID |
| GET | `/students/{id}/summary` | Retrieve a student's name and course |
| PUT | `/students/{id}` | Update all student fields |
| PATCH | `/students/{id}` | Partially update student fields |
| DELETE | `/students/{id}` | Delete a student by ID |

### Pagination and Sorting

The `GET /students` endpoint supports the following query parameters:

| Parameter | Default | Description |
|---|---|---|
| `page` | `0` | Page number (starts at 0) |
| `size` | `3` | Number of students per page |
| `sortBy` | `name` | Field used for sorting (`id`, `name`, `course`, or `level`) |
| `direction` | `asc` | Sort direction (`asc` or `desc`) |

**Example requests:**

```http
GET http://localhost:8081/students?page=0&size=3
GET http://localhost:8081/students?sortBy=course&direction=desc
GET http://localhost:8081/students?page=1&size=3&sortBy=name&direction=asc
```

## Example API Requests

### 1. Create a Student

**POST** `/students`

Request body:

```json
{
  "name": "Jane",
  "course": "Java",
  "level": "Beginner"
}
```

### 2. Retrieve All Students

**GET** `/students`

### 3. Retrieve a Student by ID

**GET** `/students/1`

### 4. Update a Student

**PUT** `/students/1`

Request body:

```json
{
  "name": "Jane",
  "course": "Spring Boot",
  "level": "Intermediate"
}
```

### 5. Partially Update a Student

**PATCH** `/students/1`

Request body (only include the fields you want to change):

```json
{
  "course": "Microservices"
}
```

### 6. Delete a Student

**DELETE** `/students/1`

## Database Configuration

This project uses two databases:

- **MySQL:** The primary database used by the application to persist student records.
- **H2 Database:** An in-memory database used during automated testing to keep test data separate from the main application database.

### MySQL Setup

1. Install and start MySQL.
2. Create the application database:

```sql
CREATE DATABASE student_management_db;
```

3. Configure the database connection using the following environment variables:

| Variable | Default value |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/student_management_db` |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | Empty |

The application supports these defaults for local development. If your MySQL setup uses different credentials or a different database URL, configure the corresponding environment variables.

**Important:** Do not commit actual database passwords or other sensitive credentials to GitHub.

### Automated Testing

Automated tests use an H2 in-memory database configured separately from MySQL. This allows tests to run without modifying the main application database.

## Running the Application

### Prerequisites

Make sure you have the following installed:

- Java Development Kit (JDK) 17
- MySQL
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/ChristyAoko/springboot-masterclass.git
```

### 2. Navigate to the Project Directory

```bash
cd springboot-masterclass
```

### 3. Create the MySQL Database

```sql
CREATE DATABASE student_management_db;
```

### 4. Configure Database Credentials

The application uses the default local MySQL configuration unless environment variables are provided.

Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` if your database configuration differs from the defaults.

### 5. Run the Application

On Windows, using PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, open the project in IntelliJ IDEA and run `SpringbootMasterclassApplication`.

Once the application starts, the API is available at:

`http://localhost:8081`

## Running Automated Tests

The project includes automated tests to verify API functionality, input validation, exception handling, pagination, sorting, and database operations.

Tests use an H2 in-memory database instead of the main MySQL database.

### Run Tests

On Windows, open PowerShell in the project directory and execute:

```powershell
.\mvnw.cmd clean test
```

Maven will:

1. Clean previously generated build files.
2. Compile the application and test classes.
3. Execute the automated tests.
4. Generate a test report.

A successful execution displays:

```text
BUILD SUCCESS
```

Test reports are generated in:

`target/surefire-reports/`

## Project Structure

```text
springboot-masterclass/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/christy/springboot_masterclass/
│   │   │       ├── Student.java
                ├── StudentController.java
                ├── StudentService.java
                ├── StudentRepository.java
│   │   │       ├── StudentRequestDTO.java
│   │   │       ├── StudentUpdateDTO.java
│   │   │       ├── StudentPatchDTO.java
│   │   │       ├── StudentSummaryDTO.java
│   │   │       ├── StudentNotFoundException.java
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── SpringbootMasterclassApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       ├── java/
│       │   └── com/christy/springboot_masterclass/
│       │       └── SpringbootMasterclassApplicationTests.java
│       │
│       └── resources/
│           └── application.properties
│
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

**Note:** The structure above highlights the main application and test files.