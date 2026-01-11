# Exercice 3 : Authentification et Autorisation

## 🎯 Objectifs d'apprentissage

- Configurer différentes méthodes d'authentification
- Créer et gérer des politiques complexes
- Utiliser les roles et bindings
- Comprendre la hiérarchie des permissions

## 📋 Prérequis

- Avoir terminé les Exercices 1 et 2
- Compréhension des concepts d'authentification Vault
- Connaissance des politiques HCL

## 🚀 Instructions

### 1. Configuration de base

Copiez la configuration depuis l'exercice précédent :

```bash
cp ../ex2-secrets/*.tf .
cd ../ex3-auth
```

### 2. Activation de l'authentification AppRole

Ajoutez à votre `main.tf` :

```hcl
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
```

### 3. Configuration LDAP (simulation)

Ajoutez une méthode d'authentification simulée :

```hcl
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
```

### 4. Politiques avancées

Créez des politiques plus complexes :

```hcl
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

### 5. Variables et outputs

Ajoutez à `outputs.tf` :

```hcl
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
  value       = [vault_policy.app_policy.name, vault_policy.admin_policy.name, vault_policy.readonly_policy.name]
}
```

### 6. Déploiement et test

```bash
terraform init
terraform plan
terraform apply
```

Testez l'authentification AppRole :
```bash
# Récupérez les IDs depuis les outputs
terraform output -raw approle_role_id
terraform output -raw approle_secret_id

# Testez l'authentification
vault write auth/approle/login \
    role_id="ROLE_ID_ICI" \
    secret_id="SECRET_ID_ICI"
```

## 🧪 Tests et vérifications

1. **Listez les méthodes d'authentification** :
   ```bash
   vault auth list
   ```

2. **Vérifiez les politiques** :
   ```bash
   vault policy list
   vault policy read app-policy
   ```

3. **Testez les permissions** :
   ```bash
   # Avec un token avec politique readonly
   vault token create -policy=readonly-access
   ```

## ❓ Questions de réflexion

1. Quelle est la différence entre `token_policies` et `policies` dans AppRole ?
2. Comment implémenteriez-vous le principe du moindre privilège ?
3. Quels sont les risques de donner un accès `sudo` dans une politique ?
4. Comment gérer la rotation des secrets dans les configurations LDAP ?

## 🔄 Nettoyage

```bash
terraform destroy
```

## 🎯 Prochain exercice

Passez à `../ex4-modules/` pour apprendre à créer des modules réutilisables.