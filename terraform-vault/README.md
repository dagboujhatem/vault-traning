# Terraform avec Vault - Formation Progressive

## 🎯 Objectifs de la Formation

Cette formation vous apprendra à utiliser Terraform pour provisionner et gérer des ressources Vault de manière automatisée et reproductible.

## 📚 Structure de la Formation

### Module 1 : Bases de Terraform avec Vault
- Configuration du provider Vault
- Ressources de base (secrets, politiques)
- Variables et outputs

### Module 2 : Gestion Avancée des Secrets
- Secrets engines dynamiques
- Templates et transformations
- Gestion des certificats

### Module 3 : Authentification et Autorisation
- Configuration des méthodes d'authentification
- Gestion des politiques complexes
- Roles et bindings

### Module 4 : Modules et Réutilisabilité
- Création de modules Terraform
- Organisation du code
- Meilleures pratiques

### Module 5 : Déploiement en Production
- Workspaces Terraform
- Gestion des états
- Intégration CI/CD

## 🚀 Prérequis

- Terraform >= 1.0
- Vault en cours d'exécution (voir projet vault/)
- Compréhension de base de Vault
- Connaissance de Terraform (optionnel mais recommandé)

## 🏗️ Architecture de l'Environnement

```
terraform-vault/
├── README.md              # Documentation principale
├── setup/                 # Configuration initiale
├── ex1-basics/           # Exercice 1 : Bases
├── ex2-secrets/          # Exercice 2 : Gestion des secrets
├── ex3-auth/             # Exercice 3 : Authentification
├── ex4-modules/          # Exercice 4 : Modules
├── ex5-production/       # Exercice 5 : Production
└── solutions/            # Solutions des exercices
```

## 🎓 Méthodologie

Chaque exercice comprend :
1. **Objectifs d'apprentissage** clairement définis
2. **Instructions détaillées** étape par étape
3. **Code de départ** minimal
4. **Vérification** des résultats
5. **Questions de réflexion** pour approfondir

## 🔧 Configuration Initiale

Avant de commencer, assurez-vous que :

1. **Vault est en cours d'exécution** :
   ```bash
   cd ../vault
   docker-compose up -d
   ```

2. **Récupérez le token root** :
   ```bash
   cat root-token.txt
   ```

3. **Configurez l'environnement** :
   ```bash
   export VAULT_ADDR=http://localhost:8200
   export VAULT_TOKEN=votre_token_root
   ```

## 📈 Progression Recommandée

Suivez les exercices dans l'ordre pour une progression logique :
1. `ex1-basics/` - Commencez ici si vous débutez avec Terraform
2. `ex2-secrets/` - Pour ceux ayant déjà une base en Terraform
3. `ex3-auth/` - Niveau intermédiaire
4. `ex4-modules/` - Niveau avancé
5. `ex5-production/` - Expert

## 🆘 Support et Ressources

- [Documentation Terraform Provider Vault](https://registry.terraform.io/providers/hashicorp/vault/latest/docs)
- [Documentation officielle Vault](https://www.vaultproject.io/docs)
- Solutions disponibles dans `solutions/`

---
*Formation Terraform-Vault*