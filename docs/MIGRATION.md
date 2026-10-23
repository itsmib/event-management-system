# Moving from the embedded UI

This is a coordinated, breaking frontend/backend release. Review and merge both repository PRs before deployment. Do not deploy the backend alone to an environment still serving Thymeleaf pages.

1. Back up the database and retain the previous application artifact/configuration for rollback.
2. Apply `docs/migrations/001-event-version.sql` to existing Oracle schemas. The new optimistic-lock version protects inventory from stale event edits. New H2 local databases are created automatically.
3. Set DB/SMTP environment variables. The application no longer embeds credentials and now defaults to `DDL_AUTO=validate`.
4. Build the backend with Java 21; build the frontend with Node.js 22.12+ and `npm ci && npm run build`.
5. Serve the frontend `dist/` and proxy `/api/` to the backend from the same HTTPS origin. Keep `/api` errors out of SPA fallback handling. See the frontend's `deploy/nginx.conf` example.
6. Set `COOKIE_SECURE=true`. Drain old instances; users should sign in again after the cutover.
7. Verify public discovery, both account roles, image uploads, booking/cancellation, profile updates, recovery email, and reminders against staging Oracle/SMTP.

## Route changes

| Old embedded page | New frontend route |
| --- | --- |
| `/`, `/home` | `/` |
| `/login`, `/register` | `/login`, `/register` |
| `/forgot-password`, `/enter-otp` | `/reset-password` |
| `/user/dashboard`, `/user/events` | `/` |
| `/user/event-details/{id}` | `/events/{id}` |
| `/user/bookings` | `/bookings` |
| `/user/edit_profile`, `/admin/edit_profile` | `/profile` |
| `/admin/dashboard` | `/admin` |
| `/admin/my-events` | `/admin/events` |
| `/admin/create-event` | `/admin/events/new` |
| `/admin/edit-event/{id}` | `/admin/events/{id}/edit` |
| `/admin/reminder` | `/admin/reminders` |

Old HTML/form endpoints, templates, static UI assets, and Thymeleaf dependencies are removed. The backend denies non-API routes. Existing `/api/events` create/edit semantics remain multipart; images now use `/api/events/{id}/image`. The reverse proxy may add explicit old-URL redirects if existing bookmarks must be preserved.

## Rollback

Roll back both frontend routing and the backend artifact together. The additive `version` column can remain when running the old backend; do not remove it while new instances are live. Restore the previous environment configuration and route setup. Application rollback does not restore deleted events/bookings; data restoration requires your database backup procedure.
