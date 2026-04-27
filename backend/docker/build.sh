#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
WORKSPACE_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

cd "$WORKSPACE_DIR/frontend"
npm install
npm run build

cd "$WORKSPACE_DIR"
mkdir -p ./backend/src/main/resources/static/new/
rm -rf ./backend/src/main/resources/static/new/*
cp -r ./frontend/build/client/* ./backend/src/main/resources/static/new/