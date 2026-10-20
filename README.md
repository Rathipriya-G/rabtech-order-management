# Order Management — Domain Model and Architecture

## Overview
This project demonstrates an order management workflow using Java 17 and a multi-module Maven architecture. It models order state transitions, prevents invalid operations, and records workflow events.

## Technology Stack
- Java 17 compatibility
- Apache Maven
- JUnit 5
- In-memory infrastructure adapters

## Project Structure

- `order-domain` — Order entity, order status, and domain event.
- `order-application` — Workflow service and interfaces for persistence, notifications, time, and event publishing.
- `order-infrastructure` — In-memory implementations of the application interfaces.
- `docs` — Architecture Decision Records (ADRs).

## Order Workflow

A new order begins in `DRAFT`.

Permitted transitions:

- `DRAFT` → `SUBMITTED`
- `SUBMITTED` → `APPROVED`
- `SUBMITTED` → `REJECTED`

Invalid transitions are rejected with an `IllegalStateException`.

## Architecture Decisions

- `docs/ADR-001-domain-boundaries.md` explains module boundaries and dependency direction.
- `docs/ADR-002-domain-events-and-transitions.md` documents order state transitions and event publishing.

## Run the Tests

Install a compatible JDK and Maven, then open a terminal in the project root and run:

```bash
mvn test
```

The automated tests cover valid and invalid state transitions, order workflow behavior, persistence through the in-memory repository, notifications, and event publishing.

## Current Limitations

The infrastructure adapters are in-memory implementations intended for demonstration and testing. They do not provide persistent storage or durable event delivery.