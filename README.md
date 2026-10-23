# Event Management System — Backend

Spring Boot REST API for [Gather, the standalone React frontend](https://github.com/itsmib/event-management-frontend). The UI now builds and deploys independently; this repository owns authentication, authorization, events, ticket inventory, bookings, profiles, and email workflows.

Originally built collaboratively during an internship, then maintained and refactored independently by Noorul Misbah. This split preserves that project history while replacing its embedded Thymeleaf UI.

## Architecture

```text
Browser → React frontend / reverse proxy → /api/* → Spring Boot → Oracle
                                                        └────→ SMTP
```

Both applications use one browser origin. Vite proxies `/api` during development; production uses a reverse proxy. Authentication uses an HttpOnly session cookie and Spring Security CSRF tokens, not browser-stored bearer tokens. No cross-origin CORS configuration is required for this topology.

## Features

- Public upcoming-event discovery, event details, and PNG/JPEG images.
- Member registration, session login/logout, current user, and profile editing.
- Email verification codes for password reset, with ten-minute expiry, five attempts, and a per-session request cooldown.
- Organizer-only event creation, editing, deletion, dashboard totals, reminders, and reminder history scoped to the organizer.
- Member bookings and cancellation, with transactional inventory locking and ownership checks.
- Booking confirmation emails after transaction commit. Mail failures are logged without turning a successful reservation into an apparent failure.
- Explicit response DTOs exclude passwords and entity relationships.

Payment processing is not implemented. `totalTickets` means remaining available inventory; editing an event does not reset that inventory. Deleting an event also deletes its bookings and reminder records.

## Requirements

Java 21 and Maven 3.6+ (or the included Maven wrapper); Node.js 22.12+ for the separate frontend. Oracle is used for persistent deployments. H2 is provided only for disposable local development and tests.

## Run both projects locally

```sh
git clone https://github.com/itsmib/event-management-system.git
cd event-management-system
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The backend listens on `http://localhost:8083`. The `local` profile creates an empty, in-memory database, which is discarded when the process stops. There is no backend home page: open the frontend.

In a second terminal:

```sh
git clone https://github.com/itsmib/event-management-frontend.git
cd event-management-frontend
npm ci
npm run dev
```

Open `http://localhost:5173`. The frontend's default API proxy points to port 8083.

### Optional local demo accounts and events

Explicitly enable the fixtures and supply a development-only password:

```sh
APP_DEMO_PASSWORD='choose-a-local-password' ./mvnw spring-boot:run   -Dspring-boot.run.profiles=local   -Dspring-boot.run.arguments='--app.demo-data=true'
```

Sign in as `organizer@example.test` or `member@example.test` using that password. Three illustrative events are added. Fixtures run only with the `local` profile and only when the users table is empty. Never use the local profile against persistent data.

Public registration always creates `USER` accounts. For a real organizer account, register normally and have the database administrator promote that specific account's `users.role` to `ADMIN`, then sign out and back in. Roles cannot be chosen through the public API.

### Email

The default SMTP destination is localhost:1025, suitable for a local mail-capture service. Without an SMTP server, recovery and reminder requests return a service-unavailable error; bookings still commit and confirmation delivery failures are logged. Use a local SMTP capture service when testing email; do not use real recipients in fixtures.

## Persistent configuration

Set environment variables in the deployment environment; Spring Boot does not automatically load a `.env` file.

| Variable | Purpose / default |
| --- | --- |
| `PORT` | HTTP port, `8083` |
| `DB_URL` | Oracle JDBC URL, defaults to local XE |
| `DB_USERNAME`, `DB_PASSWORD` | Database credentials |
| `DDL_AUTO` | Schema handling, `validate` |
| `MAIL_HOST`, `MAIL_PORT` | SMTP endpoint, `localhost:1025` |
| `MAIL_USERNAME`, `MAIL_PASSWORD` | SMTP credentials, empty by default |
| `MAIL_AUTH`, `MAIL_STARTTLS` | SMTP authentication/TLS, `false` by default |
| `COOKIE_SECURE` | Set `true` behind production HTTPS |

Credentials are never committed. Configure SMTP according to your provider. Session timeout is 30 minutes; cookies are HttpOnly and SameSite=Lax. Keep the backend behind the same HTTPS origin as the frontend and configure forwarded headers only for trusted proxies.

Before upgrading an existing Oracle database, apply [the version-column migration](docs/migrations/001-event-version.sql). The SQL is a manual migration, not an automatically executed script. Back up and validate it against your schema first. A new empty development Oracle schema may be initialized once with `DDL_AUTO=update`; managed production schemas should use reviewed migrations and `validate`.

## API and migration guide

See [API.md](docs/API.md) for methods, payloads, roles, and session handling, and [MIGRATION.md](docs/MIGRATION.md) for coordinated rollout and rollback.

## Build and test

```sh
./mvnw clean verify
java -jar target/EventManagementSystem-0.0.1-SNAPSHOT.jar
```

Tests run against H2 and mock SMTP. They cover CSRF and session rotation, role escalation prevention, profile password preservation, booking validation/ownership, concurrent bookings and cancellations, password-reset reuse, and organizer scope, alongside existing model/service tests. CI uses Java 21. Oracle integration and real SMTP delivery need environment-specific validation before deployment.

## Source map

```text
src/main/java/com/cts/EventManagementSystem/
  config/       Security filter chain and opt-in local fixtures
  controller/   REST endpoints, DTOs, and API errors
  service/      Business logic, booking transactions, and email delivery
  repository/   JPA persistence and locking queries
  model/        User, event, booking, and notification entities
src/main/resources/  Environment-based configuration and local profile
src/test/            Unit and integration tests
```

## Operational limits

Sessions and reset challenges are process-local; use sticky sessions or shared session storage before scaling across instances. Reset-request throttling is per session; production deployments should add per-account/IP rate limits at the edge. Resetting a password does not revoke other existing sessions. Email delivery is synchronous and has no durable retry queue; reminder batches may partially deliver if SMTP fails. Events use local dates/times without a stored timezone; run the backend in the event venue's timezone and communicate that convention to attendees. Upcoming discovery includes today's events; booking rejects events whose start time has passed.

## Contributors

- [Noorul Misbah](https://github.com/itsmib)
- [Meghraj Yelwande](https://github.com/meghraj-yelwande)
- [Sameed Irfan](https://github.com/SameedIrfan7)
- [Mithilesh](https://github.com/mithileshangu)

## Usage

This project is for learning and portfolio purposes. Please request permission before commercial use; no open-source license is granted by this refactor.
