package com.example.sports_betting_settlement.dto;

import java.io.Serializable;
import java.math.BigDecimal;

// Serializable is required for RocketMQ to send the object over the wire
public record BetSettlementTask(
        Long betId,
        String userId,
        String eventId,
        String eventMarketId,
        String eventWinnerId,
        Double betAmount,
        String status  // The ID the user actually chose
) implements Serializable {}
