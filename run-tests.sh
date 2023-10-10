./gradlew :sentinel-server:sentinel-server-app:runAppContainer
./gradlew :sentinel-registration-api:jvmTest || echo "Tests failed"
./gradlew :sentinel-server:sentinel-server-app:removeAppContainer
./gradlew :sentinel-server:sentinel-server-app:runAppContainer
./gradlew :sentinel-registration-api:jsNodeTest || echo "Tests failed"
./gradlew :sentinel-server:sentinel-server-app:removeAppContainer
./gradlew :sentinel-server:sentinel-server-app:runAppContainer
./gradlew :sentinel-registration-api:jsBrowserTest || echo "Tests failed"
./gradlew :sentinel-server:sentinel-server-app:removeAppContainer