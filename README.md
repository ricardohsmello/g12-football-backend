# g12-football-br
Backend for the **G12 Football Betting Pool** application, built with Java Spring Boot and MongoDB. This backend is part of a full-stack project that also includes an Angular frontend (available [here](https://github.com/ricardohsmello/g12-football-frontend)).

# Features
- User registration and authentication
- Bet placement and management
- Real-time score updates
- Leaderboard and rankings
- Admin panel for managing users and bets

# Technologies Used
- Backend: Java Spring Boot
- Database: MongoDB
- Authentication: Keycloak

# Getting Started
## Running the Application

````
mvn clean install
````
Then navigate to infrastructure directory:

````
export MONGODB_URI="<YOUR_CONNECTION_STRING>"
mvn spring-boot:run
````

## Endpoints

You can find all endpoints available at resources/http