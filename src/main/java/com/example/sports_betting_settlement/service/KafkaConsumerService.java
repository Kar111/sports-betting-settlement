package com.example.sports_betting_settlement.service;

import com.example.sports_betting_settlement.dto.EventOutcome;
import com.example.sports_betting_settlement.model.Bet;
import com.example.sports_betting_settlement.repository.BetRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConsumerService {

    private final BetRepository betRepository;

    public KafkaConsumerService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    // This annotation tells Spring to listen to Kafka 24/7
    @KafkaListener(topics = "event-outcomes", groupId = "betting-group")
    public void processOutcome(EventOutcome outcome) {
        System.out.println("🚀 Received Outcome: " + outcome.eventName() + " (Winner: " + outcome.winnerId() + ")");

        // 1. Fetch PENDING bets for this event
        List<Bet> bets = betRepository.findByEventId(outcome.eventId());

        if (bets.isEmpty()) {
            System.out.println("⚠️ No bets found in DB for Event ID: " + outcome.eventId());
            return;
        }

        // 2. Settlement Logic
        for (Bet bet : bets) {
            if (bet.getEventWinnerId().equals(outcome.winnerId())) {
                bet.setStatus("WON");
            } else {
                bet.setStatus("LOST");
            }
            // 3. Update H2
            betRepository.save(bet);
            System.out.println("✅ Bet " + bet.getBetId() + " settled for user: " + bet.getUserId());
        }
    }
}