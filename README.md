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

## Environment Variables

| Variable | Required | Description |
|---|---|---|
| `MONGODB_URI` | yes | MongoDB connection string. Queryable Encryption requires an **Atlas or Enterprise 7.0+** cluster |
| `KEYCLOAK_ISSUER_URI` | yes | Keycloak realm issuer URI used to validate JWTs (e.g. `https://<keycloak-host>/realms/<realm>`) |
| `MONGODB_ENCRYPTION_ENABLED` | yes | `true` enables Queryable Encryption of `bet.prediction`. Once data is encrypted it must stay `true` |
| `MONGODB_MASTER_KEY` | when encryption is on | Base64 96-byte local KMS master key. Generate with `openssl rand -base64 96 \| tr -d '\n'`. **Keep it backed up outside the deploy environment — losing it makes encrypted data unreadable** |
| `MONGODB_CRYPT_SHARED_PATH` | when encryption is on | Absolute path to the MongoDB Automatic Encryption Shared Library (`crypt_shared`). Windows: `...\mongo_crypt_v1.dll`; Linux: `.../mongo_crypt_v1.so`. **On Render this is already set by the Dockerfile — do not override it** |
| `MONGODB_ENCRYPTION_MIGRATION_ENABLED` | first encrypted start only | `true` runs the one-shot migration of existing plaintext bets, then set it back to `false` |

Download `crypt_shared` from the [MongoDB Download Center](https://www.mongodb.com/try/download/enterprise)
("crypt_shared" package, same version line as your cluster). Full encryption setup, migration,
rollback plan and troubleshooting: [docs/QUERYABLE_ENCRYPTION.md](docs/QUERYABLE_ENCRYPTION.md).

## Running the Application

````
mvn clean install
````
Then navigate to infrastructure directory:

````
export MONGODB_URI="<YOUR_CONNECTION_STRING>"
export KEYCLOAK_ISSUER_URI="<YOUR_KEYCLOAK_REALM_ISSUER>"
export MONGODB_ENCRYPTION_ENABLED=true
export MONGODB_MASTER_KEY="<BASE64_96_BYTE_KEY>"
export MONGODB_CRYPT_SHARED_PATH="<PATH_TO_CRYPT_SHARED_LIB>"
mvn spring-boot:run
````

## Running the Test Coverage

````
mvn clean test verify -Pcoverage
````

The Queryable Encryption integration tests need Docker (Testcontainers). Two of them also
need the env var `CRYPT_SHARED_LIB_PATH` pointing to the local `crypt_shared` library —
without it they are skipped.
## Endpoints

You can find all endpoints available at resources/http