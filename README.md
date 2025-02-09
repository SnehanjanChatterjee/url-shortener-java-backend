## Overview
Backend of Url Shortener project for Spring Boot in Java. Frontend Repository: [url-shortener-nextjs-frontend](https://github.com/SnehanjanChatterjee/url-shortener-nextjs-frontend)

Access the website at https://url-shortener-nextjs-frontend.vercel.app/

## Note
This backend app is hosted on [Render](https://www.render.com)'s free tier. Render spins down a Free web service that goes 15 minutes without receiving inbound traffic. 
Render spins the service back up whenever it next receives a request to process.

So if the website appears unresponsive, the backend is likely in "sleep" mode after being idle for 15 minutes.

It’s currently waking up, which may take up to a minute. Kindly wait for a moment, and the app will be ready shortly. Thank you for your understanding!

## Demo
![image](https://github.com/user-attachments/assets/45ab9fcd-c291-4640-b0c6-9c22aa41b4c9)
![image](https://github.com/user-attachments/assets/93caf0e1-2439-41a1-839a-36d0e1f744a2)
![image](https://github.com/user-attachments/assets/b983a3b0-4ee7-409b-a3ce-23791a5037ad)

<details>

<summary>
How to run the app locally
</summary>

## Prerequisites

1. **JDK 21+** - Make sure you have Java 21 or later installed.
2. **Gradle** - The project uses Gradle as the build tool. If you don't have it installed, follow the instructions [here](https://gradle.org/install/).
3. **Git** - Required for version control and deployment.

## Fork and Clone the Repository

### Fork the Repository
Click the <b>Fork</b> button at the top right to create a copy of the repository under your own GitHub account.

### Clone the Forked Repository
Use the following commands to clone the repository and navigate to its directory:

```
git clone https://github.com/yourusername/url-shortener-java-backend.git
cd url-shortener-java-backend
```

## Set up the Environment
Create a .env file in the root directory of the project.
Example .env file:

```
BACKEND_CLOUD_BASE_URL=<add-url-of-hosted-backend-service>
FRONTEND_LOCAL_BASE_URL=<add-url-of-localhost-frontend-service i.e http://localhost:3000>
FRONTEND_CLOUD_BASE_URL=<add-url-of-hosted-frontend-service-main-domain>
FRONTEND_CLOUD_BASE_URL_2=<add-url-of-hosted-frontend-service-domain2> [Optional]
FRONTEND_CLOUD_BASE_URL_3=<add-url-of-hosted-frontend-service-domain3> [Optional]
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
The application will start on http://localhost:8080

## Access the Health Check Endpoint
To verify the application's health status, navigate to the following URL in your browser or API client: http://localhost:8080/actuator/health

This will return a JSON response indicating whether the application is running correctly.

## Deploy to Render
Follow these steps to deploy your Spring Boot application to Render: [Hosting Springboot App on render.com](https://www.youtube.com/watch?v=p3AIecyvok4)

<!-- [![Hosting Springboot App on render.com](https://img.youtube.com/vi/p3AIecyvok4/0.jpg)](https://www.youtube.com/watch?v=p3AIecyvok4) -->

</details
