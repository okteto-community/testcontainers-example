# Testcontainers on Okteto: tests inside the build

The image is built and tested in the same `okteto build`, as in a regular CI job.

Testcontainers uses a Docker daemon (`dind`) deployed in the same namespace as a dependency,
before the build. `testcontainers.properties` points to it and is mounted as a build secret
in the `RUN mvn -B package` step.

```bash
# Deploy dind, build (compile + tests) and deploy the app
okteto deploy

# Build again forcing the tests to run (BuildKit skips them if the sources didn't change)
okteto build --no-cache
```

> Tests can't use bind mounts (`.withFileSystemBind(...)`): the Docker daemon runs in another pod
> and doesn't see the test files. Use `.withCopyFileToContainer(...)` instead.

For tests decoupled from the build (`okteto test`), see the
[`tests-decoupled`](https://github.com/okteto-community/testcontainers-example/tree/tests-decoupled) branch.
