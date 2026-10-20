
# Order Management — Domain Model and Architecture

## Overview

This project demonstrates an order management workflow using Java 17 and a multi-module Maven architecture. It models order state transitions, enforces business rules, and records domain events.

## Technology Stack

- Java 17 compatibility
- Apache Maven
- JUnit 5
- In-memory infrastructure adapters

## Project Structure

- `order-domain` — Order entity, order status, immutable order lines, and domain events.
- `order-application` — Workflow service and interfaces for persistence, notifications, time, and event publishing.
- `order-infrastructure` — In-memory implementations and workflow tests.
- `docs` — Architecture Decision Records (ADRs).

## Order Workflow

A new order begins in `DRAFT`.

The supported order states are:

- `DRAFT`
- `CONFIRMED`
- `PAID`
- `CANCELLED`

The domain model enforces these business rules:

- An order must contain at least one line before confirmation.
- Order line quantities must be positive whole numbers.
- A cancelled order cannot be paid.
- A paid order cannot return to draft.
- The order total is derived from line prices and quantities.

Important workflow changes are represented by these domain events:

- `OrderConfirmed`
- `PaymentRecorded`
- `OrderCancelled`

Invalid operations are rejected by the domain model.

## Architecture Decisions

- `docs/ADR-001-domain-boundaries.md` explains module boundaries and dependency direction.
- `docs/ADR-002-domain-events-and-transitions.md` documents order state transitions and domain events.

The domain module remains independent of Spring, JPA, HTTP, and database adapters.

## Run the Tests

Install a compatible JDK and Maven, open a terminal in the project root, and run:

```bash
mvn test
```

The automated tests cover order workflow behavior, valid and invalid operations, in-memory persistence, notifications, and event publishing.

## Current Limitations

The infrastructure adapters use in-memory implementations intended for demonstration and testing. They do not provide persistent storage or durable event delivery.
