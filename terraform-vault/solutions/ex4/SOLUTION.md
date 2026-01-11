# Solution Exercice 4

## Structure des modules

### Module KV Secrets (`modules/vault-kv/main.tf`)
```hcl
variable "path" {
  description = "Path for the KV secrets engine"
  type        = string
  default     = "secret"
}

variable "secrets" {
  description = "Map of secrets to create"
  type        = map(any)
  default     = {}
}

resource "vault_mount" "kv" {
  path        = var.path
  type        = "kv"
  options     = { version = "2" }
  description = "KV Version 2 secret store"
}

resource "vault_kv_secret_v2" "secrets" {
  for_each = var.secrets

  mount               = vault_mount.kv.path
  name                = each.key
  delete_all_versions = true
  
  data_json = jsonencode(each.value)
}

output "mount_path" {
  description = "Path of the KV mount"
  value       = vault_mount.kv.path
}
```

### Module Authentification (`modules/vault-auth/main.tf`)
```hcl
variable "auth_methods" {
  description = "Map of authentication methods to configure"
  type        = map(object({
    type        = string
    description = string
  }))
  default = {}
}

variable "policies" {
  description = "Map of policies to create"
  type        = map(string)
  default     = {}
}

resource "vault_auth_backend" "auth" {
  for_each = var.auth_methods

  type        = each.value.type
  path        = each.key
  description = each.value.description
}

resource "vault_policy" "policies" {
  for_each = var.policies

  name   = each.key
  policy = each.value
}

output "auth_paths" {
  description = "Paths of configured auth methods"
  value       = { for k, v in vault_auth_backend.auth : k => v.path }
}

output "policy_names" {
  description = "Names of created policies"
  value       = keys(vault_policy.policies)
}
```

### Module PKI (`modules/vault-pki/main.tf`)
```hcl
variable "common_name" {
  description = "Common name for the root certificate"
  type        = string
}

variable "allowed_domains" {
  description = "Allowed domains for certificates"
  type        = list(string)
}

variable "ttl" {
  description = "TTL for issued certificates"
  type        = number
  default     = 3600
}

resource "vault_mount" "pki" {
  path                      = "pki"
  type                      = "pki"
  description               = "PKI secrets engine"
  default_lease_ttl_seconds = var.ttl
  max_lease_ttl_seconds     = var.ttl * 24
}

resource "vault_pki_secret_backend_root_cert" "root" {
  backend              = vault_mount.pki.path
  type                 = "internal"
  common_name          = var.common_name
  ttl                  = var.ttl * 24
  format               = "pem"
  private_key_format   = "der"
  key_type             = "rsa"
  key_bits             = 4096
  exclude_cn_from_sans = true
}

resource "vault_pki_secret_backend_role" "server" {
  backend          = vault_mount.pki.path
  name             = "server"
  allowed_domains  = var.allowed_domains
  allow_subdomains = true
  max_ttl          = var.ttl
}

output "pki_path" {
  description = "Path of the PKI mount"
  value       = vault_mount.pki.path
}

output "ca_chain" {
  description = "CA certificate chain"
  value       = vault_pki_secret_backend_root_cert.root.certificate
  sensitive   = true
}
```

## Configuration principale (`main.tf`)
```hcl
module "kv_secrets" {
  source = "./modules/vault-kv"

  path = "apps"
  
  secrets = {
    "webapp/database" = {
      username = "web_user"
      password = "web_pass_123"
      host     = "db.internal"
      port     = 5432
    }
    "webapp/api" = {
      api_key    = "api_key_456"
      api_secret = "api_secret_789"
    }
  }
}

module "authentication" {
  source = "./modules/vault-auth"

  auth_methods = {
    approle = {
      type        = "approle"
      description = "AppRole authentication"
    }
    ldap = {
      type        = "ldap"
      description = "LDAP authentication"
    }
  }

  policies = {
    "webapp-read" = <<EOT
path "${module.kv_secrets.mount_path}/data/webapp/*" {
  capabilities = ["read", "list"]
}
EOT
    "webapp-admin" = <<EOT
path "${module.kv_secrets.mount_path}/data/webapp/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}
EOT
  }
}

module "certificates" {
  source = "./modules/vault-pki"

  common_name    = "company.com"
  allowed_domains = ["*.company.com", "company.com"]
  ttl            = 3600
}
```

### variables.tf principal
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
```

### outputs.tf principal
```hcl
output "vault_address" {
  description = "Adresse du serveur Vault configuré"
  value       = var.vault_address
}

output "environment" {
  description = "Environnement actuel"
  value       = var.environment
}

output "kv_mount_path" {
  description = "Path du mount KV"
  value       = module.kv_secrets.mount_path
}

output "auth_methods" {
  description = "Chemins des méthodes d'authentification"
  value       = module.authentication.auth_paths
}

output "created_policies" {
  description = "Politiques créées"
  value       = module.authentication.policy_names
}

output "pki_path" {
  description = "Path du mount PKI"
  value       = module.certificates.pki_path
}
```

## Commandes de validation

```bash
# Initialisation
terraform init

# Validation de la configuration
terraform validate

# Planification
terraform plan

# Application
terraform apply

# Vérification de la structure des modules
terraform providers

# Test des outputs
terraform output

# Vérification dans Vault
vault secrets list
vault auth list
vault policy list

# Test de génération de certificat
vault write pki/issue/server common_name=test.company.com

# Nettoyage
terraform destroy
```

## Points clés à retenir

1. **Modularisation** : Séparation des concerns pour réutilisabilité
2. **Variables** : Paramétrage flexible des modules
3. **Outputs** : Communication entre modules
4. **for_each** : Création dynamique de ressources
5. **Dépendances** : Liens automatiques entre modules via les outputs