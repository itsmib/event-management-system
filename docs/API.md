# Frontend API contract

Base path: `/api`. JSON responses use DTOs; the error body generally includes `message`. Framework-level invalid parameter errors may use the default Spring error shape; clients must provide a fallback message. `401` means unauthenticated, `403` means forbidden or invalid CSRF, `409` means inventory/record conflict. Successful delete/logout operations return `204` without a body.

## Session handshake

1. `GET /auth/csrf` → `{ "token": "...", "headerName": "X-CSRF-TOKEN" }`. Keep the session cookie returned by the server.
2. Include the returned header/token on every POST, PUT, and DELETE, including registration, login, reset, and logout.
3. `POST /auth/login` with URL-encoded `username` (email) and `password` → `204` on success or `401` on failure. Spring rotates the session ID.
4. `GET /auth/me` → current profile. Fetch a new CSRF token after login/logout; the frontend does so before every mutation.
5. `POST /auth/logout` invalidates the session → `204`.

All requests use the same origin and browser cookies. Do not disable CSRF to connect the frontend. Do not retry mutations automatically after a transport failure: the server may already have committed them.

## Endpoints

| Method | Path | Access | Input / response |
| --- | --- | --- | --- |
| GET | `/auth/csrf` | Public | Token + header name |
| POST | `/auth/register` | Public + CSRF | JSON `name`, `email`, `password`, optional `contactNumber`; `201` profile with role USER |
| POST | `/auth/login` | Public + CSRF | URL-encoded credentials; `204` |
| POST | `/auth/logout` | CSRF | `204` |
| GET | `/auth/me` | Signed in | Profile |
| PUT | `/profile` | Signed in + CSRF | JSON `name`, optional `contactNumber`; updated profile |
| POST | `/auth/password/request` | Public + CSRF | JSON `email`; `202` whether the account exists or not |
| POST | `/auth/password/confirm` | Same reset session + CSRF | JSON `otp`, `newPassword`; `204` |
| GET | `/events/upcoming` | Public | Array of events, today and future dates |
| GET | `/events/{id}` | Public | Event or `404` |
| GET | `/events/{id}/image` | Public | PNG/JPEG bytes or `404` |
| POST | `/events` | ADMIN + CSRF | Multipart fields below; `201` event |
| PUT | `/events/{id}` | Owning ADMIN + CSRF | Multipart fields below; updated event |
| DELETE | `/events/{id}` | Owning ADMIN + CSRF | Deletes associated bookings/reminders; `204` |
| GET | `/bookings` | USER | Current user's bookings |
| POST | `/bookings` | USER + CSRF | JSON `eventId`, positive integer `ticketCount`; `201` booking |
| DELETE | `/bookings/{id}` | Owning USER + CSRF | Restore tickets and delete booking; `204` |
| GET | `/admin/events` | ADMIN | Own events, including past events |
| GET | `/admin/summary` | ADMIN | Own `events`, `bookings`, `tickets` counts |
| POST | `/admin/events/{id}/reminders` | Owning ADMIN + CSRF | Send booked-attendee emails; `204` |
| GET | `/admin/reminders` | ADMIN | Own reminder history |

Profile: `userId`, `name`, `email`, `contactNumber`, `role`. Email and role are immutable through profile editing. Password length for registration/reset is 8–72 characters; names max 100, contacts max 30.

Event: `eventId`, `name`, `category`, `location`, `eventDate` (`YYYY-MM-DD`), `eventTime` (`HH:mm:ss`), `description`, `totalTickets` (remaining), `organizerName`, `hasImage`.

Create/edit multipart fields: `name` (required, max 100), `category` (required, max 60), `location` (required, max 200), `eventDate`, `eventTime`, optional `description` (max 255), optional `imageFile` (PNG/JPEG, max 5 MB). Creation additionally requires nonnegative integer `totalTickets`. Editing deliberately ignores inventory submitted by the client. Omit `imageFile` to preserve the image. There is no remove-image operation.

Booking: `bookingId`, `bookingDate`, `ticketCount`, nested `event` DTO. The server checks event start time and availability under a database lock. Booking list order is not guaranteed.

Reminder: `id`, `eventName`, `message`, `sentAt`. One history row is stored per successfully processed recipient. A failed email batch can partially deliver; review before retrying.

The UI treats the dates/times as venue-local display values. There is no timezone conversion or payment API.
