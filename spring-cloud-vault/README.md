# Spring Cloud Vault Demo

## 🎯 Objectif

Démo d'intégration Spring Boot avec HashiCorp Vault pour la gestion sécurisée des secrets.

## 🏗️ Architecture

```
spring-cloud-vault/
├── src/
│   ├── main/
│   │   ├── java/com/example/vault/
│   │   │   └── VaultDemoApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-test.yml
│   │       ├── application-pprod.yml
│   │       └── application-prod.yml
│   └── test/
├── docker/
│   └── Dockerfile
├── vault/
│   └── config/
│       └── vault-config.hcl
├── docker-compose.yml
├── setup-vault.sh
├── setup-profiles-vault.sh
└── pom.xml
```

## 🚀 Démarrage rapide

### 1. Construire l'application
```bash
mvn clean package
```

### 2. Configurer Vault avec les secrets par profil
```bash
# Sur Linux/Mac
chmod +x setup-profiles-vault.sh
./setup-profiles-vault.sh

# Sur Windows (PowerShell)
# Exécuter les commandes manuellement du script
```

### 3. Démarrer avec Docker Compose (tous les profils)
```bash
docker-compose up -d
```

### 4. Tester les applications par profil
```bash
# DEV - http://localhost:8080
# TEST - http://localhost:8081
# PRE-PROD - http://localhost:8082
# PROD - http://localhost:8083

curl http://localhost:8080/health
curl http://localhost:8080/profile
curl http://localhost:8080/secrets

curl http://localhost:8081/health
curl http://localhost:8081/profile
curl http://localhost:8081/secrets
```

## 🔧 Configuration

### Application.yml par profil

Chaque profil a sa propre configuration dans `src/main/resources/application-{profil}.yml` :

**DEV** (`src/main/resources/application-dev.yml`):
```yaml
spring:
  cloud:
    vault:
      uri: http://localhost:8200
      token: ${VAULT_TOKEN:root-token}
      kv:
        enabled: true
        backend: secret
        application-name: ap10981/dev/secret-dev  # Chemin des secrets dans Vault
      authentication: TOKEN
```

**TEST** (`src/main/resources/application-test.yml`):
```yaml
spring:
  cloud:
    vault:
      uri: http://localhost:8200
      token: ${VAULT_TOKEN:root-token}
      kv:
        enabled: true
        backend: secret
        application-name: ap10981/test/secret-test  # Chemin des secrets dans Vault
      authentication: TOKEN
```

**PRE-PROD** (`src/main/resources/application-pprod.yml`):
```yaml
spring:
  cloud:
    vault:
      uri: http://localhost:8200
      token: ${VAULT_TOKEN:root-token}
      kv:
        enabled: true
        backend: secret
        application-name: ap10981/pprod/secret-pprod  # Chemin des secrets dans Vault
      authentication: TOKEN
```

**PROD** (`src/main/resources/application-prod.yml`):
```yaml
spring:
  cloud:
    vault:
      uri: http://localhost:8200
      token: ${VAULT_TOKEN:root-token}
      kv:
        enabled: true
        backend: secret
        application-name: ap10981/prod/secret-prod  # Chemin des secrets dans Vault
      authentication: TOKEN
```

### Secrets attendus dans Vault

Chaque profil charge ses secrets depuis un chemin spécifique dans Vault:

```
secret/ap10981/dev/secret-dev
├── database.username
├── database.password
├── api.key
├── app.environment
└── app.profile

secret/ap10981/test/secret-test
├── database.username
├── database.password
├── api.key
├── app.environment
└── app.profile

secret/ap10981/pprod/secret-pprod
├── database.username
├── database.password
├── api.key
├── app.environment
└── app.profile

secret/ap10981/prod/secret-prod
├── database.username
├── database.password
├── api.key
├── app.environment
└── app.profile
```

## 🧪 Tests

### Endpoints disponibles

- `GET /health` - Statut de l'application
- `GET /secrets` - Récupération des secrets depuis Vault
- `GET /profile` - Informations sur le profil Spring actif

### Exemple de réponse pour /secrets
```json
{
  "databaseUsername": "spring_user",
  "databasePassword": "secure_password_123",
  "apiKey": "api_key_456_secret",
  "environment": "development",
  "profile": "development"
}
```

### Exemple de réponse pour /profile
```json
{
  "activeProfiles": ["dev"],
  "defaultProfiles": ["default"],
  "appProfile": "development",
  "appEnvironment": "dev"
}
```

## 🔐 Sécurité

### Bonnes pratiques implémentées

1. **Injection de dépendances** : Utilisation de `@Value` pour charger les secrets
2. **Fallback values** : Valeurs par défaut en cas d'indisponibilité de Vault
3. **Configuration externe** : Token Vault passé via variable d'environnement
4. **Renouvellement automatique** : Configuration du lifecycle de Vault

### Variables d'environnement

```bash
export VAULT_TOKEN=votre_token_vault
export VAULT_ADDR=http://localhost:8200
```

## 📚 Concepts démontrés

1. **Spring Cloud Vault Starter** - Intégration native avec Vault
2. **Bootstrap configuration** - Chargement précoce de la configuration
3. **Property injection** - Injection de propriétés depuis Vault
4. **Health checks** - Surveillance de la connectivité Vault
5. **Docker integration** - Déploiement conteneurisé

## 🛠️ Dépannage

### Problèmes courants

**Erreur de connexion à Vault :**
- Vérifiez que Vault est en cours d'exécution : `docker ps`
- Confirmez le token Vault : `cat vault-root-token.txt`
- Testez la connectivité : `curl http://localhost:8200/v1/sys/health`

**Secrets non trouvés :**
- Vérifiez la structure des secrets dans Vault
- Confirmez le path : `secret/vault-demo`
- Testez manuellement : `vault kv get secret/vault-demo`

**Application qui ne démarre pas :**
- Vérifiez les logs : `docker logs vault-demo-app`
- Confirmez le build Maven : `mvn clean package`
- Testez localement sans Docker : `mvn spring-boot:run`

## 🎯 Extensions possibles

1. **Authentification AppRole** au lieu de token
2. **Rotation automatique** des secrets
3. **Intégration avec Kubernetes**
4. **Monitoring et métriques**
5. **Tests d'intégration** avec Testcontainers

## 🌐 Multi-profil Setup

Le projet supporte 4 profils Spring Cloud Vault :

- **dev** (port 8080) - Développement
- **test** (port 8081) - Environnement de test
- **pprod** (port 8082) - Pré-production
- **prod** (port 8083) - Production

Chaque profil charge ses secrets depuis un chemin spécifique dans Vault :
- `secret/ap10981/dev/secret-dev`
- `secret/ap10981/test/secret-test`
- `secret/ap10981/pprod/secret-pprod`
- `secret/ap10981/prod/secret-prod`

Les applications sont démarrées simultanément via Docker Compose, chacune avec son propre port et son profil Spring.

---
*Démonstration Spring Cloud Vault - BNP Paribas 2026*