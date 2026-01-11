# Solution Exercice 3

## Fichiers de configuration

### main.tf
```hcl
# Authentification AppRole
resource "vault_auth_backend" "approle" {
  type = "approle"
  path = "approle"
}

resource "vault_policy" "app_policy" {
  name = "app-policy"

  policy = <<EOT
path "secret/data/myapp/*" {
  capabilities = ["read", "list"]
}

path "database/creds/app-role" {
  capabilities = ["read"]
}

path "pki/issue/app-server" {
  capabilities = ["create", "update"]
}
EOT
}

resource "vault_approle_auth_backend_role" "app" {
  backend          = vault_auth_backend.approle.path
  role_name        = "myapp"
  token_policies   = [vault_policy.app_policy.name]
  token_ttl        = 3600
  token_max_ttl    = 86400
  secret_id_ttl    = 7200
  secret_id_num_uses = 10
}

resource "vault_approle_auth_backend_role_secret_id" "app_secret" {
  backend   = vault_auth_backend.approle.path
  role_name = vault_approle_auth_backend_role.app.role_name
}

# Authentification LDAP (simulation)
resource "vault_auth_backend" "ldap" {
  type = "ldap"
  path = "ldap"
}

resource "vault_ldap_auth_backend" "ldap_config" {
  backend      = vault_auth_backend.ldap.path
  url          = "ldaps://ldap.example.com"
  binddn       = "cn=vault,ou=users,dc=example,dc=com"
  bindpass     = "vault_password"
  userdn       = "ou=users,dc=example,dc=com"
  userattr     = "uid"
  discoverdn   = false
  groupfilter  = "(&(objectClass=groupOfNames)(member={{.UserDN}}))"
  groupdn      = "ou=groups,dc=example,dc=com"
  groupattr    = "cn"
}

# Politiques avancées
resource "vault_policy" "admin_policy" {
  name = "admin-full-access"

  policy = <<EOT
# Accès complet aux secrets
path "secret/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# Gestion des engines
path "sys/mounts/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# Gestion des politiques
path "sys/policy/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# Gestion des auth methods
path "sys/auth/*" {
  capabilities = ["create", "read", "update", "delete", "sudo"]
}

# Accès aux logs et monitoring
path "sys/audit/*" {
  capabilities = ["read", "list"]
}

path "sys/health" {
  capabilities = ["read"]
}
EOT
}

resource "vault_policy" "readonly_policy" {
  name = "readonly-access"

  policy = <<EOT
# Lecture seule sur tous les secrets
path "secret/data/*" {
  capabilities = ["read", "list"]
}

path "secret/metadata/*" {
  capabilities = ["list"]
}

# Accès aux informations système en lecture seule
path "sys/capabilities-self" {
  capabilities = ["update"]
}

path "auth/token/lookup-self" {
  capabilities = ["read"]
}
EOT
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

variable "ldap_bind_password" {
  description = "Mot de passe de liaison LDAP"
  type        = string
  sensitive   = true
  default     = "vault_password"
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

output "approle_role_id" {
  description = "Role ID pour l'authentification AppRole"
  value       = vault_approle_auth_backend_role.app.role_id
  sensitive   = true
}

output "approle_secret_id" {
  description = "Secret ID pour l'authentification AppRole"
  value       = vault_approle_auth_backend_role_secret_id.app_secret.secret_id
  sensitive   = true
}

output "available_policies" {
  description = "Liste des politiques créées"
  value       = [
    vault_policy.app_policy.name,
    vault_policy.admin_policy.name,
    vault_policy.readonly_policy.name
  ]
}

output "auth_methods" {
  description = "Méthodes d'authentification configurées"
  value = {
    approle = vault_auth_backend.approle.path
    ldap    = vault_auth_backend.ldap.path
  }
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

# Récupération des identifiants AppRole
ROLE_ID=$(terraform output -raw approle_role_id)
SECRET_ID=$(terraform output -raw approle_secret_id)

# Test de l'authentification AppRole
vault write auth/approle/login role_id="$ROLE_ID" secret_id="$SECRET_ID"

# Liste des méthodes d'authentification
vault auth list

# Liste des politiques
vault policy list

# Test de politique en lecture seule
vault token create -policy=readonly-access

# Test des permissions
vault token create -policy=app-policy

# Nettoyage
terraform destroy
```

## Points clés à retenir

1. **AppRole** : Authentification par RoleID + SecretID, idéal pour les applications
2. **LDAP** : Intégration avec l'annuaire d'entreprise
3. **Politiques HCL** : Contrôle fin des permissions par path
4. **Least Privilege** : Chaque rôle n'a que les permissions nécessaires
5. **Token TTL** : Les tokens expirent automatiquement pour la sécurité