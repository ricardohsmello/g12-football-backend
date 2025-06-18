FROM maven:3.9.6-eclipse-temurin-21

# Cria pasta e entra no backend (onde está o pom.xml)
WORKDIR /app/backend

# Copia somente a pasta backend (Render clona o repo na raiz)
COPY backend /app/backend

# Compila o projeto
RUN mvn clean package -DskipTests

# Define variável de ambiente (Render vai sobrescrever)
ENV MONGODB_URI=""

# Roda o JAR
CMD ["java", "-jar", "infrastructure/target/g12-infrastructure-0.0.1-SNAPSHOT.jar"]