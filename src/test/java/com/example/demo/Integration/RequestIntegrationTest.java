package com.example.demo.Integration;

import com.example.demo.Application.Events.RequestStatusChangedEvent;
import com.example.demo.Domain.Enums.EnumStateRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Example 2 of the integration tests (issue #25): a student applies to a convocation. It goes through security
 * (the student comes from the token), the domain (automatic verification of the requirements), SQL Server and
 * the publication of the event. The other flows of CU-002 are pending in #35.
 */
class RequestIntegrationTest extends IntegrationTest {

    @Test
    void aStudentAppliesTheRequestIsInHerNameAndTheEventIsPublished() throws Exception {
        // Arrange: the administrator creates a convocation (it must start tomorrow)...
        String admin = tokenOf(ADMIN, ADMIN_PASSWORD);
        String created = mockMvc.perform(as(admin, post("/convocation/createConvocation"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(ConvocationIntegrationTest.convocation("Beca de Integración"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode convocation = data(created);
        int convocationId = convocation.path("id").asInt();
        int requirementId = convocation.path("convocationRequirements").get(0).path("id").asInt();
        // ...and it is moved to start today, so it receives requests now (the api does not allow creating it like that)
        jdbcTemplate.update("UPDATE Convocation SET startDate = ? WHERE id = ?", LocalDate.now(BOGOTA), convocationId);

        // Act: Laura applies, and the body says studentId 4 (another student): it must be ignored
        String laura = tokenOf(LAURA, STUDENT_PASSWORD);
        mockMvc.perform(as(laura, post("/request/createRequest"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "studentId", 4,
                                "convocationId", convocationId,
                                "requirements", List.of(Map.of("convocationRequirementId", requirementId, "value", "4.6"))))))
                // Assert: the request is Laura's (from the token) and meets the requirement, so it is ACTIVE
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Su solicitud ha sido creada exitosamente y cumple con todos los requisitos"))
                .andExpect(jsonPath("$.data.studentId").value(LAURA_STUDENT_ID))
                .andExpect(jsonPath("$.data.state").value("ACTIVE"));

        // ...and the event for the email was published, with no previous state because the request is new
        ArgumentCaptor<RequestStatusChangedEvent> event = ArgumentCaptor.forClass(RequestStatusChangedEvent.class);
        verify(eventSender).publishRequestStatusChanged(event.capture());
        assertEquals(LAURA, event.getValue().getStudentEmail());
        assertEquals("Beca de Integración", event.getValue().getConvocationTitle());
        assertNull(event.getValue().getPreviousState());
        assertEquals(EnumStateRequest.ACTIVE, event.getValue().getNewState());
    }
}
