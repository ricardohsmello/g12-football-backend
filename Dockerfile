FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

COPY backend /app

RUN mvn clean package -DskipTests

ENV MONGODB_URI=""

CMD ["java", "-jar", "infrastructure/target/g12-infrastructure-0.0.1-SNAPSHOT.jar"]