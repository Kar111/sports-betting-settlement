package com.example.sports_betting_settlement.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bets")
@Data
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long betId;

    private String userId;
    private String eventId;
    private String eventMarketId;
    private String eventWinnerId; // The one we will use to match
    private Double betAmount;

    // Status can be: PENDING, WON, LOST
    private String status;
}
