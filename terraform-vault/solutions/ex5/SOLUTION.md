# Solution Exercice 5

## Configuration multi-environnement

### Structure des répertoires
```bash
├── dev/
│   └── main.tf
├── staging/
│   └── main.tf
├── prod/
│   └── main.tf
└── modules/
    └── vault-complete/
        ├── main.tf
        ├── variables.tf
        └── outputs.tf
```

## Backend sécurisé (`backend.tf`)
```hcl
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

## Configuration de sécurité (`security.tf`)
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

## Versions et dépendances (`versions.tf`)
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

## Pipeline CI/CD (`.github/workflows/terraform.yml`)
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

## Configuration par environnement

### Dev environment (`env/dev/main.tf`)
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

## Tests automatisés (`test/integration_test.go`)
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
    
    // Test auth methods
    authOutput := terraform.Output(t, terraformOptions, "auth_methods")
    assert.Contains(t, authOutput, "approle")
}
```

## Commandes de gestion

```bash
# Création des workspaces
terraform workspace new dev
terraform workspace new staging
terraform workspace new prod

# Sélection et déploiement par environnement
terraform workspace select dev
terraform init
terraform plan
terraform apply

terraform workspace select staging
terraform plan
terraform apply

terraform workspace select prod
terraform plan
terraform apply

# Vérification de la sécurité
vault audit list
vault list sys/quotas/rate-limit/
vault list sys/namespaces/

# Tests de validation
terraform fmt
terraform validate
go test -v ./test/

# Nettoyage sélectif
terraform workspace select dev
terraform destroy
```

## Variables d'environnement requises

```bash
# Pour chaque environnement
export TF_VAR_vault_token="your-vault-token"
export TF_VAR_environment="dev|staging|prod"

# Pour AWS backend
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_DEFAULT_REGION="eu-west-1"
```

## Points clés à retenir

1. **Workspaces** : Isolation des environnements dans un même codebase
2. **Backend distant** : Stockage sécurisé de l'état avec verrouillage
3. **CI/CD** : Automatisation des déploiements
4. **Tests** : Validation automatisée de l'infrastructure
5. **Sécurité** : Audit, quotas, namespaces pour l'entreprise
6. **Versioning** : Contrôle des versions des providers et modules