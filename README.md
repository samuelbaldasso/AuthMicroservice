# MyBankAuth Microservice

This project is the authentication and authorization microservice extracted from the MyBank modular app. It is built using Spring Boot 3 and Spring Security 6 with JWT token support.

## 🚀 Technologies Used

* Java
* Maven
* Spring Boot
* Spring Security
* JWT Tokens
* Docker

## 📁 Project Structure

The project follows the standard Maven Java project structure:

```
MyBankAuthMicroservice/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── sbaldasso/
│   │               └── mybank/
│   │                   └── authmicroservice/
│   │                       └── [Java classes]
│   └── resources/
│       └── application.properties
├── Dockerfile
├── pom.xml
└── README.md
```

## ⚙️ How to Run

1. Build the project:

   ```bash
   ./mvnw clean install
   ```

2. Run the microservice:

   ```bash
   ./mvnw spring-boot:run
   ```

3. Alternatively, build and run the Docker container:

   ```bash
   docker build -t auth-microservice .
   docker run -p 8081:8081 auth-microservice
   ```

## 🧪 Testing

Run automated tests with:

```bash
./mvnw test
```

## 📄 License

This project is licensed under the MIT License.
