package com.example.sports_betting_settlement.service;

import com.example.sports_betting_settlement.model.Bet;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(topic = "bet-settlements", consumerGroup = "verify-group")
public class RocketMQVerificationConsumer implements RocketMQListener<Bet> {

    @Override
    public void onMessage(Bet bet) {
        System.out.println("📬 [RocketMQ Consumer] Received: Bet " + bet.getBetId() + " is " + bet.getStatus());
    }
}
