FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app


#Currently, the production cluster runs MongoDB Atlas version 8.0.24.
#The version below must be updated if the cluster is upgraded.
ARG CRYPT_SHARED_VERSION=8.2.11
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /opt/mongo-crypt \
    && curl -fsSL "https://downloads.mongodb.com/linux/mongo_crypt_shared_v1-linux-x86_64-enterprise-ubuntu2204-${CRYPT_SHARED_VERSION}.tgz" \
       | tar -xz -C /opt/mongo-crypt \
    && test -f /opt/mongo-crypt/lib/mongo_crypt_v1.so

ENV MONGODB_CRYPT_SHARED_PATH=/opt/mongo-crypt/lib/mongo_crypt_v1.so

COPY . /app

RUN mvn clean package -DskipTests

ENV MONGODB_URI=""

CMD ["java", "-jar", "infrastructure/target/g12-infrastructure-0.0.1-SNAPSHOT.jar"]