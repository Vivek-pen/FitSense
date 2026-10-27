# FitSense Backend

FitSense is a fitness tracking platform that allows users to authenticate,
record activities, view activity history, and receive AI-generated fitness
recommendations.

This repository contains the Spring Boot and Spring Cloud microservices that
power the platform. The separate React frontend is available at
[Vivek-pen/FitSense-Frontend](https://github.com/Vivek-pen/FitSense-Frontend).

## Architecture

The backend is composed of the following services:

| Service | Port | Responsibility |
| --- | ---: | --- |
| Config Server | `8888` | Provides centralized service configuration |
| Eureka Server | `8761` | Service discovery and registration |
| User Service | `8081` | Stores application user profiles |
| Activity Service | `8082` | Creates and retrieves fitness activities |
| AI Service | `8083` | Generates and stores activity recommendations |
| API Gateway | `9090` | Authenticated API entry point for the frontend |

Requests from the frontend should be sent to the API Gateway. Individual
microservice ports are intended for internal/local service operation.

## Technology

- Java and Spring Boot
- Spring Cloud Config, Gateway, and Eureka
- Spring Security OAuth2 Resource Server
- Keycloak for authentication and authorization
- PostgreSQL for user data
- MongoDB for activity and recommendation data
- RabbitMQ for asynchronous activity events
- Google Gemini through the Spring AI Google GenAI integration

## Prerequisites

- JDK compatible with the service Maven projects
- Maven, or the Maven Wrapper included in each service
- Docker Desktop or Docker Engine with Docker Compose
- A Google Gemini API key

## Required Docker containers

The backend expects these infrastructure containers to be available locally:

| Container | Port | Purpose |
| --- | ---: | --- |
| Keycloak | `8181` | User login, realms, and token issuance |
| PostgreSQL | `5433` | User Service database: `fitness_user_db` |
| MongoDB | `27017` | Activity database: `fitnessactivity` and recommendation database: `fitnessrecommendation` |
| RabbitMQ | `5672` | Activity and recommendation messaging |
| RabbitMQ Management | `15672` | Optional browser-based RabbitMQ administration |

This repository currently does not include a Docker Compose file. Start
equivalent containers individually, or use your own local Compose setup with
the ports, databases, and credentials expected by the configuration files.
Do not use production credentials for local development.

RabbitMQ clients use port `5672`; port `15672` is only the optional management
interface.

## Gemini API configuration

The AI Service requires a Google Gemini API key to generate recommendations.
Create a key through [Google AI Studio](https://aistudio.google.com/) and
provide it to the backend process as an environment variable:

PowerShell:

```powershell
$env:AI_GOOGLE_GENAI_API_KEY="your-gemini-api-key"
```

macOS/Linux:

```bash
export AI_GOOGLE_GENAI_API_KEY="your-gemini-api-key"
```

The key must be available to the AI Service and must never be committed to
Git or placed in the frontend environment file.

## Keycloak configuration

Create or import a realm named `fitness-oauth2`, then configure a public
frontend client with:

- Authorization Code flow
- PKCE using `S256`
- Redirect URI: `http://localhost:5173/*`
- Web origin: `http://localhost:5173`

The API Gateway validates tokens issued by this realm. The frontend should
use the matching realm, Keycloak URL, and client ID in its `.env` file.

## Running locally

1. Start the required Docker containers.
2. Set `AI_GOOGLE_GENAI_API_KEY` in the environment used to start the AI Service.
3. Start the services in this order:

   ```text
   Config Server  (8888)
   Eureka Server  (8761)
   User Service   (8081)
   Activity Service (8082)
   AI Service     (8083)
   API Gateway    (9090)
   ```

4. Configure the frontend to use:

   ```env
   VITE_API_BASE_URL=http://localhost:9090
   VITE_KEYCLOAK_URL=http://localhost:8181
   VITE_KEYCLOAK_REALM=fitness-oauth2
   VITE_KEYCLOAK_CLIENT_ID=your-frontend-client-id
   ```

5. Start the frontend by following the instructions in the
   [frontend repository](https://github.com/Vivek-pen/FitSense-Frontend).

Each service contains a Maven Wrapper. From a service directory, run:

```bash
./mvnw spring-boot:run
```

On Windows, use:

```powershell
.\mvnw.cmd spring-boot:run
```

Wait for Config Server and Eureka to be available before starting the
remaining services.

## Request flow

1. The user signs in through Keycloak.
2. The frontend sends an access token to the API Gateway.
3. The Gateway validates the token and routes the request through Eureka.
4. Activity Service stores the activity in MongoDB and publishes an event to
   RabbitMQ.
5. AI Service consumes the event, calls Gemini, and stores the recommendation
   in MongoDB.
6. The frontend retrieves the recommendation through the API Gateway when it
   becomes available.

Recommendation generation is asynchronous, so it may not be available
immediately after an activity is created.


