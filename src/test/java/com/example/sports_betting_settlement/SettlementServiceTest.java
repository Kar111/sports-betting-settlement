package com.example.sports_betting_settlement;

import com.example.sports_betting_settlement.dto.EventOutcome;
import com.example.sports_betting_settlement.model.Bet;
import com.example.sports_betting_settlement.repository.BetRepository;
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

    SettlementService settlementService;

    @Captor
    ArgumentCaptor<Bet> betCaptor;

    @BeforeEach
    void setUp() {
        settlementService = new SettlementService(betRepository);
    }

    @Test
    void processOutcome_noBets_doesNotSaveAnything() {
        // given
        EventOutcome outcome = new EventOutcome("E1", "Match 1", "TEAM_A");
        when(betRepository.findByEventId("E1")).thenReturn(List.of());

        // when
        settlementService.processOutcome(outcome);

        // then
        verify(betRepository).findByEventId("E1");
        verify(betRepository, never()).save(any());
        verifyNoMoreInteractions(betRepository);
    }

    @Test
    void processOutcome_singleBetWinner_matchesOutcome_setsWonAndSaves() {
        // given
        EventOutcome outcome = new EventOutcome("E1", "Match 1", "TEAM_A");

        Bet bet = new Bet();
        bet.setBetId(1L);
        bet.setUserId("U1");
        bet.setEventId("E1");
        bet.setEventWinnerId("TEAM_A"); // user predicted TEAM_A

        when(betRepository.findByEventId("E1")).thenReturn(List.of(bet));
        when(betRepository.save(any(Bet.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        settlementService.processOutcome(outcome);

        // then
        verify(betRepository).save(betCaptor.capture());
        Bet saved = betCaptor.getValue();
        assertThat(saved.getStatus()).isEqualTo("WON");
    }

    @Test
    void processOutcome_singleBetWinner_doesNotMatchOutcome_setsLostAndSaves() {
        // given
        EventOutcome outcome = new EventOutcome("E1", "Match 1", "TEAM_A");

        Bet bet = new Bet();
        bet.setBetId(1L);
        bet.setUserId("U1");
        bet.setEventId("E1");
        bet.setEventWinnerId("TEAM_B"); // user predicted TEAM_B

        when(betRepository.findByEventId("E1")).thenReturn(List.of(bet));
        when(betRepository.save(any(Bet.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        settlementService.processOutcome(outcome);

        // then
        verify(betRepository).save(betCaptor.capture());
        Bet saved = betCaptor.getValue();
        assertThat(saved.getStatus()).isEqualTo("LOST");
    }

    @Test
    void processOutcome_multipleBets_setsCorrectStatusesAndSavesEach() {
        // given
        EventOutcome outcome = new EventOutcome("E1", "Match 1", "TEAM_A");

        Bet bet1 = new Bet();
        bet1.setBetId(1L);
        bet1.setUserId("U1");
        bet1.setEventId("E1");
        bet1.setEventWinnerId("TEAM_A");

        Bet bet2 = new Bet();
        bet2.setBetId(2L);
        bet2.setUserId("U2");
        bet2.setEventId("E1");
        bet2.setEventWinnerId("TEAM_B");

        when(betRepository.findByEventId("E1")).thenReturn(List.of(bet1, bet2));
        when(betRepository.save(any(Bet.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        settlementService.processOutcome(outcome);

        // then
        verify(betRepository, times(2)).save(betCaptor.capture());
        List<Bet> savedBets = betCaptor.getAllValues();

        // order is same as loop order
        assertThat(savedBets.get(0).getBetId()).isEqualTo(1L);
        assertThat(savedBets.get(0).getStatus()).isEqualTo("WON");

        assertThat(savedBets.get(1).getBetId()).isEqualTo(2L);
        assertThat(savedBets.get(1).getStatus()).isEqualTo("LOST");
    }
}
