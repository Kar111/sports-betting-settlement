package com.example.sports_betting_settlement;

import com.example.sports_betting_settlement.controller.EventController;
import com.example.sports_betting_settlement.dto.EventOutcome;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    KafkaTemplate<String, EventOutcome> kafkaTemplate;

    @Test
    void publishOutcome_callsKafkaTemplateSend_andReturnsMessage() throws Exception {
        // given
        EventOutcome outcome = new EventOutcome("E1", "Match 1", "TEAM_A");

        // KafkaTemplate.send returns a Future; we don't need to stub it unless your code blocks on it.
        // Some KafkaTemplate implementations return null in mocks; it's fine since we don't use it.

        // when + then
        mockMvc.perform(
                        post("/api/events/outcome")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(outcome))
                )
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("published to Kafka successfully")));

        // verify send called with correct topic and payload
        ArgumentCaptor<EventOutcome> captor = ArgumentCaptor.forClass(EventOutcome.class);
        verify(kafkaTemplate).send(org.mockito.Mockito.eq("event-outcomes"), captor.capture());

        assertThat(captor.getValue().eventId()).isEqualTo("E1");
        assertThat(captor.getValue().winnerId()).isEqualTo("TEAM_A");
    }
}