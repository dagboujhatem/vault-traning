#!/bin/bash

echo "🔐 Initialisation de Vault..."
echo "=============================="

# Attendre que Vault soit prêt
sleep 5

# Initialiser Vault
echo "Initialisation de Vault..."
init_output=$(docker exec vault-server vault operator init -key-shares=1 -key-threshold=1)

# Extraire le token root et la clé unseal
root_token=$(echo "$init_output" | grep "Initial Root Token" | awk '{print $4}')
unseal_key=$(echo "$init_output" | grep "Unseal Key 1" | awk '{print $4}')

echo "Token Root: $root_token"
echo "Clé Unseal: $unseal_key"

# Sauvegarder les informations
echo "$root_token" > root-token.txt
echo "$unseal_key" > unseal-key.txt

echo "✅ Vault initialisé avec succès !"
echo "Les identifiants sont sauvegardés dans root-token.txt et unseal-key.txt"
echo ""
echo "Pour déverrouiller Vault :"
echo "docker exec vault-server vault operator unseal $unseal_key"
echo ""
echo "Pour se connecter avec le token root :"
echo "docker exec vault-server vault login $root_token"