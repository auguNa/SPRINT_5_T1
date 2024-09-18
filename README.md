# Blackjack Game API
# Blackjack Game Application

This is a **reactive Blackjack game application** built using Java, Spring Boot, Spring WebFlux, and a reactive stack with **MongoDB** and **MySQL** for storing game and player data.

## Features
- Play Blackjack in a reactive environment.
- Supports player management, game creation, game deletion, and ranking.
- JWT-based authentication for secure endpoints.
- Separate game and user data storage in MongoDB and MySQL, respectively.
- Handles asynchronous operations with Reactor's `Mono` and `Flux`.

## Technologies Used
- **Java 17+**
- **Spring Boot**
- **Spring WebFlux**
- **MongoDB** (for game data)
- **MySQL** (for player)
- **JWT Authentication**
- **Reactive Programming with Reactor**
- **Spring Data R2DBC**
- **Swagger/OpenAPI** for API documentation

## Prerequisites
- Java 17 or later
- MongoDB and MySQL installed and running
- Maven installed

## Setup

### 1. Clone the repository
```bash
git clone https://github.com/auguNa/SPRINT_5_T1.git
cd blackjack-game

## Setup Instructions

1. Clone the repository
2. Configure MongoDB and MySQL in `application.properties`.
3. Run the application using Maven/Gradle.

## API Endpoints

### Create a New Game
`POST /game/new`
- **Request:** `playerName`
- **Response:** Game object

### Get Game Details
`GET /game/{id}`
- **Request:** Game ID
- **Response:** Game object

### Make a Move
`POST /game/{id}/play`
- **Request:** Game ID, moveType
- **Response:** Updated Game object

### Delete a Game
`DELETE /game/{id}/delete`
- **Request:** Game ID
- **Response:** No Content (204)

### Get Player Ranking
`GET /player/ranking`
- **Response:** List of players ranked by their score.

## Testing
- Run unit tests using `mvn test`.

