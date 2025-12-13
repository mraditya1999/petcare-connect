# 🐾 PetCareConnect (Pet Forum Application)

[![PetCareConnect Screenshot 1](./design/petcareconnect-01-home.jpeg)](YOUR_DEPLOYED_LINK)

[![PetCareConnect Screenshot 2](./design/petcareconnect-02-about.jpeg)](YOUR_DEPLOYED_LINK)

[![PetCareConnect Screenshot 3](./design/petcareconnect-03-forum.jpeg)](YOUR_DEPLOYED_LINK)

[![PetCareConnect Screenshot 5](./design/petcareconnect-05-authentication.jpeg)](YOUR_DEPLOYED_LINK)

This project, **PetCareConnect**, is a modern forum application where pet owners connect, share thoughts, suggestions, and advice regarding their pets. It is built with a scalable **Microservices** architecture.

The frontend uses **React**, **TypeScript**, **Tailwind CSS**, and **shadcn/ui**. The backend utilizes **Spring Boot**, **Spring Security**, with **MySQL** (for user data), **MongoDB** (for posts/comments), and **Redis** (for caching). The future scope includes an appointment system for pets.

## Features

- **Forum Posting**: Users can create, view, and comment on discussion posts.
- **Secure Auth**: Robust security implemented using **Spring Security** and JWTs.
- **Microservices**: Decoupled architecture for high availability and independent scaling.
- **Search & Filter**: Allows users to find relevant topics by keywords or categories.

### Frontend (from `/frontend` directory)

- **React 18** with **TypeScript**
- **Redux Toolkit**: State Management.
- **Tailwind CSS + shadcn/ui**: Styling and design system.
- **Vite**: Build Tool.

### Backend (from `/backend` directory)

- **Spring Boot 3.4.1** with **Java 17**.
- **MySQL**: Relational data (Users, Appointments, Pets).
- **MongoDB**: Unstructured data (Forums, Comments).
- **Redis**: Caching (OTPs, sessions).
- **Spring Security**: JWT authentication and role-based access control.
- **Twilio & Cloudinary**: External integrations (SMS, Image uploads).

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

## How to Run Locally

### 1. Setup Infrastructure (Docker Recommended)

From the root directory, start the required database and caching services:

```powershell
# Start MySQL, MongoDB, Redis, Backend, and Frontend services
docker-compose up --build

2. Manual Startup (Without Docker)
Start your local MySQL (3306), MongoDB (27017), and Redis (6379) instances.

Backend: Navigate to /backend, ensure all environment variables are set in .env, and run:

mvn spring-boot:run

Frontend: Navigate to /frontend, ensure API URLs are configured, and run:
npm install
npm run dev

Open https://petcareconnect.netlify.app to view the app in your browser.

Command,Directory,Description
npm install,/frontend,Install dependencies
npm run dev,/frontend,Start development server
mvn spring-boot:run,/backend,Run Spring Boot application
mvn clean install -DskipTests,/backend,Build project (skipping tests)
docker-compose up -d,/ (root),Start all services in detached mode
```

## Author

- Portfolio - [@mraditya1999](https://adityayadav-dev.netlify.app)
- Twitter - [@mraditya1999](https://twitter.com/mraditya1999)
- Linkedin - [@mraditya1999](https://www.linkedin.com/in/mraditya1999/)
- Medium - [@mraditya1999](https://medium.com/@mraditya1999)
- Frontend Mentor - [@mraditya1999](https://www.frontendmentor.io/profile/Aditya-oss-creator)
