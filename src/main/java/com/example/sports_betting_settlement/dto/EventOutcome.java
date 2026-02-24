package com.example.sports_betting_settlement.dto;

/**
 * Representing the Event Outcome according to requirement 1a:
 * i. Event ID
 * ii. Event Name
 * iii. Event Winner ID
 */
public record EventOutcome(
        String eventId,
        String eventName,
        String winnerId
) {}
