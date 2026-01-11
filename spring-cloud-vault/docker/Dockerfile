FROM openjdk:17-jdk-slim

WORKDIR /app

# Copier le JAR construit
COPY target/vault-demo-1.0.0.jar app.jar

# Exposer le port
EXPOSE 8080

# Variables d'environnement
ENV VAULT_TOKEN=root-token
ENV VAULT_ADDR=http://vault:8200

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]