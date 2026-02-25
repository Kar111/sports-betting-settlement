package com.example.sports_betting_settlement.dto;

public record BetResponse(
        Long betId,
        String userId,
        String eventId,
        String status,
        Double betAmount
) {}
