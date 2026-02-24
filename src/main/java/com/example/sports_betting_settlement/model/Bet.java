package com.example.sports_betting_settlement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bets")
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

    public Long getBetId() {
        return betId;
    }

    public void setBetId(Long betId) {
        this.betId = betId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventMarketId() {
        return eventMarketId;
    }

    public void setEventMarketId(String eventMarketId) {
        this.eventMarketId = eventMarketId;
    }

    public String getEventWinnerId() {
        return eventWinnerId;
    }

    public void setEventWinnerId(String eventWinnerId) {
        this.eventWinnerId = eventWinnerId;
    }

    public Double getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Double betAmount) {
        this.betAmount = betAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
