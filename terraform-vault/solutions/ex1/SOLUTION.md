# Solution Exercice 1

## Fichiers de configuration

### main.tf
```hcl
resource "vault_mount" "kvv2" {
  path        = "secret"
  type        = "kv"
  options     = { version = "2" }
  description = "KV Version 2 secret store"
}

resource "vault_kv_secret_v2" "database_creds" {
  mount               = vault_mount.kvv2.path
  name                = "myapp/database"
  cas                 = 1
  delete_all_versions = true
  
  data_json = jsonencode({
    username = "admin"
    password = "supersecret123"
    host     = "localhost"
    port     = 5432
  })
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

output "secret_path" {
  description = "Chemin du secret créé"
  value       = "${vault_mount.kvv2.path}/${vault_kv_secret_v2.database_creds.name}"
}
```

### terraform.tfvars
```hcl
vault_token = "votre_token_root_ici"
```

## Commandes de validation

```bash
# Initialisation
terraform init

# Planification
terraform plan

# Application
terraform apply

# Vérification
terraform show
vault kv get secret/myapp/database

# Nettoyage
terraform destroy
```