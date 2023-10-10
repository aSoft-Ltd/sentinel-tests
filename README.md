# sentinel-tests
A module dedicated to testing different sentinel implementations

## Hot to test Sentinel Registration Service

### Prerequisites

Make sure you have docker installed

### Steps
- run `./gradlew :sentinel-registration-service:runMongoContainer`
- run `./gradlew :sentinel-registration-service:build`
- run `./gradlew :sentinel-registration-service:removeMongoContainer`