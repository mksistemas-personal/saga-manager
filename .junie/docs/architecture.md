# Architecture

## Backend

The backend follows a modular architecture.

Controller ↓ Application ↓ Domain ↓ Infrastructure

## Domain

The domain must not depend on Spring Framework.

## Infrastructure

Spring-specific implementations belong here.