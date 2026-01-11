#!/bin/bash

echo "🔧 Configuration de Vault avec les secrets par profil..."

# Attendre que Vault soit prêt
sleep 10

# Initialiser Vault si nécessaire
if [ ! -f vault-root-token.txt ]; then
    echo "Initialisation de Vault..."
    init_output=$(docker exec vault-server vault operator init -key-shares=1 -key-threshold=1)
    
    # Extraire le token root
    root_token=$(echo "$init_output" | grep "Initial Root Token" | awk '{print $4}')
    
    echo "Token Root: $root_token"
    
    # Sauvegarder le token
    echo "$root_token" > vault-root-token.txt
else
    root_token=$(cat vault-root-token.txt)
fi

# Se connecter à Vault
docker exec vault-server vault login "$root_token"

# Activer le KV store si nécessaire
docker exec vault-server vault secrets enable -path=secret kv-v2

# Créer les secrets pour chaque profil
echo "Création des secrets pour le profil DEV..."
docker exec vault-server vault kv put secret/ap10981/dev/secret-dev \
    database.username="dev_user" \
    database.password="dev_password_123" \
    api.key="dev_api_key_456" \
    app.environment="development" \
    app.profile="development"

echo "Création des secrets pour le profil TEST..."
docker exec vault-server vault kv put secret/ap10981/test/secret-test \
    database.username="test_user" \
    database.password="test_password_456" \
    api.key="test_api_key_789" \
    app.environment="test" \
    app.profile="testing"

echo "Création des secrets pour le profil PRE-PRODUCTION..."
docker exec vault-server vault kv put secret/ap10981/pprod/secret-pprod \
    database.username="pprod_user" \
    database.password="pprod_password_789" \
    api.key="pprod_api_key_012" \
    app.environment="pprod" \
    app.profile="pre-production"

echo "Création des secrets pour le profil PRODUCTION..."
docker exec vault-server vault kv put secret/ap10981/prod/secret-prod \
    database.username="prod_user" \
    database.password="prod_password_012" \
    api.key="prod_api_key_345" \
    app.environment="production" \
    app.profile="production"

# Vérifier les secrets créés
echo "Vérification des secrets pour chaque profil :"
echo "DEV:"
docker exec vault-server vault kv get secret/ap10981/dev/secret-dev
echo ""
echo "TEST:"
docker exec vault-server vault kv get secret/ap10981/test/secret-test
echo ""
echo "PRE-PROD:"
docker exec vault-server vault kv get secret/ap10981/pprod/secret-pprod
echo ""
echo "PROD:"
docker exec vault-server vault kv get secret/ap10981/prod/secret-prod

echo "✅ Configuration terminée !"
echo "Token Vault : $root_token"
echo "Secrets créés pour les profils : dev, test, pprod, prod"