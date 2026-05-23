# =============================================================================
#  Master API Test Automation Framework
#  Production-grade · REST Assured · TestNG · Allure · Java 17
# =============================================================================

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-3.9%2B-blue?logo=apache-maven)
![REST%20Assured](https://img.shields.io/badge/REST%20Assured-5.4.0-green)
![TestNG](https://img.shields.io/badge/TestNG-7.9-yellow)
![Allure](https://img.shields.io/badge/Allure-2.27-blueviolet)
![CI](https://img.shields.io/badge/CI-GitHub%20Actions%20%7C%20GitLab%20CI%20%7C%20Jenkins-informational)

</div>

## Overview

A **production-grade API Test Automation Framework** built with Java 17 and REST Assured
that demonstrates clean SOLID architecture, parallel test execution, and enterprise-level
reporting — designed as an open-source portfolio project.

---

## Tech Stack

| Concern | Technology |
|---|---|
| Language | Java 17 (LTS) |
| Build Tool | Apache Maven 3.9+ |
| Test Runner | TestNG 7.9 (parallel, data-driven) |
| API Client | REST Assured 5.4 (BDD Given/When/Then) |
| Reporting | Allure Report 2.27 |
| Serialization | Jackson Databind 2.17 |
| Boilerplate Reduction | Lombok 1.18 |
| Logging | Log4j2 2.23 + SLF4J bridge |
| Assertions | AssertJ 3.25 |
| Schema Validation | JSON Schema Validator (REST Assured) + Java `javax.xml.validation` |

---

## Directory Structure

```
MasterApiTestAutomationFramework/
├── .github/
│   └── workflows/
│       └── api-tests.yml            # GitHub Actions CI/CD pipeline
├── .gitlab-ci.yml                   # GitLab CI/CD pipeline
├── Jenkinsfile                      # Jenkins Declarative Pipeline
├── testng.xml                       # TestNG suite — parallel execution config
├── pom.xml                          # Maven build descriptor
│
└── src/
    ├── main/
    │   ├── java/com/api/framework/
    │   │   ├── clients/
    │   │   │   ├── BaseApiClient.java       # Abstract base; wires AllureRestAssured filter
    │   │   │   ├── UserApiClient.java       # Reqres.in REST API client
    │   │   │   └── SoapApiClient.java       # NumberConversion SOAP client
    │   │   ├── config/
    │   │   │   └── ConfigManager.java       # Thread-safe singleton; reads config.properties
    │   │   ├── models/
    │   │   │   ├── request/
    │   │   │   │   └── CreateUserRequest.java
    │   │   │   └── response/
    │   │   │       ├── CreateUserResponse.java
    │   │   │       ├── ListUsersResponse.java
    │   │   │       └── UserData.java
    │   │   └── utils/
    │   │       ├── AllureAttachmentUtil.java  # Manual Allure attachment helpers
    │   │       └── XmlSchemaValidator.java   # XSD validation + SOAP body extractor
    │   └── resources/
    │       └── log4j2.xml                    # Log4j2 configuration
    │
    └── test/
        ├── java/com/api/framework/
        │   ├── base/
        │   │   └── BaseTest.java             # @BeforeSuite; global REST Assured config
        │   ├── listeners/
        │   │   └── TestNGListener.java       # ITestListener — structured lifecycle logging
        │   └── tests/
        │       ├── JsonValidationTest.java   # Reqres.in REST tests + JSON Schema validation
        │       └── XmlValidationTest.java    # SOAP tests + XSD validation
        └── resources/
            ├── config.properties             # Runtime configuration (base URIs, timeouts)
            ├── allure.properties             # Allure results directory override
            └── schemas/
                ├── list-users-response-schema.json    # JSON Schema for GET /api/users
                ├── create-user-response-schema.json   # JSON Schema for POST /api/users
                └── number-to-words-response.xsd       # XSD for SOAP NumberToWords response
```

---

## Target APIs

### 1. RESTful JSON — [Reqres.in](https://reqres.in/)
| Test | Method | Endpoint | Validates |
|---|---|---|---|
| `listUsers_shouldReturn200AndMatchSchema` | GET | `/api/users?page=2` | HTTP 200 + JSON Schema |
| `createUser_shouldReturn201AndMatchSchema` | POST | `/api/users` | HTTP 201 + JSON Schema |
| `getUserById_shouldReturn200WithCorrectUser` | GET | `/api/users/2` | HTTP 200 + field values |
| `getUserById_nonExistent_shouldReturn404` | GET | `/api/users/999` | HTTP 404 |
| `deleteUser_shouldReturn204` | DELETE | `/api/users/2` | HTTP 204 + empty body |

### 2. SOAP/XML — [DataAccess NumberConversion](https://www.dataaccess.com/webservicesserver/numberconversion.wso)
| Test | Operation | Validates |
|---|---|---|
| `numberToWords_shouldReturnCorrectWordRepresentation` ×4 | NumberToWords | HTTP 200 + XSD + word content |
| `numberToWords_withZero_shouldReturnNonBlankResult` | NumberToWords | HTTP 200 + XSD |

---

## Running Tests

### Prerequisites
- JDK 17+
- Maven 3.9+

### Run all tests
```bash
mvn test
```

### Run with custom thread count
```bash
mvn test -Dsurefire.threadCount=8
```

### Run and generate Allure report
```bash
mvn test
mvn allure:serve          # Opens a live report in your browser
# OR
mvn allure:report         # Generates static HTML to target/allure-report/
```

### Override target base URI (CI/CD pattern)
```bash
mvn test -Dbase.uri.reqres=https://staging.reqres.in
```

---

## CI/CD Integration

| Platform | Configuration File | Report |
|---|---|---|
| **GitHub Actions** | `.github/workflows/api-tests.yml` | Published to GitHub Pages |
| **GitLab CI** | `.gitlab-ci.yml` | Exposed as pipeline artifact + GitLab Pages |
| **Jenkins** | `Jenkinsfile` | Allure Jenkins Plugin integration |

---

## Key Design Decisions

| Decision | Rationale |
|---|---|
| `ConfigManager` singleton with env-var override | Keeps secrets out of code; CI/CD can inject via environment |
| `BaseApiClient` with `AllureRestAssured` filter per-client | Allure attachments are scoped to API calls only; avoids noise from health checks |
| `XmlSchemaValidator.extractSoapBodyContent()` | Validates service logic (the response element) independently of SOAP boilerplate |
| AssertJ over TestNG native asserts | Fluent, readable failure messages that include context automatically |
| `@BeforeClass` for client instantiation | Thread-safe in parallel mode; TestNG creates one instance per class |

---

## Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature`.
3. Follow the existing package structure and code style.
4. Ensure `mvn test` passes locally before submitting a PR.
5. Open a pull request against `develop`.

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

