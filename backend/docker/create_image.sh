#!/bin/bash

# Script para construir la imagen Docker
# Uso: ./create_image.sh <nombre_imagen> <tag>
# Ejemplo: ./create_image.sh spring-webapp-compose latest

if [ $# -lt 2 ]; then
    echo "Uso: $0 <nombre_imagen> <tag>"
    echo "Ejemplo: $0 spring-webapp-compose latest"
    exit 1
fi

IMAGE_NAME=$1
TAG=$2
FULL_IMAGE="$IMAGE_NAME:$TAG"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "================================"
echo "Construyendo imagen Docker: $FULL_IMAGE"
echo "================================"

docker build --platform linux/amd64 -t "$FULL_IMAGE" -f "$SCRIPT_DIR/Dockerfile" "$SCRIPT_DIR/../.."

if [ $? -eq 0 ]; then
    echo ""
    echo "================================"
    echo "✓ Imagen creada exitosamente: $FULL_IMAGE"
    echo "================================"
else
    echo ""
    echo "✗ Error al crear la imagen"
    exit 1
fi
