package com.example.sports_betting_settlement.controller;

import com.example.sports_betting_settlement.dto.BetResponse;
import com.example.sports_betting_settlement.dto.EventOutcome;
import com.example.sports_betting_settlement.repository.BetRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final KafkaTemplate<String, EventOutcome> kafkaTemplate;
    private final BetRepository betRepository;
    public EventController(KafkaTemplate<String, EventOutcome> kafkaTemplate, BetRepository betRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.betRepository = betRepository;
    }

    @PostMapping("/outcome")
    public String publishOutcome(@RequestBody EventOutcome outcome) {
        // We send the outcome to the topic we defined earlier
        kafkaTemplate.send("event-outcomes", outcome);

        return "Event outcome for " + outcome.eventName() + " published to Kafka successfully!";
    }

    @GetMapping("/bets")
    public List<BetResponse> getAllBets() {
        return betRepository.findAll().stream()
                .map(bet -> new BetResponse(
                        bet.getBetId(),
                        bet.getUserId(),
                        bet.getEventId(),
                        bet.getStatus(),
                        bet.getBetAmount()
                ))
                .toList();
    }
}
