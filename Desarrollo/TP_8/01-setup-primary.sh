#!/bin/sh
set -e

# Crear usuario con rol de REPLICATION para el proceso de Streaming Replication
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER $POSTGRES_REPLICATION_USER WITH REPLICATION ENCRYPTED PASSWORD '$POSTGRES_REPLICATION_PASSWORD';
EOSQL

# Autorizar la subred de contenedores para conexiones de replicación con cifrado scram-sha-256
echo "host replication $POSTGRES_REPLICATION_USER all scram-sha-256" >> "$PGDATA/pg_hba.conf"