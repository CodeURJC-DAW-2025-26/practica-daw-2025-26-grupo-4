#!/usr/bin/env bash
set -euo pipefail

if ! command -v docker >/dev/null 2>&1; then
  echo "Error: Docker no esta instalado o no esta en PATH." >&2
  exit 1
fi

if ! docker compose publish --help >/dev/null 2>&1; then
  echo "Error: tu version de Docker Compose no soporta 'docker compose publish'." >&2
  echo "Actualiza Docker Desktop / Docker Compose a una version reciente." >&2
  exit 1
fi

if [ "$#" -lt 1 ] || [ "$#" -gt 3 ]; then
  echo "Uso: $0 <dockerhub_user> [compose_repo_name] [tag]" >&2
  echo "Ejemplo: $0 miusuario aplicacion latest" >&2
  exit 1
fi

DOCKERHUB_USER="$1"
COMPOSE_REPO_NAME="${2:-scam-g18-compose}"
TAG="${3:-latest}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yml"

if [ ! -f "$COMPOSE_FILE" ]; then
  echo "Error: No se encontro $COMPOSE_FILE" >&2
  exit 1
fi

TARGET_REF="$DOCKERHUB_USER/$COMPOSE_REPO_NAME:$TAG"

echo "Publicando docker-compose.yml como Compose OCI Artifact: $TARGET_REF"
echo "(No se publica como imagen Docker)"
docker compose -f "$COMPOSE_FILE" publish "$TARGET_REF" -y

echo "Compose publicado correctamente: oci://$TARGET_REF"