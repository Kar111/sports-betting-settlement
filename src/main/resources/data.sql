-- Ensure IDs match your manual curl test: EVT-123
INSERT INTO bets (user_id, event_id, event_market_id, event_winner_id, bet_amount, status)
VALUES ('user_daniel', 'EVT-123', 'MK-01', 'LAL', 250.0, 'PENDING');

INSERT INTO bets (user_id, event_id, event_market_id, event_winner_id, bet_amount, status)
VALUES ('user_alex', 'EVT-123', 'MK-01', 'BULLS', 100.0, 'PENDING');

INSERT INTO bets (user_id, event_id, event_market_id, event_winner_id, bet_amount, status)
VALUES ('user_mariam', 'EVT-999', 'MK-02', 'ARM', 500.0, 'PENDING');