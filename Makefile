.PHONY: help build up down logs clean rebuild restart

help:
	@echo "Available commands:"
	@echo "  make build      - Build Docker images"
	@echo "  make up         - Start all containers"
	@echo "  make down       - Stop all containers"
	@echo "  make logs       - View logs from all containers"
	@echo "  make clean      - Remove all containers and volumes"
	@echo "  make rebuild    - Rebuild images and start containers"
	@echo "  make restart    - Restart all containers"
	@echo "  make ps         - Show running containers"

build:
	docker-compose build

up:
	docker-compose up -d

down:
	docker-compose down

logs:
	docker-compose logs -f

clean:
	docker-compose down -v

rebuild: clean build up
	docker-compose logs -f

restart:
	docker-compose restart

ps:
	docker-compose ps

backend-logs:
	docker-compose logs -f backend

frontend-logs:
	docker-compose logs -f frontend

mysql-logs:
	docker-compose logs -f mysql

mongodb-logs:
	docker-compose logs -f mongodb
