# Exercice 2 : Authentification et politiques

## Objectif
Configurer l'authentification par tokens et appliquer des politiques.

## Étapes

### 1. Créer une politique
Créer un fichier `developer-policy.hcl` :
```hcl
path "secret/data/developers/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

path "secret/metadata/developers/*" {
  capabilities = ["list"]
}
```

Écrire la politique dans Vault :
```bash
docker exec -i vault-server vault policy write developers - < developer-policy.hcl
```

### 2. Créer un token avec la politique
```bash
docker exec vault-server vault token create -policy=developers
```

### 3. Utiliser le nouveau token
```bash
# Stocker le token
NEW_TOKEN="s.xxxxxxxxxxxx"  # Remplacer par le token généré

# Se connecter avec le nouveau token
docker exec vault-server vault login $NEW_TOKEN

# Tester les permissions
docker exec vault-server vault kv put secret/developers/project1/api_key value=myapikey123
docker exec vault-server vault kv get secret/developers/project1/api_key
```

### 4. Tester les limitations
Essayez d'accéder à des chemins non autorisés :
```bash
docker exec vault-server vault kv get secret/myapp/database  # Devrait échouer
```

## Questions
1. Quelle est la différence entre les tokens service et batch ?
2. Comment révoquer un token ?
3. Qu'est-ce qu'une politique par défaut ?