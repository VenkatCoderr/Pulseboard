# Uptime Monitor SaaS

Production-ready uptime monitor SaaS with a Spring Boot 3 backend and a React frontend.

## Stack

- Java 17+ with Spring Boot 3
- Spring Security with JWT authentication
- Spring Data JPA with PostgreSQL
- `@Scheduled` background health checks
- JavaMailSender with Gmail SMTP alerts
- React + Vite frontend

## Project Layout

```text
backend/   Spring Boot API, scheduler, persistence, security, mail
frontend/  React dashboard, auth flow, monitor detail page
```

## Backend Features  [See (docs/API.md) for full endpoint documentation] 

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/monitors`
- `POST /api/monitors`
- `DELETE /api/monitors/{id}`
- `GET /api/monitors/{id}/logs`
- `GET /api/monitors/{id}/stats`
- Scheduled HTTP GET checks every 5 minutes
- DOWN and RECOVERED email alerts
- Uptime and response-time stats from the last 30 logs

## Run The Backend

1. Update `backend/src/main/resources/application.properties` with your PostgreSQL and Gmail SMTP credentials.
2. Create a PostgreSQL database named `uptime_monitor`.
3. From `backend`, run:

```bash
mvn spring-boot:run
```

## Run The Frontend

1. Install Node.js 18+.
2. Copy `frontend/.env.example` to `frontend/.env` if you want a custom API URL.
3. From `frontend`, run:

```bash
npm install
npm run dev
```

## Notes

- The scheduler treats `2xx` and `3xx` responses as `UP`.
- First-time `UNKNOWN` status does not trigger an alert. Alerts only fire on `UP -> DOWN` and `DOWN -> UP`.
- The backend was compiled successfully with Maven in this workspace.
- The frontend could not be built here because Node.js and npm are not installed in the current environment.
