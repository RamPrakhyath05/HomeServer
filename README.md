# HomeServer
A personal file server built on an old laptop, running a Spring Boot REST API in Docker. Accessible on the local network at home, and remotely via Tailscale.

---

## Stack

| Layer            | Technology                                         |
| ---------------- | -------------------------------------------------- |
| OS               | EndeavourOS (boots into TTY on the server, no GUI) |
| Dev OS           | Fedora                                             |
| Backend          | Java Spring Boot                                   |
| Containerization | Docker                                             |
| CI/CD            | GitHub Actions                                     |
| Auto-updates     | Watchtower                                         |
| Remote Access    | Tailscale (planned)                                |
| Auth             | JWT (planned)                                      |

---

## Architecture

```
Dev Machine (Fedora)
  └── git push → GitHub
                    │
                    ▼
            GitHub Actions
              ├── mvn clean package       # builds the jar
              ├── docker build            # packages into image
              └── docker push             # pushes to Docker Hub
                                                │
                                                ▼
                                           Docker Hub
                                                │
                                                ▼
                                         Server Laptop
                                        ┌──────────────┐
                                        │  Watchtower  │  ← watches for new images
                                        │  Homeserver  │  ← serves the REST API
                                        └──────────────┘
                                               │
                                ───────────────────────────────
                                │                             │
                           Home Network                  Tailscale VPN
                                                   (planned, for outside access)
```

---

## Getting Started

### Prerequisites
- Java 21
- Maven
- Docker
- Docker Hub account
- GitHub account

### Running Locally

```bash
mvn spring-boot:run
```

App runs at `http://localhost:8080`.

### Deploying to Server

```bash
git push origin main
```

GitHub Actions automatically builds the jar, packages it into a Docker image, and pushes it to Docker Hub. Watchtower on the server picks up the new image automatically within 30 seconds.

---

## Server Setup (One Time)

Run these on the server laptop once:

```bash
# Run the homeserver container
docker run -d \
  --name homeserver \
  -p 8080:8080 \
  -v /home/ramprakhyath/homeserver-files:/homeserver-files \
  --restart unless-stopped \
  ramprakhyath/homeserver:latest

# Run Watchtower for auto-updates
docker run -d \
  --name watchtower \
  -e DOCKER_API_VERSION=1.40 \
  -e WATCHTOWER_POLL_INTERVAL=30 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --restart unless-stopped \
  containrrr/watchtower homeserver
```

On boot, systemd starts Docker automatically, which in turn restarts all containers due to the `--restart unless-stopped` flag. No manual intervention needed after a power outage or reboot.

---

## Project Structure (Currently)

```
src/
└── main/
    └── java/com/annamareddys/homeserver/
        ├── controller/         # HTTP layer, handles requests
        ├── service/            # Business logic
        └── repository/         # Filesystem operations
```

---

## API Endpoints

### Health Check
```
GET /response/checkhealth
```
Returns `All is well :)` if the server is running.

### File Upload _(planned)_
```
POST /files/upload
```

### File Download _(planned)_
```
GET /files/download/{filename}
```

### List Files _(planned)_
```
GET /files/list
```

### Delete File _(planned)_
```
DELETE /files/{filename}
```

---

## Security _(planned)_
- JWT Authentication on all file endpoints
- Tailscale VPN for remote access — server is invisible to the public internet

---

## Storage

Files are stored in `/homeserver-files` on the server laptop, mounted as a Docker volume.

---

## Author
Ram Prakhyath Annamareddy
