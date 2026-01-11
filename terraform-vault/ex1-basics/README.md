# Exercice 1 : Bases de Terraform avec Vault

## 🎯 Objectifs d'apprentissage

- Configurer Terraform pour travailler avec Vault
- Créer et gérer des secrets simples
- Utiliser les variables et outputs
- Comprendre le cycle de vie Terraform

## 📋 Prérequis

- Vault en cours d'exécution
- Terraform installé
- Token root Vault disponible

## 🚀 Instructions

### 1. Configuration initiale

Copiez les fichiers de configuration depuis `../setup/` :

```bash
cp ../setup/*.tf .
```

Créez un fichier `terraform.tfvars` avec vos identifiants :

```hcl
vault_token = "votre_token_root_ici"
```

### 2. Initialisation de Terraform

```bash
terraform init
```

### 3. Premier déploiement

Créez un fichier `main.tf` avec ce contenu :

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

### 4. Planification et application

```bash
terraform plan
terraform apply
```

### 5. Vérification

Vérifiez que les ressources ont été créées :

```bash
terraform show
```

Dans Vault UI ou via CLI :
```bash
vault kv get secret/myapp/database
```

## 🧪 Tests et vérifications

1. **Vérifiez l'état Terraform** :
   ```bash
   terraform state list
   ```

2. **Testez la lecture des secrets** :
   ```bash
   vault kv get secret/myapp/database
   ```

3. **Modifiez un secret** :
   Changez le mot de passe dans le code et réappliquez

## ❓ Questions de réflexion

1. Quelle est la différence entre `terraform plan` et `terraform apply` ?
2. Pourquoi utilisons-nous KV v2 plutôt que v1 ?
3. Que fait l'option `delete_all_versions = true` ?
4. Comment gérer les secrets sensibles dans le code Terraform ?

## 🔄 Nettoyage

Pour nettoyer les ressources créées :

```bash
terraform destroy
```

## 🎯 Prochain exercice

Une fois terminé, passez à `../ex2-secrets/` pour apprendre la gestion avancée des secrets.