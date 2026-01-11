# Configuration Initiale Terraform-Vault

Ce répertoire contient la configuration de base nécessaire pour tous les exercices.

## Fichiers inclus

- `provider.tf` - Configuration du provider Vault
- `variables.tf` - Variables communes
- `outputs.tf` - Sorties standards
- `backend.tf` - Backend de stockage de l'état (local pour développement)

## Configuration requise

Assurez-vous que ces variables d'environnement sont définies :

```bash
export VAULT_ADDR=http://localhost:8200
export VAULT_TOKEN=votre_token_root_de_vault
```

Ou utilisez le fichier `terraform.tfvars` pour définir les valeurs.