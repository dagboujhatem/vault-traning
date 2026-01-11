# Résumé du Projet de Formation Vault

## 🎯 Ce qui a été créé

### Structure du projet
```
vault-training/
├── README.md                           # Documentation principale
├── QUICKSTART.md                       # Guide de démarrage rapide
├── vault/
│   ├── docker-compose.yml              # Configuration Docker pour Vault
│   ├── config/
│   │   └── vault-config.hcl            # Configuration Vault
│   ├── policies/
│   │   ├── admin-policy.hcl            # Politique admin (accès complet)
│   │   └── app-policy.hcl              # Politique application (accès limité)
│   └── scripts/
│       └── init-vault.sh               # Script d'initialisation automatique
├── training/
│   └── exercises/
│       ├── exercise1-basic-secrets.md      # Exercice 1 : Gestion de base
│       ├── exercise2-authentication-policies.md  # Exercice 2 : Auth & politiques
│       └── exercise3-approle-auth.md       # Exercice 3 : Authentification AppRole
└── postman/
    └── Vault Training.postman_collection.json  # Collection Postman complète
```

## 🚀 Fonctionnalités incluses

### 1. Environnement Docker
- Container Vault officiel
- Configuration persistante
- Ports mappés (8200 pour l'UI/API)
- Volumes pour données et configuration

### 2. Configuration prête à l'emploi
- Stockage fichier (facile pour développement)
- Interface web activée
- Configuration réseau isolée

### 3. Scripts utilitaires
- Initialisation automatique
- Sauvegarde des tokens et clés
- Instructions claires

### 4. Politiques de sécurité
- Politique admin (accès complet)
- Politique application (accès restreint)

### 5. Formation pratique
- 3 exercices progressifs
- Explications détaillées
- Questions de réflexion

### 6. Collection Postman
- Requêtes organisées par catégories :
  - Santé et statut
  - Authentification
  - Gestion des secrets
  - Politiques
  - Authentification AppRole
- Variables configurables
- Prêt à l'emploi

## 📋 Prérequis

- Docker Desktop (avec WSL2 sur Windows)
- Git
- Postman (optionnel mais recommandé)
- Terminal/PowerShell

## 🎯 Comment commencer

1. **Démarrer l'environnement :**
   ```bash
   cd vault
   docker-compose up -d
   ```

2. **Initialiser Vault :**
   ```bash
   # Sur Linux/Mac
   chmod +x scripts/init-vault.sh
   ./scripts/init-vault.sh
   
   # Sur Windows
   docker exec vault-server vault operator init -key-shares=1 -key-threshold=1
   ```

3. **Suivre les exercices :**
   Commencer par `training/exercises/exercise1-basic-secrets.md`

4. **Utiliser Postman :**
   Importer `postman/Vault Training.postman_collection.json`

## 🔧 Notes importantes

- Les identifiants sont sauvegardés dans `vault/root-token.txt` et `vault/unseal-key.txt`
- Vault doit être déverrouillé après redémarrage
- Pour un environnement de production, utiliser un backend de stockage externe (Consul, AWS, etc.)

## 🆘 Support

En cas de problème :
1. Vérifier que Docker est en cours d'exécution
2. Consulter le fichier `QUICKSTART.md`
3. Vérifier les logs : `docker logs vault-server`

---
*Formation complète prête à utiliser - BNP Paribas 2026*