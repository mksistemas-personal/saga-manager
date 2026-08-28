ALTER TABLE composite_state_relationship ADD CONSTRAINT uk_composite_state_owner_child UNIQUE (owner_id, child_id);

CREATE INDEX idx_composite_state_owner_child ON composite_state_relationship (owner_id, child_id);
