# Async Payment Retry Service

A production-inspired backend system that simulates **asynchronous payment processing** with **automatic retries, exponential backoff, Dead Letter Queue (DLQ), and idempotency**.

## Problem Statement

In real payment systems, transactions may fail due to:

* Network issues
* Timeout errors
* Temporary gateway failures
* Service unavailability

Instead of failing immediately, modern systems automatically retry payments and safely handle permanent failures.

This project demonstrates how such systems work using **Spring Boot + Redis + MySQL**.

---

## Features

### 1. Asynchronous Payment Processing

Payments are processed asynchronously using a background worker (`@Scheduled`) instead of blocking API requests.

### 2. Retry Mechanism

Failed payments are automatically retried up to a configurable limit.

### 3. Exponential Backoff

Retries use increasing delay intervals:

```text
Retry 1 → 2 sec
Retry 2 → 4 sec
Retry 3 → 8 sec
```

This prevents overwhelming downstream services.

### 4. Dead Letter Queue (DLQ)

Payments that permanently fail after maximum retries are moved to a dedicated Redis queue:

```text
deadLetterQueue
```

This enables debugging and manual inspection of failed jobs.

### 5. Idempotency

Duplicate payment requests with the same `orderId` are prevented.

If the same request is sent multiple times:

```text
Same request → Same payment
```

This avoids duplicate payment creation.

---

## Tech Stack

* Java
* Spring Boot
* Spring Data JPA
* MySQL
* Redis
* Docker
* Maven

---

## Architecture Flow

```text
Create Payment Request
          ↓
       PENDING
          ↓
     Redis Queue
          ↓
 Scheduled Worker
          ↓
    Process Payment
          ↓
 ┌────────┼────────┐
 ↓        ↓        ↓
SUCCESS  RETRY    FAILED
           ↓         ↓
     Exponential    DLQ
       Backoff
```

---

## API Endpoints

### Create Payment

```http
POST /payment/create
```

Request Body:

```json
{
  "orderId": "ORD101",
  "amount": 5000
}
```

---

### Get Payment Status

```http
GET /payment/{id}
```

Returns:

* `PENDING`
* `SUCCESS`
* `FAILED`

---

## Sample Payment Lifecycle

```text
Payment Created
       ↓
     PENDING
       ↓
 Worker Processes
       ↓
     FAILED
       ↓
 Retry After 2 sec
       ↓
     FAILED
       ↓
 Retry After 4 sec
       ↓
    SUCCESS
```

OR

```text
Max Retry Reached
        ↓
      FAILED
        ↓
Moved to deadLetterQueue
```

---

## How to Run Locally

### Clone Repository

```bash
git clone https://github.com/Aamish8/async-payment-retry-service.git
```

### Configure MySQL

Update `application.properties` with your database credentials.

### Start Redis using Docker

```bash
docker run --name redis-payment -p 6379:6379 redis
```

### Run Application

```bash
mvn spring-boot:run
```

---

## Future Improvements

* Payment reconciliation service
* Notification system
* Kafka-based event processing
* Monitoring & logging
* API rate limiting

---

## Author

**Aamish**
