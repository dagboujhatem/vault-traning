# Guide de Démarrage Rapide

## 🚀 Lancement de l'environnement Vault

### 1. Démarrer Vault avec Docker
```bash
cd vault
docker-compose up -d
```

### 2. Vérifier que Vault est en cours d'exécution
```bash
docker ps
# Vous devriez voir le conteneur vault-server
```

### 3. Initialiser Vault
```bash
# Sur Linux/Mac
chmod +x scripts/init-vault.sh
./scripts/init-vault.sh

# Sur Windows (PowerShell)
docker exec vault-server vault operator init -key-shares=1 -key-threshold=1
```

### 4. Noter les identifiants générés
Le script sauvegarde automatiquement :
- `root-token.txt` : Token administrateur
- `unseal-key.txt` : Clé de déverrouillage

### 5. Déverrouiller Vault (si nécessaire)
```bash
docker exec vault-server vault operator unseal VOTRE_CLE_DEVERROUILLAGE
```

### 6. Se connecter avec le token root
```bash
docker exec vault-server vault login VOTRE_TOKEN_ROOT
```

## 🧪 Test avec Postman

1. Importer la collection Postman : `postman/Vault Training.postman_collection.json`
2. Mettre à jour les variables d'environnement :
   - `vault_url` : http://localhost:8200
   - `vault_token` : votre token root
3. Exécuter les requêtes dans l'ordre

## 📚 Suivre les exercices

Les exercices se trouvent dans `training/exercises/` :
1. `exercise1-basic-secrets.md` - Gestion de base des secrets
2. `exercise2-authentication-policies.md` - Authentification et politiques
3. `exercise3-approle-auth.md` - Authentification AppRole

## ⚠️ Commandes utiles

### Arrêter Vault
```bash
cd vault
docker-compose down
```

### Réinitialiser complètement
```bash
cd vault
docker-compose down -v
rm -rf data/*
# Puis recommencer l'initialisation
```

### Vérifier les logs
```bash
docker logs vault-server
```

## 🔧 Dépannage

**Problème : Vault ne démarre pas**
- Vérifiez que le port 8200 n'est pas utilisé
- Assurez-vous que Docker est en cours d'exécution

**Problème : Erreur de permission**
- Sur Linux/Mac : `chmod +x scripts/init-vault.sh`
- Exécutez les commandes avec les bons privilèges

**Problème : Vault est scellé**
- Utilisez la commande `vault operator unseal` avec votre clé