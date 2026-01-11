# Exercice 1 : Gestion de base des secrets

## Objectif
Apprendre à stocker, lire et supprimer des secrets dans Vault.

## Étapes

### 1. Vérifier l'état de Vault
```bash
docker exec vault-server vault status
```

### 2. Se connecter à Vault
Utilisez le token root sauvegardé dans `root-token.txt` :
```bash
docker exec vault-server vault login $(cat root-token.txt)
```

### 3. Activer le KV store (si nécessaire)
```bash
docker exec vault-server vault secrets enable -path=secret kv-v2
```

### 4. Stocker un secret
```bash
docker exec vault-server vault kv put secret/myapp/database \
    username=admin \
    password=supersecret123 \
    host=localhost \
    port=5432
```

### 5. Lire un secret
```bash
docker exec vault-server vault kv get secret/myapp/database
```

### 6. Lire une valeur spécifique
```bash
docker exec vault-server vault kv get -field=password secret/myapp/database
```

### 7. Mettre à jour un secret
```bash
docker exec vault-server vault kv put secret/myapp/database \
    username=admin \
    password=newpassword456 \
    host=localhost \
    port=5432
```

### 8. Supprimer un secret
```bash
docker exec vault-server vault kv delete secret/myapp/database
```

## Questions
1. Quelle est la différence entre `kv put` et `kv patch` ?
2. Comment lister tous les secrets dans un path ?
3. Que se passe-t-il quand on supprime un secret dans KV v2 ?