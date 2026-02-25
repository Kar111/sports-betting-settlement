Architecture Summary
REST API
→ Kafka (event-outcomes)
→ Kafka Consumer (find matching bets)
→ RocketMQ (bet-settlements)
→ RocketMQ Consumer (final settlement)
→ H2 Database

Kafka is used for high-throughput domain events (game outcomes).

RocketMQ is used for reliable, asynchronous settlement commands.

H2 stores bet data and settlement status.

Prerequisites

Java 17

Docker & Docker Compose

cURL (or any API client)

How to Run
1) Start infrastructure
   docker-compose up -d

Wait ~20 seconds for RocketMQ to initialize. You can use sleep 20 ;)

2) Run the application
   mvn spring-boot:run

The app starts on http://localhost:8080
.

Test the Flow
1) Check initial bets
   curl http://localhost:8080/api/events/bets

Bets should be in PENDING state.

2) Publish an event outcome
   curl -X POST http://localhost:8080/api/events/outcome \
   -H "Content-Type: application/json" \
   -d '{"eventId": "EVT-123", "eventName": "Lakers vs Bulls", "winnerId": "LAL"}'

3) Verify settlement
   curl http://localhost:8080/api/events/bets

Bets linked to EVT-123 will be marked WON or LOST.

🔍 Technical Notes (Environment & Compatibility)
This project was developed and tested on macOS (Apple Silicon/ARM64). Specific configurations have been applied to ensure cross-platform compatibility:

Docker Platform: The docker-compose.yml uses platform: linux/amd64 tags. This ensures that the Apache RocketMQ and Kafka images run correctly on Apple Silicon (M1/M2/M3) without architecture mismatches.

RocketMQ Broker Connection: The rmqbroker is configured with an entrypoint that sets brokerIP1 = 127.0.0.1. This is a critical fix for local development to allow the Spring Boot host to communicate with the Broker inside the Docker network.

Topic Auto-Creation: The Broker configuration includes autoCreateTopicEnable = true to allow for a seamless "out-of-the-box" experience without manual topic provisioning.
