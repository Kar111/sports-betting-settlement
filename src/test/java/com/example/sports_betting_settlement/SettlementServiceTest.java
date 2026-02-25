package com.example.sports_betting_settlement;

import com.example.sports_betting_settlement.dto.BetSettlementTask;
import com.example.sports_betting_settlement.dto.EventOutcome;
import com.example.sports_betting_settlement.model.Bet;
import com.example.sports_betting_settlement.repository.BetRepository;
import com.example.sports_betting_settlement.service.KafkaConsumerService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock
    BetRepository betRepository;

    @Mock
    RocketMQTemplate rocketMQTemplate; // Added Mock for RocketMQ

    KafkaConsumerService settlementService;

    @Captor
    ArgumentCaptor<BetSettlementTask> taskCaptor; // Changed to capture the Task Record

    @BeforeEach
    void setUp() {
        settlementService = new KafkaConsumerService(betRepository, rocketMQTemplate);
    }

    @Test
    void processOutcome_noBets_doesNotSendToRocketMQ() {
        // given
        EventOutcome outcome = new EventOutcome("E1", "Match 1", "TEAM_A");
        when(betRepository.findByEventId("E1")).thenReturn(List.of());

        // when
        settlementService.processOutcome(outcome);

        // then
        verify(betRepository).findByEventId("E1");
        verifyNoInteractions(rocketMQTemplate); // Verify nothing was sent
    }

    @Test
    void processOutcome_singleBetWinner_dispatchesCorrectTask() {
        // given
        EventOutcome outcome = new EventOutcome("E1", "Match 1", "TEAM_A");

        Bet bet = new Bet();
        bet.setBetId(1L);
        bet.setUserId("U1");
        bet.setEventId("E1");
        bet.setEventMarketId("M1");
        bet.setEventWinnerId("TEAM_A"); // User predicted TEAM_A
        bet.setBetAmount(100.0);

        when(betRepository.findByEventId("E1")).thenReturn(List.of(bet));

        // when
        settlementService.processOutcome(outcome);

        // then
        // Verify call to RocketMQ instead of DB save
        verify(rocketMQTemplate).convertAndSend(eq("bet-settlements"), taskCaptor.capture());

        BetSettlementTask capturedTask = taskCaptor.getValue();
        assertThat(capturedTask.status()).isEqualTo("WON");
        assertThat(capturedTask.betId()).isEqualTo(1L);
        assertThat(capturedTask.betAmount()).isEqualByComparingTo(100.00);
    }

    @Test
    void processOutcome_multipleBets_dispatchesMultipleTasks() {
        // given
        EventOutcome outcome = new EventOutcome("E1", "Match 1", "TEAM_A");

        Bet bet1 = createBet(1L, "TEAM_A"); // Winner
        Bet bet2 = createBet(2L, "TEAM_B"); // Loser

        when(betRepository.findByEventId("E1")).thenReturn(List.of(bet1, bet2));

        // when
        settlementService.processOutcome(outcome);

        // then
        verify(rocketMQTemplate, times(2)).convertAndSend(eq("bet-settlements"), taskCaptor.capture());
        List<BetSettlementTask> capturedTasks = taskCaptor.getAllValues();

        assertThat(capturedTasks.get(0).status()).isEqualTo("WON");
        assertThat(capturedTasks.get(1).status()).isEqualTo("LOST");
    }

    // Helper method to keep test code clean
    private Bet createBet(Long id, String prediction) {
        Bet bet = new Bet();
        bet.setBetId(id);
        bet.setEventId("E1");
        bet.setEventWinnerId(prediction);
        bet.setBetAmount(10.0);
        return bet;
    }
}