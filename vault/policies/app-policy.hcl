# Politique pour les applications - accès limité
path "secret/data/myapp/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

path "secret/metadata/myapp/*" {
  capabilities = ["list"]
}

# Lecture seule sur certaines données système
path "auth/token/lookup-self" {
  capabilities = ["read"]
}

path "sys/capabilities-self" {
  capabilities = ["update"]
}