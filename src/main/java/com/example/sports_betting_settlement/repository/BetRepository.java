package com.example.sports_betting_settlement.repository;

import com.example.sports_betting_settlement.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByEventId(String eventId);
}
