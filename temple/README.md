# Sri Sri Baba Dhabaleswar Mandira – Spring Boot backend

Spring Boot 3.3 · Java 17 · Spring Data JPA · Spring Security · Thymeleaf · MySQL (or H2 for a quick test)

## Open in Spring Tool Suite
1. **File → Import → Maven → Existing Maven Projects** → choose this folder → Finish.
2. Wait for Maven to download dependencies (needs Java 17+).
3. Create the database (MySQL):
   ```sql
   CREATE DATABASE dhabaleswara_temple CHARACTER SET utf8mb4;
   ```
4. Edit `src/main/resources/application.properties` → set `spring.datasource.username/password`
   (or set env vars `DB_URL`, `DB_USER`, `DB_PASSWORD`).
5. Right-click `TempleApplication` → **Run As → Spring Boot App**.

**No MySQL yet?** Run with profile `h2` (Run Configurations → Spring Boot → Profile: `h2`).
Data is stored in the `./data` folder.

## URLs
| URL | What |
|---|---|
| http://localhost:8080/ | Public temple website (your index.html / style.css / script.js) |
| http://localhost:8080/login | Admin login |
| http://localhost:8080/admin | Admin dashboard (enquiries, rituals, festivals, password) |
| POST /api/enquiries | Public enquiry form endpoint |
| GET /api/rituals, /api/festivals | Public data used by the website |

Default admin (created on first start): **admin / Admin@12345**
→ Change it immediately via *Admin → Change Password*, and set `ADMIN_USERNAME` / `ADMIN_PASSWORD`
before the very first run in production.

## Security included
- Form login, BCrypt (strength 12) password hashes, `/admin/**` restricted to role ADMIN
- CSRF protection on all admin forms; session fixation protection; HttpOnly + SameSite cookies
- Account lock for 15 min after 5 failed logins
- Per-IP rate limit on login and on the public enquiry endpoint
- Bean Validation on every input; Thymeleaf output escaping (XSS); JPA parameterised queries (SQL injection)
- Security headers (X-Frame-Options, nosniff, Referrer-Policy, HSTS over HTTPS)
- H2 console disabled

## Before going live
- Serve over HTTPS and set `server.servlet.session.cookie.secure=true`
- Use a dedicated MySQL user (not root) and change `spring.jpa.hibernate.ddl-auto` to `validate`/`none` once tables exist
- Behind a reverse proxy, add `server.forward-headers-strategy=framework` so rate limiting sees real client IPs
- Build a jar: `mvn clean package` → `java -jar target/temple-1.0.0.jar`
