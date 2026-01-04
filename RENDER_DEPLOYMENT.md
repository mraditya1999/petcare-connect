# Deploying PetCare-Connect to Render

This guide provides step-by-step instructions for deploying your full-stack application (React frontend, Spring Boot backend) to Render. We will use Docker for containerization and a `render.yaml` file to define and manage our infrastructure as code.

## Table of Contents
1.  [Prerequisites](#1-prerequisites)
2.  [Step 1: Set Up Your Databases and Redis on Render](#1-set-up-your-databases-and-redis-on-render)
3.  [Step 2: Prepare Your Application for Production](#2-prepare-your-application-for-production)
4.  [Step 3: Create a `render.yaml` File](#3-create-a-renderyaml-file)
5.  [Step 4: Deploy Your Application](#4-deploy-your-application)
6.  [Step 5: CI/CD - Automatic Deployments](#5-cicd---automatic-deployments)
7.  [Step 6: Custom Domains](#6-custom-domains)

---

### 1. Prerequisites

*   **Render Account**: Sign up for a free account at [render.com](https://render.com/).
*   **GitHub Repository**: Your application code should be in a GitHub repository.
*   **Install Render CLI** (Optional but recommended): `npm install -g @render-api/cli`

---

### 2. Step 1: Set Up Your Databases and Redis on Render

Before deploying your application, you need to create managed instances of PostgreSQL, MongoDB, and Redis on Render.

1.  From the Render Dashboard, click **New +** and choose the database/service you want to create:
    *   **PostgreSQL**: Create a new PostgreSQL database. Give it a unique name (e.g., `petcare-db`).
    *   **MongoDB**: Render does not have a native MongoDB service. You can use a MongoDB provider like [MongoDB Atlas](https://www.mongodb.com/atlas/database) and get a connection string. For simplicity during initial deployment, you can search for a MongoDB alternative in Render's marketplace or use a free tier from Atlas.
    *   **Redis**: Create a new Redis instance. Give it a name (e.g., `petcare-redis`).

2.  **Collect Connection Details**: For each service you create, Render will provide you with connection details (hostname, username, password, connection string). You will use these as environment variables for your backend service.
    *   **Security Note**: Render provides an "internal" connection string for services within the same Render account. **Always use the internal connection string** for better security and performance.

---

### 3. Step 2: Prepare Your Application for Production

I will help you with the necessary code changes in the next steps, which include:
*   **Updating Dockerfiles**: Optimizing your `backend/Dockerfile` and `frontend/Dockerfile` for smaller, more secure production builds.
*   **Production Configuration**: Modifying `application.properties` to use environment variables for all secrets and to set production-appropriate logging and caching.

---

### 4. Step 3: Create a `render.yaml` File

The `render.yaml` file tells Render how to build and deploy your services. Create this file in the root of your project. I will provide a production-ready `render.yaml` for you in a later step.

A preview of what it will look like:

```yaml
# render.yaml
services:
  - type: web # Your Spring Boot Backend
    name: petcare-backend
    env: docker
    dockerfilePath: ./backend/Dockerfile
    # ... more configuration ...

  - type: web # Your React Frontend
    name: petcare-frontend
    env: docker
    dockerfilePath: ./frontend/Dockerfile
    # ... more configuration ...
```

---

### 5. Step 4: Deploy Your Application

1.  **Commit and Push**: Commit the updated Dockerfiles, the new `render.yaml`, and any other code changes to your GitHub repository.
2.  **Create Blueprint Instance**: In the Render Dashboard, click **New +** -> **Blueprint**.
3.  **Connect Your Repo**: Select the GitHub repository for your project. Render will automatically detect and parse your `render.yaml` file.
4.  **Create Services**: Render will show you the services defined in your `render.yaml`. Click **Apply** to create and deploy them.
5.  **Monitor the Deploy**: Go to the dashboard for your services to monitor the build and deploy logs.

---

### 6. Step 5: CI/CD - Automatic Deployments

Render's GitHub integration provides CI/CD out of the box.

*   **Auto-Deploy on Push**: By default, Render will automatically build and deploy any new commit to your default branch (e.g., `main` or `master`).
*   **Preview Environments**: You can also configure Render to create a new "Preview Environment" for every pull request, allowing you to test changes before merging them into your main branch.

The empty `.github/workflows/ci.yml` is not needed for Render's default auto-deploy feature. If you have specific CI checks you want to run (like running tests before a deploy is triggered), you can set that up in GitHub Actions, and Render will only deploy if those checks pass.

---

### 7. Step 6: Custom Domains

Once your application is deployed and running, you can add a custom domain.

1.  Go to the **Settings** tab for your `frontend` service in Render.
2.  Click **Add Custom Domain** and follow the instructions to point your domain's DNS records to Render.
3.  Render automatically provisions and renews SSL certificates for your custom domains.

This guide provides a high-level overview. I will now proceed with the specific file changes needed to get your application ready for deployment.
