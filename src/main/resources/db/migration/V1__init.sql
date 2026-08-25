CREATE TABLE saga (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

COMMENT ON TABLE saga IS 'Table that stores the sagas managed by the system.';
COMMENT ON COLUMN saga.id IS 'Unique saga identifier generated via TSID.';
COMMENT ON COLUMN saga.name IS 'Saga name.';
COMMENT ON COLUMN saga.description IS 'Detailed description of the saga.';
COMMENT ON COLUMN saga.deleted IS 'Flag indicating if the saga was logically deleted (soft delete).';
COMMENT ON COLUMN saga.created_at IS 'Date and time of record creation.';
COMMENT ON COLUMN saga.updated_at IS 'Date and time of the last record update.';
COMMENT ON COLUMN saga.created_by IS 'User who created the record.';
COMMENT ON COLUMN saga.updated_by IS 'User who last updated the record.';
