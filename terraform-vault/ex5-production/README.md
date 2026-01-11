# Exercice 5 : Bonnes Pratiques de Production

## 🎯 Objectifs d'apprentissage

- Configurer Terraform pour un environnement de production
- Gérer les workspaces et les environnements
- Implémenter la sécurité et le contrôle d'accès
- Automatiser les déploiements avec CI/CD

## 📋 Prérequis

- Avoir terminé tous les exercices précédents
- Compréhension avancée de Terraform
- Connaissance des pratiques DevOps

## 🚀 Instructions

### 1. Configuration multi-environnement

Créez la structure pour les différents environnements :

```bash
mkdir -p {dev,staging,prod}
```

### 2. Backend distant sécurisé

Remplacez le backend local par un backend sécurisé :

```hcl
# backend.tf
terraform {
  backend "s3" {
    bucket         = "terraform-state-${var.environment}"
    key            = "vault/terraform.tfstate"
    region         = "eu-west-1"
    encrypt        = true
    kms_key_id     = "alias/terraform-state"
    dynamodb_table = "terraform-state-lock"
  }
}
```

### 3. Workspaces Terraform

Configurez les workspaces :

```bash
# Création des workspaces
terraform workspace new dev
terraform workspace new staging
terraform workspace new prod

# Sélection du workspace
terraform workspace select dev
```

### 4. Configuration de sécurité

Créez `security.tf` :

```hcl
# Audit logging
resource "vault_audit" "file" {
  type = "file"
  
  options = {
    file_path = "/var/log/vault/audit.log"
    log_raw   = "false"
  }
}

# Rate limiting
resource "vault_quota_rate_limit" "global" {
  name = "global-rate-limit"
  path = "*"
  rate = 100
}

# Namespace isolation (Enterprise)
resource "vault_namespace" "apps" {
  path = "apps"
}

resource "vault_namespace" "infrastructure" {
  path = "infrastructure"
}
```

### 5. Gestion des versions et dépendances

Créez `versions.tf` :

```hcl
terraform {
  required_version = "~> 1.5"

  required_providers {
    vault = {
      source  = "hashicorp/vault"
      version = "~> 3.15"
    }
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # Remote state locking
  backend "s3" {
    bucket         = "terraform-state-${local.environment}"
    key            = "vault/terraform.tfstate"
    region         = "eu-west-1"
    encrypt        = true
    kms_key_id     = "alias/terraform-state"
    dynamodb_table = "terraform-state-lock"
  }
}

locals {
  environment = terraform.workspace
  tags = {
    Environment = local.environment
    Project     = "vault-infrastructure"
    ManagedBy   = "terraform"
  }
}
```

### 6. Pipeline CI/CD

Créez `.github/workflows/terraform.yml` :

```yaml
name: Terraform CI/CD

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  terraform:
    name: Terraform
    runs-on: ubuntu-latest
    steps:
    - name: Checkout
      uses: actions/checkout@v3

    - name: Setup Terraform
      uses: hashicorp/setup-terraform@v2
      with:
        terraform_version: 1.5.*

    - name: Terraform Format
      run: terraform fmt -check

    - name: Terraform Init
      run: terraform init

    - name: Terraform Validate
      run: terraform validate

    - name: Terraform Plan
      run: terraform plan -input=false
      env:
        VAULT_ADDR: ${{ secrets.VAULT_ADDR }}
        VAULT_TOKEN: ${{ secrets.VAULT_TOKEN }}

    - name: Terraform Apply (on main branch)
      if: github.ref == 'refs/heads/main'
      run: terraform apply -auto-approve -input=false
      env:
        VAULT_ADDR: ${{ secrets.VAULT_ADDR }}
        VAULT_TOKEN: ${{ secrets.VAULT_TOKEN }}
```

### 7. Configuration spécifique par environnement

Créez `env/dev/main.tf` :

```hcl
provider "vault" {
  address = "https://vault-dev.company.com"
  token   = var.vault_token
}

module "vault_infrastructure" {
  source = "../../modules/vault-complete"

  environment     = "dev"
  enable_audit    = false
  enable_monitoring = false
  
  kv_secrets = {
    "test/app" = {
      debug_mode = true
      log_level  = "debug"
    }
  }

  policies = {
    "dev-admin" = templatefile("${path.module}/policies/admin.hcl.tmpl", {
      environment = "dev"
    })
  }
}
```

### 8. Tests automatisés

Créez `test/integration_test.go` :

```go
package test

import (
    "testing"
    "github.com/gruntwork-io/terratest/modules/terraform"
    "github.com/stretchr/testify/assert"
)

func TestVaultInfrastructure(t *testing.T) {
    terraformOptions := &terraform.Options{
        TerraformDir: "../",
        Vars: map[string]interface{}{
            "environment": "test",
        },
    }

    defer terraform.Destroy(t, terraformOptions)
    terraform.InitAndApply(t, terraformOptions)

    // Test KV secrets
    output := terraform.Output(t, terraformOptions, "kv_mount_path")
    assert.Equal(t, "secret", output)
}
```

## 🧪 Tests et vérifications

1. **Testez les workspaces** :
   ```bash
   terraform workspace list
   terraform workspace select dev
   ```

2. **Vérifiez la configuration de sécurité** :
   ```bash
   vault audit list
   vault list sys/quotas/rate-limit/
   ```

3. **Testez le pipeline localement** :
   ```bash
   terraform fmt
   terraform validate
   terraform plan
   ```

## ❓ Questions de réflexion

1. Pourquoi utiliser des workspaces plutôt que des répertoires séparés ?
2. Quels sont les risques de stocker l'état Terraform localement en production ?
3. Comment gérez-vous le versioning des modules en production ?
4. Quelles métriques surveillez-vous pour monitorer l'infrastructure Vault ?

## 🔄 Nettoyage

```bash
# Pour chaque workspace
terraform workspace select dev
terraform destroy
terraform workspace select staging
terraform destroy
terraform workspace select prod
terraform destroy
```

## 🎉 Félicitations !

Vous avez terminé la formation complète Terraform-Vault !