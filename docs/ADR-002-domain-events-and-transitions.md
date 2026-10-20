
# ADR-002: Domain Events and Order State Transitions

## Status
Accepted

## Context
The Order Management system must enforce valid order state transitions, prevent invalid business operations, and record important changes as domain events. The domain model must remain independent of Spring, JPA, HTTP, and database adapters.

## Decision
An order begins in the `DRAFT` state and uses the following states:

- `DRAFT`
- `CONFIRMED`
- `PAID`
- `CANCELLED`

The `Order` entity enforces the following business rules:

- An order must contain at least one line before confirmation.
- Each order line must have a positive whole-number quantity.
- A cancelled order cannot be paid.
- A paid order cannot return to the draft state.
- The order total is calculated from immutable line prices and quantities.

The domain records important changes through the following events:

- `OrderConfirmed`
- `PaymentRecorded`
- `OrderCancelled`

The application service coordinates order operations, saves changes through the repository port, publishes domain events through `OrderEventPublisher`, and sends notifications through the notification port.

The application layer uses interfaces such as `OrderRepository`, `OrderNotification`, `OrderEventPublisher`, and `TimeProvider` to keep application logic independent of infrastructure implementations.

The infrastructure module provides in-memory adapters for testing. The domain module does not depend on Spring, JPA, HTTP, or database adapters.

## Consequences
- Invalid order operations are rejected by domain rules.
- Important order changes are represented by domain events.
- Application logic can be tested without an external database.
- Infrastructure implementations can be replaced without changing the core domain model.
- In-memory storage and event publishing are suitable for testing but do not provide durable production persistence or guaranteed event delivery.
