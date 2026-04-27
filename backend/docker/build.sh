cd ./frontend && npm run build
cd ..

mkdir -p ./backend/java/src/main/resources/static/new/
rm -rf ./backend/java/src/main/resources/static/new/*
cp -r ./frontend/build/client/* ./backend/java/src/main/resources/static/new/