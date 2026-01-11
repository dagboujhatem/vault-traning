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