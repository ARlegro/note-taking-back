package prac.demonote.support;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers()
@ActiveProfiles("test")
public abstract class PostgresTestContainer {

  @Container
  @ServiceConnection
  static final GenericContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
      .withDatabaseName("note_test")
      .withUsername("test")
      .withPassword("test");
}
