ALTER TABLE saga_step_relationship
    ADD CONSTRAINT uk_saga_unique UNIQUE (saga_id);
