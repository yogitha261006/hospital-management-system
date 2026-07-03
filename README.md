# Hospital Patient & Staff Domain Model 🏥

A full-stack web application built to demonstrate core Object-Oriented Programming (OOP) concepts such as Encapsulation, Access Modifiers, and Controlled State Transitions.

## Technology Stack
- **Frontend**: React + Vite + Tailwind CSS
- **Backend**: Java 17 + Spring Boot + Spring Data JPA
- **Database**: MySQL

## OOP Concepts Demonstrated
- **Encapsulation**: Entities (`Patient`, `Staff`) have strictly private fields. There are no public setters that bypass state logic.
- **Controlled State Transitions**: A patient cannot be discharged without being admitted first. You cannot assign a doctor who is unavailable. These throw custom exceptions.
- **DTO Pattern**: We don't return mutable database entities directly to the API. Read-only Data Transfer Objects (DTOs) are used.

## Prerequisites
- Java 17+
- Node.js 18+
- MySQL Server running on `localhost:3306`

## Database Setup
1. Open MySQL workbench or terminal.
2. Run the SQL script from `schema.sql` at the root of this repository to create the database and tables (or allow Spring Boot's `ddl-auto=update` to create them automatically!).
3. Make sure your MySQL username is `root` with no password (if you have a password, update `backend/src/main/resources/application.properties`).

## How to Run the Backend
1. Open a terminal and navigate to the `backend` folder:
   ```bash
   cd backend
   ```
2. Run the Spring Boot application using Maven Wrapper (or your installed maven):
   ```bash
   mvn spring-boot:run
   ```
   *The backend will start on `http://localhost:8080`.*

## How to Run the Frontend
1. Open a new terminal and navigate to the `frontend` folder:
   ```bash
   cd frontend
   ```
2. Install dependencies (if you haven't already):
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
   *The frontend will start on `http://localhost:5173`. Open this URL in your browser.*

## API Documentation & Sample JSON

### 1. Patients API

#### Create Patient (`POST /patients`)
```json
{
  "name": "John Doe",
  "age": 45,
  "gender": "Male",
  "diagnosis": "Viral Fever"
}
```

#### Admit Patient (`POST /patients/{id}/admit`)
No request body needed. Changes status to `ADMITTED`.

#### Discharge Patient (`POST /patients/{id}/discharge`)
No request body needed. Changes status to `DISCHARGED`.

#### Assign Doctor (`POST /patients/{id}/assign/{staffId}`)
No request body needed.

### 2. Staff API

#### Create Staff (`POST /staff`)
```json
{
  "name": "Dr. Sarah",
  "role": "DOCTOR",
  "specialization": "Neurology"
}
```

#### Toggle Availability (`POST /staff/{id}/availability?available=false`)
No request body needed. Uses query parameter.

### 3. Dashboard API

#### Get Statistics (`GET /dashboard`)
Returns:
```json
{
  "totalPatients": 1,
  "admittedPatients": 0,
  "dischargedPatients": 1,
  "availableStaff": 3
}
```
