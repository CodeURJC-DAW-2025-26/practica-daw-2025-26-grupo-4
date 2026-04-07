#!/bin/bash

# Script para publicar la imagen Docker en DockerHub
# Uso: ./publish_image.sh <usuario_dockerhub> <nombre_imagen> <tag>
# Ejemplo: ./publish_image.sh eduamongus spring-webapp-compose 0.1.0

if [ $# -lt 3 ]; then
    echo "Uso: $0 <usuario_dockerhub> <nombre_imagen> <tag>"
    echo "Ejemplo: $0 eduamongus spring-webapp 0.1.0"
    exit 1
fi

DOCKERHUB_USER=$1
IMAGE_NAME=$2
TAG=$3
LOCAL_IMAGE="$IMAGE_NAME:$TAG"
DOCKERHUB_IMAGE="$DOCKERHUB_USER/$IMAGE_NAME:$TAG"

echo "================================"
echo "Publicando imagen en DockerHub"
echo "Usuario: $DOCKERHUB_USER"
echo "Imagen: $DOCKERHUB_IMAGE"
echo "================================"

# Taggear la imagen local para DockerHub
echo ""
echo "Taggeando imagen: $LOCAL_IMAGE -> $DOCKERHUB_IMAGE"
docker tag "$LOCAL_IMAGE" "$DOCKERHUB_IMAGE"

if [ $? -ne 0 ]; then
    echo "✗ Error al taggear la imagen"
    exit 1
fi

# Publicar en DockerHub
echo ""
echo "Publicando imagen en DockerHub..."
docker push "$DOCKERHUB_IMAGE"

if [ $? -eq 0 ]; then
    echo ""
    echo "================================"
    echo "✓ Imagen publicada exitosamente"
    echo "Puedes descargarla con:"
    echo "docker pull $DOCKERHUB_IMAGE"
    echo "================================"
else
    echo ""
    echo "✗ Error al publicar la imagen"
    exit 1
fi
