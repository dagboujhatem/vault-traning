# Exercice 3 : Authentification AppRole

## Objectif
Configurer l'authentification AppRole pour les applications.

## Étapes

### 1. Activer l'authentification AppRole
```bash
docker exec vault-server vault auth enable approle
```

### 2. Créer un rôle
```bash
docker exec vault-server vault write auth/approle/role/myapp \
    secret_id_ttl=10m \
    token_num_uses=10 \
    token_ttl=20m \
    token_max_ttl=30m \
    secret_id_num_uses=40
```

### 3. Récupérer le Role ID
```bash
docker exec vault-server vault read auth/approle/role/myapp/role-id
ROLE_ID=$(docker exec vault-server vault read -field=role_id auth/approle/role/myapp/role-id)
```

### 4. Générer un Secret ID
```bash
docker exec vault-server vault write -f auth/approle/role/myapp/secret-id
SECRET_ID=$(docker exec vault-server vault write -field=secret_id -f auth/approle/role/myapp/secret-id)
```

### 5. S'authentifier avec AppRole
```bash
docker exec vault-server vault write auth/approle/login \
    role_id=$ROLE_ID \
    secret_id=$SECRET_ID
```

### 6. Utiliser le token obtenu
```bash
# Récupérer le token
APP_TOKEN=$(docker exec vault-server vault write -field=token auth/approle/login role_id=$ROLE_ID secret_id=$SECRET_ID)

# Utiliser le token
docker exec vault-server vault login $APP_TOKEN
docker exec vault-server vault kv get secret/myapp/database
```

## Questions
1. Quand utiliser AppRole plutôt que des tokens manuels ?
2. Comment renouveler un token AppRole ?
3. Quels sont les avantages de séparer Role ID et Secret ID ?