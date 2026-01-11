# Solution Exercice 2

## Fichiers de configuration

### main.tf
```hcl
# KV v2 Mount (hérité de l'exercice 1)
resource "vault_mount" "kvv2" {
  path        = "secret"
  type        = "kv"
  options     = { version = "2" }
  description = "KV Version 2 secret store"
}

# Database Secrets Engine
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

# PKI Secrets Engine
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

### variables.tf
```hcl
variable "vault_address" {
  description = "Adresse du serveur Vault"
  type        = string
  default     = "http://localhost:8200"
}

variable "vault_token" {
  description = "Token d'authentification Vault"
  type        = string
  sensitive   = true
}

variable "environment" {
  description = "Environnement (dev, staging, prod)"
  type        = string
  default     = "dev"
}

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

### outputs.tf
```hcl
output "vault_address" {
  description = "Adresse du serveur Vault configuré"
  value       = var.vault_address
}

output "environment" {
  description = "Environnement actuel"
  value       = var.environment
}

output "database_role" {
  description = "Nom du rôle database créé"
  value       = vault_database_secret_backend_role.app.name
}

output "pki_ca_certificate" {
  description = "Certificat CA PKI"
  value       = vault_pki_secret_backend_root_cert.root.certificate
  sensitive   = true
}

output "available_roles" {
  description = "Liste des rôles disponibles"
  value       = [vault_database_secret_backend_role.app.name, vault_pki_secret_backend_role.app_server.name]
}
```

## Commandes de validation

```bash
# Initialisation
terraform init

# Planification
terraform plan

# Application
terraform apply

# Vérification des secrets engines
vault secrets list

# Test de génération de credentials DB
vault read database/creds/app-role

# Génération de certificat
vault write pki/issue/app-server common_name=myapp.my-company.com

# Vérification des rôles
vault list pki/roles

# Nettoyage
terraform destroy
```

## Points clés à retenir

1. **Database Secrets Engine** : Génère des credentials temporaires pour les bases de données
2. **PKI Engine** : Gère les certificats TLS avec rotation automatique
3. **Dependencies** : Les ressources sont liées par leurs références Terraform
4. **Security** : Les tokens ont des TTL configurables pour limiter l'exposition