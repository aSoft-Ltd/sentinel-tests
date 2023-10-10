MONGODB_VERSION="7.0.0-ubuntu2204"
docker run --name mongo -d \
-p 27017:27017 \
-v tmp:/data \
-e MONGODB_INITDB_ROOT_USERNAME=root \
-e MONGODB_INITDB_ROOT_PASSWORD=pass \
mongodb/mongodb-community-server:$MONGODB_VERSION