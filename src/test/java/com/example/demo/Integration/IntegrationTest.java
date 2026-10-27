package com.example.demo.Integration;

import com.example.demo.Application.IRabbitMQSender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Path;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base of the integration tests (issue #25): the whole application (controllers, security, services, domain and JPA)
 * against a real SQL Server in Docker, started with Testcontainers. Run them with ./gradlew integrationTest.
 *
 * The database uses the image of docker compose (built from sqlserver.Dockerfile), so it has the same tables and the
 * seed users of create-database.sql, in a container of its own that is removed at the end. RabbitMQ is replaced by a Mockito mock of IRabbitMQSender, which also lets a test
 * check which event was published. Guide in the README, section "Pruebas de integración".
 */
@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTest {

    // Seed users of create-database.sql
    protected static final String ADMIN = "admin@ucaldas.test";
    protected static final String ADMIN_PASSWORD = "Admin2026*";
    protected static final String LAURA = "laura.gomez@ucaldas.test";
    protected static final String STUDENT_PASSWORD = "Estudiante2026*";
    protected static final int LAURA_STUDENT_ID = 1;

    // "Today" for the application is in Colombia (ClockConfig), not in the time zone of the machine running the tests
    protected static final ZoneId BOGOTA = ZoneId.of("America/Bogota");

    private static final String SA_PASSWORD = "your_password_123_.";

    // The image of docker compose (sqlserver.Dockerfile): same SQL Server, same create-database.sql, same seed users
    private static final String SQL_SERVER_IMAGE = "clean-architecture-api-sqlserver";

    // A container of its own for the tests, removed when they end: they never touch the data of the compose database
    private static final GenericContainer<?> SQL_SERVER = sqlServer();

    private static GenericContainer<?> sqlServer() {
        boolean imageExists = !DockerClientFactory.instance().client().listImagesCmd()
                .withReferenceFilter(SQL_SERVER_IMAGE).exec().isEmpty();
        // Whoever has not run docker compose yet (or the CI) builds it once from the same Dockerfile, with the same name
        GenericContainer<?> container = imageExists
                ? new GenericContainer<>(DockerImageName.parse(SQL_SERVER_IMAGE))
                : new GenericContainer<>(new ImageFromDockerfile(SQL_SERVER_IMAGE, false)
                        .withDockerfile(Path.of("sqlserver.Dockerfile").toAbsolutePath()));
        return container
                // The image carries the labels of docker compose: without overriding them, Docker Desktop and
                // "docker compose" would take this container as part of the compose project
                .withLabel("com.docker.compose.project", "")
                .withLabel("com.docker.compose.service", "")
                .withExposedPorts(1433)
                // Ready when create-database.sql finished: its last step loads the three convocation types
                // (the same idea as the healthcheck of docker-compose.yml)
                .waitingFor(Wait.forSuccessfulCommand("/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P \"$SA_PASSWORD\" "
                                + "-d CleanArchitectureDB -b -Q \"IF (SELECT COUNT(*) FROM ConvocationType) < 3 THROW 50000, 'loading', 1\"")
                        .withStartupTimeout(Duration.ofMinutes(5)));
    }

    static {
        SQL_SERVER.start();
        // Removed when the tests end; Testcontainers also does it, this makes it certain
        Runtime.getRuntime().addShutdownHook(new Thread(SQL_SERVER::stop));
    }

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:sqlserver://" + SQL_SERVER.getHost() + ":"
                + SQL_SERVER.getMappedPort(1433) + ";databaseName=CleanArchitectureDB;encrypt=true;trustServerCertificate=true");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> SA_PASSWORD);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    // To prepare data the api does not allow to create directly (for example a convocation that started today)
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @MockBean
    protected IRabbitMQSender eventSender;

    /** Signs in through POST /auth/login, like the front, and returns the JWT. */
    protected String tokenOf(String email, String password) throws Exception {
        String body = this.mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", password))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return this.objectMapper.readTree(body).path("data").path("token").asText();
    }

    /** Adds "Authorization: Bearer <token>" to a request. */
    protected static MockHttpServletRequestBuilder as(String token, MockHttpServletRequestBuilder request) {
        return request.header("Authorization", "Bearer " + token);
    }

    protected String json(Object value) throws Exception {
        return this.objectMapper.writeValueAsString(value);
    }

    protected JsonNode data(String responseBody) throws Exception {
        return this.objectMapper.readTree(responseBody).path("data");
    }
}
