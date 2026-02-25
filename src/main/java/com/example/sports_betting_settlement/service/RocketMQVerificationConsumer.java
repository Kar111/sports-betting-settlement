package com.example.sports_betting_settlement.service;

import com.example.sports_betting_settlement.dto.BetSettlementTask;
import com.example.sports_betting_settlement.repository.BetRepository;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "bet-settlements", consumerGroup = "h2-update-group")
public class RocketMQVerificationConsumer implements RocketMQListener<BetSettlementTask> {

    private final BetRepository betRepository;

    public RocketMQVerificationConsumer(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    @Override
    public void onMessage(BetSettlementTask task) {
        System.out.println("📬 RocketMQ received: " + task.betId() + " for user " + task.userId());

        betRepository.findById(task.betId()).ifPresent(bet -> {
            bet.setStatus(task.status()); // Using record accessor
            betRepository.save(bet);
            System.out.println("💾 H2 Updated: Bet status for the betId " + bet.getBetId() + " is now " + task.status() + " for the user " + task.userId());
        });
    }
}
