# Use the official Node.js image
FROM node:16-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the application
RUN npm run build

# Expose port 3000 (or the port your frontend uses)
EXPOSE 5173

# Start the application
CMD ["npm", "run", "serve"]
