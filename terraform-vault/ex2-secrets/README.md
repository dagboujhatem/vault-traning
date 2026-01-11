# Exercice 2 : Gestion Avancée des Secrets

## 🎯 Objectifs d'apprentissage

- Travailler avec différents types de secrets engines
- Utiliser les templates et transformations
- Gérer les certificats TLS
- Comprendre les dépendances entre ressources

## 📋 Prérequis

- Avoir terminé l'Exercice 1
- Compréhension des bases de Terraform
- Vault avec KV v2 fonctionnel

## 🚀 Instructions

### 1. Configuration de base

Depuis le répertoire `ex1-basics/`, copiez votre configuration :

```bash
cp *.tf ../ex2-secrets/
cd ../ex2-secrets
```

### 2. Ajout d'un secrets engine de type Database

Ajoutez au fichier `main.tf` :

```hcl
resource "vault_mount" "database" {
  path        = "database"
  type        = "database"
  description = "Database secrets engine"
}

resource "vault_database_secret_backend_connection" "postgres" {
  backend       = vault_mount.database.path
  name          = "postgresql"
  allowed_roles = ["app-role"]

  postgresql {
    connection_url = "postgresql://{{username}}:{{password}}@localhost:5432/myapp"
  }
}

resource "vault_database_secret_backend_role" "app" {
  backend               = vault_mount.database.path
  name                  = "app-role"
  db_name               = vault_database_secret_backend_connection.postgres.name
  creation_statements   = ["CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}';"]
  default_ttl           = 3600
  max_ttl               = 86400
}
```

### 3. Configuration des certificats PKI

Ajoutez également la gestion des certificats :

```hcl
resource "vault_mount" "pki" {
  path                      = "pki"
  type                      = "pki"
  description               = "PKI secrets engine"
  default_lease_ttl_seconds = 3600
  max_lease_ttl_seconds     = 86400
}

resource "vault_pki_secret_backend_root_cert" "root" {
  backend              = vault_mount.pki.path
  type                 = "internal"
  common_name          = "my-company.com"
  ttl                  = 86400
  format               = "pem"
  private_key_format   = "der"
  key_type             = "rsa"
  key_bits             = 4096
  exclude_cn_from_sans = true
}

resource "vault_pki_secret_backend_role" "app_server" {
  backend          = vault_mount.pki.path
  name             = "app-server"
  allowed_domains  = ["myapp.my-company.com"]
  allow_subdomains = true
  max_ttl          = 3600
}
```

### 4. Variables et structure

Créez un fichier `variables.tf` spécifique :

```hcl
variable "db_username" {
  description = "Nom d'utilisateur de la base de données"
  type        = string
  default     = "vault_admin"
}

variable "db_password" {
  description = "Mot de passe de la base de données"
  type        = string
  sensitive   = true
}

variable "app_domains" {
  description = "Domaines autorisés pour les certificats"
  type        = list(string)
  default     = ["myapp.my-company.com", "api.myapp.my-company.com"]
}
```

### 5. Déploiement et test

```bash
terraform init
terraform plan
terraform apply
```

Générez un certificat de test :
```bash
vault write pki/issue/app-server common_name=myapp.my-company.com
```

## 🧪 Tests et vérifications

1. **Vérifiez les secrets engines** :
   ```bash
   vault secrets list
   ```

2. **Testez la génération de credentials DB** :
   ```bash
   vault read database/creds/app-role
   ```

3. **Vérifiez les certificats** :
   ```bash
   vault list pki/roles
   ```

## ❓ Questions de réflexion

1. Quelle est l'utilité du `cas` (Check And Set) dans KV v2 ?
2. Comment les leases fonctionnent-ils avec les secrets dynamiques ?
3. Pourquoi utiliser un root certificat `internal` plutôt qu'importé ?
4. Quelles sont les implications de sécurité des différents types de secrets engines ?

## 🔄 Nettoyage

```bash
terraform destroy
```

## 🎯 Prochain exercice

Passez à `../ex3-auth/` pour configurer l'authentification et les politiques.