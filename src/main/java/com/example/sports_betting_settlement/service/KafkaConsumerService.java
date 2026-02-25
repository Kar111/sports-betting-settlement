package com.example.sports_betting_settlement.service;

import com.example.sports_betting_settlement.dto.BetSettlementTask;
import com.example.sports_betting_settlement.dto.EventOutcome;
import com.example.sports_betting_settlement.model.Bet;
import com.example.sports_betting_settlement.repository.BetRepository;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConsumerService {

    private final BetRepository betRepository;
    private final RocketMQTemplate rocketMQTemplate;

    public KafkaConsumerService(BetRepository betRepository, RocketMQTemplate rocketMQTemplate) {
        this.betRepository = betRepository;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    // This annotation tells Spring to listen to Kafka 24/7
    @KafkaListener(topics = "event-outcomes", groupId = "betting-group")
    public void processOutcome(EventOutcome outcome) {
        System.out.println("🚀 Kafka: Received Outcome for " + outcome.eventName());

        // 1. Match outcome to bets in DB
        List<Bet> matchingBets = betRepository.findByEventId(outcome.eventId());

        if (matchingBets.isEmpty()) {
            System.out.println("⚠️ No bets found for Event ID: " + outcome.eventId());
            return;
        }
        for (Bet bet : matchingBets) {
            String finalStatus = bet.getEventWinnerId().equals(outcome.winnerId()) ? "WON" : "LOST";

            // Creating the Record instance
            BetSettlementTask task = new BetSettlementTask(
                    bet.getBetId(),
                    bet.getUserId(),
                    bet.getEventId(),
                    bet.getEventMarketId(),
                    bet.getEventWinnerId(),
                    bet.getBetAmount(),
                    finalStatus
            );

            rocketMQTemplate.convertAndSend("bet-settlements", task);
            System.out.println("📤 Kafka: Matching bet " + task.betId() + " dispatched to RocketMQ.");
        }

    }
}