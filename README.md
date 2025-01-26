# url-shortener-java-backend
Backend of Url Shortener project for Spring Boot in Java

## Prerequisites

1. **JDK 21+** - Make sure you have Java 21 or later installed.
2. **Gradle** - The project uses Gradle as the build tool. If you don't have it installed, follow the instructions [here](https://gradle.org/install/).
3. **Git** - Required for version control and deployment.

## Fork and Clone the Repository

### Fork the Repository
   Go to my [GitHub repository](https://github.com/SnehanjanChatterjee/url-shortener-java-backend) and click the "Fork" button at the top right to create a copy of the repository under your own GitHub account.

### Clone the Forked Repository
Use the following commands to clone the repository and navigate to its directory:

```bash
git clone https://github.com/yourusername/url-shortener-java-backend.git
cd url-shortener-java-backend
```

## Set up the Environment
Create a .env file in the root directory of the project.
Example .env file:

```bash
BACKEND_CLOUD_BASE_URL=<add-url-of-hosted-backend-service>
FRONTEND_CLOUD_BASE_URL=<add-url-of-hosted-frontend-service>
```

Add the .env file to .gitignore to avoid committing sensitive information.

```bash
git rm --cached .env
echo ".env" >> .gitignore
```

## Build the Project Using Gradle

To build the project, run:

```bash
./gradlew clean build
```
This will compile the code and package it into a JAR file.

## Run the Project Locally
To run the project locally, go to UrlShortenerJavaBackendApplication file and run it or you can use the following command:

```bash
./gradlew bootRun
```
The application will start on http://localhost:8080.

## Deploy to Render
Follow these steps to deploy your Spring Boot application to Render:

[![Hosting Springboot App on render.com](https://img.youtube.com/vi/p3AIecyvok4/0.jpg)](https://www.youtube.com/watch?v=p3AIecyvok4)