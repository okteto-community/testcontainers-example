# Testcontainers on Okteto

## Option A: tests decoupled from the build

The image is built without tests (`Dockerfile.skip-tests`) and the tests run with `okteto test`,
against a shared Docker daemon (`dind`) configured in `testcontainers.properties`.

```bash
# Shared Docker daemon (once)
okteto namespace create testcontainers
okteto deploy -f dind/okteto.yml -n testcontainers

# Build and deploy (no tests)
okteto deploy -f okteto.tests-decoupled.yml -n <your-namespace>

# Run the tests
okteto test unit -f okteto.tests-decoupled.yml -n <your-namespace>
```

## Option B: tests inside the build

The image is built with tests (`Dockerfile`). `testcontainers.properties` is passed to the build
as a secret and mounted in the `RUN mvn -B package` step, so Testcontainers uses the shared Docker daemon (`dind`).

```bash
# Shared Docker daemon (once)
okteto namespace create testcontainers
okteto deploy -f dind/okteto.yml -n testcontainers

# Build (with tests) and deploy
okteto build -f okteto.tests-in-build.yml -n <your-namespace> --no-cache
okteto deploy -f okteto.tests-in-build.yml -n <your-namespace>
```
