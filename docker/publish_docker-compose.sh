#!/bin/bash

if [ $# -lt 3 ]; then
    echo "Uso: $0 <usuario> <repositorio> <tag>"
    echo "Ejemplo: $0 eduamongus spring-webapp-compose-compose 0.1.0"
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
docker compose -f "$SCRIPT_DIR/docker-compose.yml" publish "$1/$2:$3" --with-env
