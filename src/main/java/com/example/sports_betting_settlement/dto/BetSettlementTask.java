package com.example.sports_betting_settlement.dto;

import java.io.Serializable;

// Serializable is required for RocketMQ to send the object over the wire
public record BetSettlementTask(
        Long betId,
        String eventWinnerId, // The ID from the Kafka outcome
        String betWinnerId    // The ID the user actually chose
) implements Serializable {}
