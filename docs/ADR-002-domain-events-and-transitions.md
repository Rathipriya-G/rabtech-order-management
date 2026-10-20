# ADR-002: Domain Events and Order State Transitions

## Status
Accepted

## Context
The Order Management system must control how an order moves through its approval workflow. Invalid state transitions must be rejected, and successful changes must be recorded as events and communicated through notifications.

## Decision
An order begins in the `DRAFT` state and follows these permitted transitions:

- `DRAFT` → `SUBMITTED`
- `SUBMITTED` → `APPROVED`
- `SUBMITTED` → `REJECTED`

The `Order` entity enforces these rules and throws an `IllegalStateException` when an invalid transition is attempted.

After a successful transition, the application service saves the order, sends a notification through the notification port, and publishes an `OrderEvent` containing the order ID, current status, and occurrence time.

The application uses `OrderEventPublisher` and `TimeProvider` interfaces so that event publishing and time management remain independent of infrastructure implementations.

The current infrastructure uses in-memory adapters for testing.

## Consequences
- Invalid order transitions are prevented by domain rules.
- Successful transitions produce timestamped events.
- Notifications and event publishing can be replaced with other implementations.
- Automated tests can verify the workflow without an external database.
- In-memory events are not durable; a production system would need reliable event storage and delivery.