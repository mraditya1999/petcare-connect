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

## Technologies Used

- **React, TypeScript**: Frontend libraries for building user interfaces with type safety.
- **Tailwind CSS & shadcn/ui**: Used for highly customizable and accessible styling.
- **Redux**: State Management for global application state.
- **Spring Boot & Microservices**: Framework for building scalable, independent backend services.
- **Spring Security**: Provides authentication and authorization across services.
- **MySQL & MongoDB**: Dual database strategy for relational data (MySQL) and flexible content (MongoDB).
- **Redis**: In-memory data store for caching and session management.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

## How to Run Locally

1.  Clone the repository to your local machine.
    ```bash
    git clone [YOUR_REPOSITORY_URL]
    ```
2.  Start the infrastructure services (MySQL, MongoDB, Redis) using Docker Compose.
    ```bash
    docker-compose up -d
    ```
3.  Navigate to each **Spring Boot Microservice** directory and run them.
    ```bash
    # Example for one service
    cd user-service
    mvn spring-boot:run
    ```
4.  Navigate to the **Frontend** directory.
5.  Install the required dependencies with `npm install`.
6.  Start the development server with `npm run dev` (or `npm start`).
7.  Open [https://petcareconnect.netlify.app](https://petcareconnect.netlify.app) to view the app in your browser.

## Author

- Portfolio - [@mraditya1999](https://adityayadav-dev.netlify.app)
- Twitter - [@mraditya1999](https://twitter.com/mraditya1999)
- Linkedin - [@mraditya1999](https://www.linkedin.com/in/mraditya1999/)
- Medium - [@mraditya1999](https://medium.com/@mraditya1999)
- Frontend Mentor - [@mraditya1999](https://www.frontendmentor.io/profile/Aditya-oss-creator)
