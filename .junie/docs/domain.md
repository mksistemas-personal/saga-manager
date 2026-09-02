# Domain

## Saga

A Saga represents the step startpoint. A Saga is a sequence of steps that are executed in a specific order. The order is
given by the relationship between the steps

## Step

A Step represents a single action or task that is part of a Saga. Steps are executed when they receive an event pointing
to the step.

## Connectivity

A Step can be connected to other Steps through relationships. These relationships define the order in which Steps are
executed. A step can be connected to other steps through relationships. These relationships define the connectivity in
which steps are executed. A step can be connected to other steps through relationships. These relationships define the
connectivity between steps.

## Relationships

Saga → Start StateStep ← Step ← Step or Steps

## Rules

Rules are defined in the relationships between steps. A rule defines the execution conditions between steps; for
example, if there are two related steps and the second executes while the first does not, a rule could indicate a
workflow execution failure due to a missing required step. 