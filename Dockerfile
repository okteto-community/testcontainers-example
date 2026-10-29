# Builds the project the same way a normal CI job would: compile + run the test suite.
#
# Testcontainers needs a Docker daemon. Its config (testcontainers.properties, pointing to
# the dind service in the namespace) is mounted as a build secret, so it never ends up in the image.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /src
COPY pom.xml .
COPY src ./src

RUN --mount=type=secret,id=testcontainers,target=/root/.testcontainers.properties \
    mvn -B package

# Minimal runtime stage, only here so the build produces a usable image.
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /src/target/testcontainers-example-1.0.0.jar app.jar
COPY --from=build /src/target/lib ./lib

CMD ["java", "-cp", "app.jar:lib/*", "example.HelloWorld"]
