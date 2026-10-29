# Testcontainers on Okteto: tests decoupled from the build

The image is built without running the tests. `okteto test` runs them on the `builder` image,
which already has the compiled classes, so the project is compiled only once.

Testcontainers uses a Docker daemon (`dind`) deployed in the same namespace as a dependency
(`dind/okteto.yml` in the `main` branch).

```bash
# Deploy dind, build (no tests) and deploy the app
okteto deploy

# Run the tests
okteto test unit
```

> Tests can't use bind mounts (`.withFileSystemBind(...)`): the Docker daemon runs in another pod
> and doesn't see the test files. Use `.withCopyFileToContainer(...)` instead.

For tests inside the build, see the [`main`](https://github.com/okteto-community/testcontainers-example) branch.
