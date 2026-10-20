# ADR-001: Domain Boundaries and Module Architecture

## Status
Accepted

## Context
The Order Management system must enforce order approval rules without depending on frameworks, databases, or external services. Mixing business rules with infrastructure code would make the domain harder to test and maintain.

## Decision
The project is divided into three Maven modules:

- **order-domain:** Contains the Order entity, OrderStatus enum, and OrderEvent record. It has no framework or database dependencies.
- **order-application:** Contains OrderWorkflowService and ports for persistence, notifications, time, and event publishing.
- **order-infrastructure:** Contains in-memory implementations of the ports for testing.

The domain module remains independent of the application and infrastructure modules. Dependencies point inward toward the domain.

## Consequences
- Business rules can be tested without a database or Spring.
- Infrastructure implementations can be replaced without rewriting the domain model.
- Additional modules and production adapters can be added later.
- Each module has a clear responsibility.