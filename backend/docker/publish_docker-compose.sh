#!/bin/bash

if [ $# -lt 3 ]; then
    echo "Uso: $0 <usuario> <repositorio> <tag>"
    echo "Ejemplo: $0 eduamongus spring-webapp-compose 0.1.0"
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
IMAGE_NAME="$2"
TAG="$3"
LOCAL_IMAGE="$IMAGE_NAME:$TAG"
DOCKERHUB_IMAGE="$1/$IMAGE_NAME:$TAG"

docker tag "$LOCAL_IMAGE" "$DOCKERHUB_IMAGE"
docker push "$DOCKERHUB_IMAGE"
