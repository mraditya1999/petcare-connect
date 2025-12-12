# Full Stack Project Setup

## Table of Contents

- [Prerequisites](#prerequisites)
- [Frontend Setup](#frontend-setup)
- [Backend Setup](#backend-setup)
- [Docker](#docker)
- [Contributing](#contributing)
- [License](#license)

## Prerequisites

- Node.js (>=14.x)
- npm (>=6.x)
- Java (>=11)
- Maven (>=3.6.0)
- Docker
- Docker Compose

## Frontend Setup

### Installation

First, clone the repository and navigate to the frontend directory:

\`\`\`bash
git clone https://github.com/yourusername/yourproject.git
cd yourproject/frontend
\`\`\`

Next, install the dependencies:

\`\`\`bash
npm install
\`\`\`

### Development

To start the development server, run:

\`\`\`bash
npm start
\`\`\`

This will run the app in development mode. Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

### Build

To create a production build of the app, run:

\`\`\`bash
npm run build
\`\`\`

The build will be located in the `build` directory.

## Backend Setup

### Installation

Navigate to the backend directory:

\`\`\`bash
cd ../backend
\`\`\`

Make sure you have all required dependencies installed.

### Development

To start the backend server, run:

\`\`\`bash
mvn spring-boot:run
\`\`\`

The backend server will run on [http://localhost:8080](http://localhost:8080).

### Database Setup

The backend uses both SQL and MongoDB. Configure your databases as follows:

1. **SQL**: Update your `application.properties` file with your SQL database credentials.
2. **MongoDB**: Update your `application.properties` file with your MongoDB connection string.

Example `application.properties`:

\`\`\`properties
spring.datasource.url=jdbc:mysql://localhost:3306/yourdatabase
spring.datasource.username=yourusername
spring.datasource.password=yourpassword

spring.data.mongodb.uri=mongodb://localhost:27017/yourmongodb
\`\`\`

## Docker

You can also use Docker to containerize both the frontend and backend applications.

### Build Docker Image for Frontend

To build the Docker image for the frontend, use the following command:

\`\`\`bash
cd frontend
docker build -t yourfrontend:latest .
\`\`\`

### Run Docker Container for Frontend

To run the Docker container for the frontend, use the following command:

\`\`\`bash
docker run -p 3000:3000 yourfrontend:latest
\`\`\`

### Build Docker Image for Backend

To build the Docker image for the backend, use the following command:

\`\`\`bash
cd ../backend
docker build -t yourbackend:latest .
\`\`\`

### Run Docker Container for Backend

To run the Docker container for the backend, use the following command:

\`\`\`bash
docker run -p 8080:8080 yourbackend:latest
\`\`\`

### Docker Compose

Alternatively, you can use Docker Compose. Create a `docker-compose.yml` file in the root directory of your project:

\`\`\`yaml
version: '3'
services:
frontend:
build: ./frontend
ports: - "3000:3000"
environment: - NODE_ENV=development

backend:
build: ./backend
ports: - "8080:8080"
environment: - SPRING_DATASOURCE_URL=jdbc:mysql://database:3306/yourdatabase - SPRING_DATASOURCE_USERNAME=yourusername - SPRING_DATASOURCE_PASSWORD=yourpassword - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/yourmongodb
depends_on: - database - mongodb

database:
image: mysql:latest
environment: - MYSQL_ROOT_PASSWORD=rootpassword - MYSQL_DATABASE=yourdatabase

mongodb:
image: mongo:latest
\`\`\`

### Build and Run with Docker Compose

To build and run the entire application stack, use:

\`\`\`bash
docker-compose up --build
\`\`\`

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for more details.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.
