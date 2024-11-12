# Employee Management API

This Employee Management API is a Spring Boot application for managing employee records.
## Features

- **CRUD Operations**: Create, read, update, delete employees
- **Third-Party API Integration**:
  - **Email Validation**: Validates employee emails via an external API(mocked)
  - **Department Validation**: Confirms valid departments via a mock API
- **Email Notification**: Sends email notifications upon employee creation and update
- **Exception Handling**: Custom exception handling with appropriate HTTP responses
- **Docker Support**: Docker configuration provided for containerization
- **Rate Limiting and Circuit Breaker**: Applied to handle third-party API calls (used ->reseliance)
- **Audit Logging**: Tracks each step of employee creation for traceability
- **Testing**: Unit and integration tests with JUnit and MockMvc

## Prerequisites

- **Java 17 or higher**
- **Maven**: For building the project
- **Docker** (optional): For containerization
- **SMTP Account**: Required for email notifications (e.g., Gmail with App Password)

## Setup and Configuration

1. **Clone the Repository**
    ```bash
    git clone https://github.com/your-username/employee-management-api.git
    cd employee-management-api
    ```

2. configurations:
	**Configure SMTP in `application.properties`**
	   Update `src/main/resources/application.properties` with your SMTP details:
		```properties
		spring.mail.host=smtp.gmail.com
		spring.mail.port=587
		spring.mail.username=your_email@gmail.com
		spring.mail.password=your_app_password
		spring.mail.properties.mail.smtp.auth=true
		spring.mail.properties.mail.smtp.starttls.enable=true
		```
	**Configure rateLimitting in `application.properties`**
	   Update `src/main/resources/application.properties` with your rateLimitting details:
		```properties
		resilience4j.ratelimiter.instances.validationService.limitForPeriod=10
		resilience4j.ratelimiter.instances.validationService.limitRefreshPeriod=1s
		resilience4j.ratelimiter.instances.validationService.timeoutDuration=500ms
		```
	**Configure circuit breaker in `application.properties`**
	   Update `src/main/resources/application.properties` with your circuit breaker details:
		```properties
		resilience4j.circuitbreaker.instances.validationService.failureRateThreshold=50
		resilience4j.circuitbreaker.instances.validationService.waitDurationInOpenState=10000
		```

3. **Run the Application**
   Start the application with:
    ```bash
    mvn spring-boot:run
    ```

4. **Docker (Optional)**
   Build and run the application with Docker:
    a-employee-managment service:
		```bash
		docker build -t employee-management .
		docker run -p 8080:8080 employee-management
		```
	b-validation service:
		```bash
		docker build -t service-validation .
		docker run -p 8081:8081 service-validation
		```
	notes: run the commands from the directory containing the dockerfile and remember to pick two different ports.
## API Endpoints

### Base URL
The API is accessible at `http://localhost:8080/api/employees`.

### Endpoints

| Method | Endpoint              | Description                          |
|--------|------------------------|--------------------------------------|
| POST   | `/`                   | Create a new employee                |
| GET    | `/{id}`               | Retrieve an employee by ID           |
| PUT    | `/{id}`               | Update an employee's details         |
| DELETE | `/{id}`               | Delete an employee                   |
| GET    | `/`                   | List all employees                   |

### Sample API Request/Response

**Create Employee**
```http
POST /api/employees
Content-Type: application/json
{
    "firstName": "mahmoud",
    "lastName": "saleh",
    "email": "mahmoud0saleh94@gmail.com",
    "department": "Sales",
    "salary": 63000
}

### Note:
 -make sue that the validation service is running before running the employee Management service.
 -the dockerfile is under : /src/main/resources
 -the postman collection is root level of the repo
**improvments:
	1- instead of running two dockerfiles separatly I could have used docker compose.
	2- improving the code with more exception and covering more cases.
	3- better test coverage.
	4- using an actual thirdparty API for validation(I did wrote the code but it did not work and needed debuging and the time was up).
	5- deployment on cloud.
