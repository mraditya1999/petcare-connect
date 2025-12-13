# Docker Setup Guide for PetCare Connect

## Prerequisites

- Docker Desktop (Windows/Mac) or Docker Engine (Linux)
- Docker Compose (usually included with Docker Desktop)

## Quick Start

### 1. Build and Start Containers

```bash
# Using Make (recommended)
make build
make up

# Or using Docker Compose directly
docker-compose build
docker-compose up -d
```

### 2. Access the Application

- **Frontend**: http://localhost:5173,https://petcareconnect.netlify.app
- **Backend**: http://localhost:8080,petcare-connect-production.up.railway.app
- **MySQL**: localhost:3306
- **MongoDB**: localhost:27017

## Services

### Backend (Java/Spring Boot)

- **Port**: 8080
- **Build**: Multi-stage Maven build (optimized)
- **Dependencies**: MySQL, MongoDB
- **Environment Variables**: See `.env.example`

### Frontend (React/Vite)

- **Port**: 5173
- **Build**: Node.js multi-stage build
- **Server**: Serve (lightweight Node.js server)

### MySQL Database

- **Port**: 3306
- **Username**: root
- **Password**: root
- **Database**: petcare_connect
- **Health Check**: Enabled

### MongoDB

- **Port**: 27017
- **Database**: petcare_connect
- **Username**: configurable in `.env`
- **Password**: configurable in `.env`

## Common Commands

### View Logs

```bash
# All services
make logs
# or
docker-compose logs -f

# Specific service
make backend-logs
make frontend-logs
make mysql-logs
make mongodb-logs
```

### Stop Containers

```bash
make down
```

### Remove Containers and Volumes (Clean)

```bash
make clean
```

### Rebuild Everything

```bash
make rebuild
```

### Check Running Containers

```bash
make ps
# or
docker-compose ps
```

## Environment Configuration

1. Copy `.env.example` to `.env` (if you need custom values):

   ```bash
   cp .env.example .env
   ```

2. Update the following in `.env`:
   - Database credentials (if different from defaults)
   - Email credentials (GMAIL_USERNAME, GMAIL_PASSWORD)
   - JWT secret
   - Cloudinary credentials
   - MongoDB credentials (if using authentication)

## Troubleshooting

### Port Already in Use

```bash
# Find what's using the port (Windows PowerShell)
netstat -ano | findstr :8080

# Kill the process
taskkill /PID <PID> /F
```

### Container Won't Start

```bash
# Check logs
docker-compose logs service_name

# Rebuild without cache
docker-compose build --no-cache
```

### Database Connection Issues

```bash
# Verify services are running
docker-compose ps

# Check database health
docker-compose logs mysql
docker-compose logs mongodb
```

### Clear Docker Cache

```bash
# Remove unused images, containers, and volumes
docker system prune -a --volumes
```

## Development Workflow

### For Backend Development

1. Make changes in `backend/src`
2. Rebuild container: `docker-compose up --build backend -d`
3. View logs: `docker-compose logs -f backend`

### For Frontend Development

1. Make changes in `frontend/src`
2. The Vite dev server will hot-reload
3. If not, rebuild: `docker-compose up --build frontend -d`

## Production Considerations

For production deployment, consider:

1. Use environment-specific `.env` files
2. Set `VITE_API_URL` in frontend for production API endpoint
3. Update `FRONTEND_URL_1` and `FRONTEND_URL_2` in backend config
4. Use proper secret management (not hardcoded in compose file)
5. Add health checks for all services
6. Configure persistent volumes for databases
7. Use reverse proxy (nginx) for routing

## Additional Resources

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Vite Docker Guide](https://vitejs.dev/guide/ssr.html)
