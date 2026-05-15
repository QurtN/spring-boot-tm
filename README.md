# Task Manager API

A RESTful Task Manager backend built with Spring Boot.

The application supports:

- task creation
- parent/subtask grouping
- automatic parent completion
- timer-based tasks
- pause/resume timer functionality
- recursive deletion
- REST API communication
- unit and controller testing

---

# Features

## Task Management

- Create tasks
- Delete tasks
- Mark tasks as completed
- Retrieve all tasks

---

## Task Grouping

Tasks can contain subtasks.

Example:

Parent task: Kochen, subtasks: Einkaufen, Geschirr waschen

The parent task is automatically marked as completed
once all subtasks are completed.

---

## Timer Functionality

Tasks may optionally contain timers.

Example:

```json
{
  "title": "Lernen",
  "durationSeconds": 3600
}
```

Supported timer operations:

- start timer
- pause timer
- resume timer
- automatic completion when duration is reached

---

# Technologies Used

## Java

Main programming language used for backend development.

---

## Spring Boot

Framework used for building the REST API and backend architecture.

Responsibilities:

- dependency injection
- REST controllers
- application configuration
- request handling
- backend architecture

---

## Spring Data JPA

Used for database persistence and repository abstraction.

Responsibilities:

- CRUD database operations
- query generation
- entity persistence





---

## H2 Database

In-memory SQL database used during development.

The database resets when the application stops.

---

## Maven

Build and dependency management tool.

Responsibilities:

- dependency management
- project build lifecycle
- plugin execution
- test execution

---

# Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.spring_boot_tm.demo
│   │       ├── controller
│   │       ├── service
│   │       ├── repository
│   │       ├── entity
│   │       └── dto
│   └── resources
│       └── application.properties
│
└── test
    └── java
        └── tests
```

---

# Architecture Overview

```text
Client
   ↓
TaskManagerController
   ↓
TaskService
   ↓
TaskManagerRepo
   ↓
H2 Database
```

---

# Communication Diagram

```text
HTTP Request
      ↓
Controller Layer
(TaskManagerController)
      ↓
Service Layer
(TaskService)
      ↓
Repository Layer
(TaskManagerRepo)
      ↓
Database Layer
(H2 Database)
```

---

# Entity Relationships

```text
TaskManager
   ├── id
   ├── title
   ├── completed
   ├── durationSeconds
   ├── elapsedSeconds
   └── parentTask
           ↓
      TaskManager
```

This allows recursive parent/subtask structures.

---

# API Endpoints

## Get all tasks

```http
GET /tasks
```

---

## Create task

```http
POST /tasks
```

Example request:

```json
{
  "title": "Lernen",
  "durationSeconds": 3600
}
```

---

## Create subtask

```json
{
  "title": "Geschirr waschen",
  "parentTaskId": 1
}
```

---

## Delete task

```http
DELETE /tasks/{id}
```

Deletes task recursively with all subtasks.

---

## Start timer

```http
POST /tasks/{id}/start
```

---

## Pause timer

```http
POST /tasks/{id}/pause
```

---

# Testing

The project contains:

- service tests
- controller tests
- repository tests

Frameworks used:

- JUnit 5
- Spring Boot Test
- MockMvc

---

# Running the Project

## Requirements

- Java JDK
- Maven
- IDE (IntelliJ or VS Code)

---

## Clone Repository

```bash
git clone <repository-url>
```

---

## Start Application

```bash
mvn spring-boot:run
```
---
## Access Tasks after start
```text
http://localhost:8080/tasks
```
---


## Access H2 Console

```text
http://localhost:8080/h2-console
```

---

## Default H2 Configuration

```text
JDBC URL:
jdbc:h2:mem:testdb
```

---

### Example API-calls in JSON format

```text
Within the requests.http are a bunch of example API calls in the form of POST, GET and DELETE
API calls. It also has JSONs which represent more complex TaskManager objects with which the
current functionality of the project can be tested and played around with.
```
---

### Documentation

```text
Within the file structure spring-boot-tm.demo/target/reports/apidocs/index.html one can find the
HTML-document of the documentation. It can be opened directly in the browser or by running it in
your desired IDE and contains the documentation of TaskService, TaskManager, TaskManagerRepo,
TaskRequest and TaskManagerController.
```
---

# Future Improvements

- Angular frontend
- authentication
- PostgreSQL integration (replaces H2 database)
- Docker deployment
- user accounts
- task priorities
- notifications

---

# Author: QubiteAlgo

Developed as a portfolio backend project
for learning Spring Boot and REST API development.