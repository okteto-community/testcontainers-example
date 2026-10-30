package example

import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

import java.sql.DriverManager

/** The "hello world" of a Testcontainers-backed test.
  *
  * It needs a reachable Docker daemon at TEST time, i.e. while `mvn package` runs. That is exactly the requirement an
  * Okteto image build cannot satisfy, because the build runs inside BuildKit where no Docker daemon is exposed.
  */
final class HelloWorldSpec extends AnyFunSuite with BeforeAndAfterAll {

  // The Docker daemon runs in another pod (dind), so it can't see local files: bind mounts
  // (`withFileSystemBind`) don't work. `withCopyFileToContainer` sends the file through the Docker API.
  private lazy val postgres: PostgreSQLContainer[_] = new PostgreSQLContainer("postgres:15-alpine")
    .withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"), "/docker-entrypoint-initdb.d/init.sql")

  override def beforeAll(): Unit = postgres.start()

  override def afterAll(): Unit = postgres.stop()

  test("greeting is rendered") {
    assert(HelloWorld.greeting("world") == "Hello, world!")
  }

  test("postgres container answers a query") {
    val conn = DriverManager.getConnection(postgres.getJdbcUrl, postgres.getUsername, postgres.getPassword)
    try {
      val rs = conn.createStatement().executeQuery("select 1")
      assert(rs.next())
      assert(rs.getInt(1) == 1)
    } finally conn.close()
  }

  test("postgres container is initialized with init.sql") {
    val conn = DriverManager.getConnection(postgres.getJdbcUrl, postgres.getUsername, postgres.getPassword)
    try {
      val rs = conn.createStatement().executeQuery("select name from greetings")
      assert(rs.next())
      assert(HelloWorld.greeting(rs.getString(1)) == "Hello, world!")
    } finally conn.close()
  }
}
