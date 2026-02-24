package com.example.sports_betting_settlement.controller;

import com.example.sports_betting_settlement.dto.EventOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private KafkaTemplate<String, EventOutcome> kafkaTemplate;

    @PostMapping("/outcome")
    public String publishOutcome(@RequestBody EventOutcome outcome) {
        // We send the outcome to the topic we defined earlier
        kafkaTemplate.send("event-outcomes", outcome);

        return "Event outcome for " + outcome.eventName() + " published to Kafka successfully!";
    }
}
