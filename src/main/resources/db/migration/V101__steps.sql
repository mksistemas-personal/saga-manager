CREATE TABLE state_step
(
    id           BIGINT PRIMARY KEY,
    name         VARCHAR(255)                NOT NULL,
    description  TEXT,
    is_composite boolean                     NOT NULL DEFAULT FALSE,
    deleted      BOOLEAN                     NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255)
);

CREATE TABLE step_relationship
(
    id        BIGINT PRIMARY KEY,
    source_id BIGINT NOT NULL,
    dest_id   BIGINT NOT NULL,
    CONSTRAINT fk_state_step FOREIGN KEY (source_id) REFERENCES state_step (id),
    CONSTRAINT fk_next_state_step FOREIGN KEY (dest_id) REFERENCES state_step (id)
);

CREATE INDEX idx_step_relationship_source_id ON step_relationship (source_id);
CREATE INDEX idx_step_relationship_dest_id ON step_relationship (dest_id);

CREATE TABLE composite_state_relationship
(
    id       BIGINT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    child_id BIGINT NOT NULL,
    CONSTRAINT fk_composite_owner FOREIGN KEY (owner_id) REFERENCES state_step (id),
    CONSTRAINT fk_composite_child FOREIGN KEY (child_id) REFERENCES state_step (id)
);

CREATE INDEX idx_composite_state_relationship_source_id ON step_relationship (source_id);
CREATE INDEX idx_composite_state_relationship_dest_id ON step_relationship (dest_id);

CREATE TABLE saga_step_relationship
(
    id       BIGINT PRIMARY KEY,
    saga_id  BIGINT  NOT NULL,
    step_id  BIGINT  NOT NULL,
    is_valid BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_saga_sept_relationship_saga FOREIGN KEY (saga_id) REFERENCES saga (id),
    CONSTRAINT fk_saga_sept_relationship_step FOREIGN KEY (step_id) REFERENCES state_step (id)
);

CREATE INDEX idx_saga_step_relationship_saga_id ON saga_step_relationship (saga_id);
CREATE INDEX idx_saga_step_relationship_dest_id ON saga_step_relationship (step_id);

COMMENT ON TABLE state_step IS 'Table that stores the state steps of the sagas.';
COMMENT ON COLUMN state_step.id IS 'Unique identifier of the state step generated via TSID.';
COMMENT ON COLUMN state_step.name IS 'Name of the state step.';
COMMENT ON COLUMN state_step.description IS 'Detailed description of the state step.';
COMMENT ON COLUMN state_step.is_composite IS 'Flag indicating if this step is composed of multiple sub-steps.';
COMMENT ON COLUMN state_step.deleted IS 'Flag indicating if the state step was logically deleted (soft delete).';
COMMENT ON COLUMN state_step.created_at IS 'Date and time of record creation.';
COMMENT ON COLUMN state_step.updated_at IS 'Date and time of the last record update.';
COMMENT ON COLUMN state_step.created_by IS 'User who created the record.';
COMMENT ON COLUMN state_step.updated_by IS 'User who last updated the record.';

COMMENT ON TABLE step_relationship IS 'Table that stores directional relationships between state steps (source -> dest).';
COMMENT ON COLUMN step_relationship.id IS 'Unique identifier of the relationship generated via TSID.';
COMMENT ON COLUMN step_relationship.source_id IS 'Identifier of the source state step (foreign key).';
COMMENT ON COLUMN step_relationship.dest_id IS 'Identifier of the destination state step (foreign key).';

COMMENT ON TABLE composite_state_relationship IS 'Table that stores hierarchical relationships for composite steps (owner -> child).';
COMMENT ON COLUMN composite_state_relationship.id IS 'Unique identifier of the relationship generated via TSID.';
COMMENT ON COLUMN composite_state_relationship.owner_id IS 'Identifier of the parent composite state step (owner).';
COMMENT ON COLUMN composite_state_relationship.child_id IS 'Identifier of the child state step (child).';

COMMENT ON TABLE saga_step_relationship IS 'Table that associates a saga with its respective state steps.';
COMMENT ON COLUMN saga_step_relationship.id IS 'Unique identifier of the relationship generated via TSID.';
COMMENT ON COLUMN saga_step_relationship.saga_id IS 'Identifier of the associated saga (foreign key).';
COMMENT ON COLUMN saga_step_relationship.step_id IS 'Identifier of the associated state step (foreign key).';
COMMENT ON COLUMN saga_step_relationship.is_valid IS 'Flag indicating if the state step is valid.';