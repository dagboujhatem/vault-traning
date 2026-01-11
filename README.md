# Vault Training Project

Bienvenue dans cette formation complète sur HashiCorp Vault !

## 📚 Contenu de la Formation

Cette formation couvre les aspects fondamentaux et avancés de Vault, l'outil de gestion des secrets de HashiCorp.

### Modules de Formation

1. **Introduction à Vault**
   - Qu'est-ce que Vault ?
   - Architecture et concepts clés
   - Cas d'utilisation

2. **Installation et Configuration**
   - Installation locale
   - Configuration avec Docker
   - Première initialisation

3. **Fonctionnalités de Base**
   - Stockage de secrets
   - Authentification
   - Politiques d'accès

4. **Backends de Stockage**
   - KV Store (v1 et v2)
   - Bases de données dynamiques
   - Certificats TLS

5. **Authentification Avancée**
   - Tokens
   - Authentification LDAP/AD
   - AWS, Kubernetes, AppRole

6. **Déploiement en Production**
   - Haute disponibilité
   - Sécurité
   - Monitoring

## 🚀 Démarrage Rapide

### Prérequis

- Docker et Docker Compose
- Git
- Postman (pour les collections API)

### Lancement de l'environnement

```bash
cd vault
docker-compose up -d
```

Vault sera accessible sur: http://localhost:8200

## 📁 Structure du Projet

```
vault-training/
├── vault/
│   ├── docker-compose.yml     # Configuration Docker
│   ├── config/
│   │   └── vault-config.hcl   # Configuration Vault
│   ├── policies/
│   │   ├── admin-policy.hcl   # Politique admin
│   │   └── app-policy.hcl     # Politique application
│   └── scripts/
│       └── init-vault.sh      # Script d'initialisation
├── training/
│   ├── exercises/             # Exercices pratiques
│   └── solutions/             # Solutions
├── postman/
│   └── Vault Training.postman_collection.json  # Collection Postman
└── README.md                  # Documentation principale
```

## 🎯 Objectifs de la Formation

À la fin de cette formation, vous serez capable de :
- Installer et configurer Vault
- Gérer les secrets de manière sécurisée
- Implémenter différentes méthodes d'authentification
- Créer et gérer des politiques d'accès
- Déployer Vault en environnement de production

## 🔧 Technologies Utilisées

- HashiCorp Vault
- Docker
- Postman
- Bash/Shell scripting

## 📖 Ressources Supplémentaires

- [Documentation officielle Vault](https://www.vaultproject.io/docs)
- [Tutoriels HashiCorp Learn](https://learn.hashicorp.com/vault)
- [GitHub HashiCorp Vault](https://github.com/hashicorp/vault)

---

*Formation créée pour BNP Paribas - 2026*