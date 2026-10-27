package com.example.demo.Integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Example 1 of the integration tests (issue #25): creating a convocation, from the HTTP request to SQL Server,
 * with the three answers of the security rules. The other flows of CU-001 are pending in #35.
 */
class ConvocationIntegrationTest extends IntegrationTest {

    // A valid convocation: it starts tomorrow and has one requirement of the catalog (Requirement 1)
    static Map<String, Object> convocation(String title) {
        return Map.of(
                "title", title,
                "description", "Prueba de integración",
                "type", 1,
                "startDate", LocalDate.now(BOGOTA).plusDays(1).toString(),
                "endDate", LocalDate.now(BOGOTA).plusDays(30).toString(),
                "convocationRequirements", List.of(Map.of(
                        "name", "Promedio académico",
                        "requiredValue", "4.0",
                        "conditional", "GREATER_OR_EQUAL",
                        "description", "Promedio acumulado",
                        "requirementId", 1)));
    }

    @Test
    void theAdministratorCreatesAConvocationAndItIsSaved() throws Exception {
        String admin = tokenOf(ADMIN, ADMIN_PASSWORD);

        // The answer: 200 with the CustomResponse and the convocation created OPEN
        String body = mockMvc.perform(as(admin, post("/convocation/createConvocation"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(convocation("Monitoría de Integración"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codeState").value(200))
                .andExpect(jsonPath("$.message").value("New convocation"))
                .andExpect(jsonPath("$.data.state").value("OPEN"))
                .andExpect(jsonPath("$.data.convocationRequirements.length()").value(1))
                .andReturn().getResponse().getContentAsString();

        // It is really in SQL Server: the detail reads it back from the database
        int id = data(body).path("id").asInt();
        mockMvc.perform(as(admin, get("/convocation/getConvocationById/" + id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Monitoría de Integración"));
    }

    @Test
    void aStudentCannotCreateAConvocation() throws Exception {
        String laura = tokenOf(LAURA, STUDENT_PASSWORD);

        mockMvc.perform(as(laura, post("/convocation/createConvocation"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(convocation("No debería existir"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codeState").value(403))
                .andExpect(jsonPath("$.message").value("No tiene permiso para usar este recurso"));
    }

    @Test
    void withoutATokenTheAnswerIs401() throws Exception {
        mockMvc.perform(post("/convocation/createConvocation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(convocation("Tampoco"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codeState").value(401))
                .andExpect(jsonPath("$.message").value("Debe iniciar sesión para usar este recurso"));
    }
}
