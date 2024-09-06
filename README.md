# Blackjack Game API

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

