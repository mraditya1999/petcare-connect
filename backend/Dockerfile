# ----------------------------------------------------
# STAGE 1: BUILD (Uses Maven image to compile code)
# ----------------------------------------------------
FROM maven:3.9.11-eclipse-temurin-21 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests


# ----------------------------------------------------
# STAGE 2: RUNTIME (Uses minimal JRE image to run JAR)
# ----------------------------------------------------
FROM eclipse-temurin:21-jre-jammy

# Set the working directory in the container
WORKDIR /app

# 🛑 INSTALL netcat HERE: Only required for the dependency check script
# We must use 'apt-get' in this Debian-based (jammy) image.
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Copy the wait script and make it executable
COPY wait-for-it.sh . 
RUN chmod +x wait-for-it.sh

# Copy the built jar file from the build stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application runs on
EXPOSE 8080

# Run the JAR file, waiting for MySQL first
ENTRYPOINT ["./wait-for-it.sh", "mysql", "3306", "java", "-jar", "app.jar"]