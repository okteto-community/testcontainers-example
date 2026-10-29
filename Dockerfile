# Compiles the project (main and test classes) without running the tests.
# The `build` stage is also published as the `builder` image, where `okteto test` runs the tests.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /src
COPY pom.xml .
COPY src ./src

RUN mvn -B package -DskipTests

# Minimal runtime stage, only here so the build produces a usable image.
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /src/target/testcontainers-example-1.0.0.jar app.jar
COPY --from=build /src/target/lib ./lib

CMD ["java", "-cp", "app.jar:lib/*", "example.HelloWorld"]
