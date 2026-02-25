# 🏀 Sports Betting Settlement Service

This project is a high-performance backend simulation designed to handle sports betting event outcomes and automated bet settlements. It demonstrates a decoupled, event-driven architecture powered by **Spring Boot**, **Apache Kafka**, and **Apache RocketMQ**.

---

## 🏗️ Architecture Summary

The system is designed for high-throughput and reliable processing by separating concerns:

1.  **REST API**: Receives game results.
2.  **Kafka (`event-outcomes`)**: Used for high-throughput domain events (game outcomes).
3.  **Kafka Consumer**: Fetches matching bets from the database.
4.  **RocketMQ (`bet-settlements`)**: Used for reliable, asynchronous settlement commands.
5.  **RocketMQ Consumer**: Executes the final settlement and logic.
6.  **H2 Database**: Stores bet data and final settlement status.

[Image of event-driven architecture diagram for sports betting]

---

## 🛠️ Prerequisites

* **Java 17** (JDK)
* **Maven** (installed locally)
* **Docker & Docker Compose**
* **cURL** (or any API client like Postman)

---

## 🚀 How to Run

### 1. Start Infrastructure
Launch the containers in the background:
```bash
docker-compose up -d


Note: Please wait ~20 seconds for RocketMQ to fully initialize before starting the application.

2. Run the Application
Start the Spring Boot service:

Bash
mvn spring-boot:run
The application will be available at http://localhost:8080.

🧪 Testing the Flow
Follow these steps to observe the end-to-end settlement process:

1. Check Initial Bets
Verify the current state of the database (bets should be PENDING):

Bash
curl http://localhost:8080/api/events/bets
2. Publish an Event Outcome
Simulate a game result. This sends a message to Kafka:

Bash
curl -X POST http://localhost:8080/api/events/outcome \
-H "Content-Type: application/json" \
-d '{
  "eventId": "EVT-123", 
  "eventName": "Lakers vs Bulls", 
  "winnerId": "LAL"
}'
3. Verify Settlement
Check the bets again. The RocketMQ consumer will have updated the records to WON or LOST:

Bash
curl http://localhost:8080/api/events/bets
🔍 Technical Notes (Environment & Compatibility)
This project was developed and optimized for macOS (Apple Silicon/ARM64). Specific configurations ensure cross-platform stability:

Docker Platform: docker-compose.yml uses platform: linux/amd64 tags. This ensures Apache RocketMQ and Kafka images run correctly on Apple Silicon (M1/M2/M3) without architecture mismatches.

RocketMQ Broker Connection: The rmqbroker is configured with an entrypoint setting brokerIP1 = 127.0.0.1. This is a critical fix for local development, allowing the Spring Boot host to communicate with the Broker inside the Docker network.

Topic Auto-Creation: The Broker configuration includes autoCreateTopicEnable = true for a seamless "out-of-the-box" experience without manual topic provisioning.