#!/bin/bash

echo "🔧 Configuration de Vault pour la démo Spring Cloud..."

# Attendre que Vault soit prêt
sleep 10

# Initialiser Vault
echo "Initialisation de Vault..."
init_output=$(docker exec vault-server vault operator init -key-shares=1 -key-threshold=1)

# Extraire le token root
root_token=$(echo "$init_output" | grep "Initial Root Token" | awk '{print $4}')

echo "Token Root: $root_token"

# Sauvegarder le token
echo "$root_token" > vault-root-token.txt

# Se connecter à Vault
docker exec vault-server vault login "$root_token"

# Activer le KV store
docker exec vault-server vault secrets enable -path=secret kv-v2

# Créer les secrets pour l'application
echo "Création des secrets de démonstration..."
docker exec vault-server vault kv put secret/vault-demo \
    database.username="spring_user" \
    database.password="secure_password_123" \
    api.key="api_key_456_secret" \
    app.environment="development"

# Vérifier les secrets
echo "Vérification des secrets créés :"
docker exec vault-server vault kv get secret/vault-demo

echo "✅ Configuration terminée !"
echo "Token Vault : $root_token"
echo "L'application peut maintenant se connecter à Vault"