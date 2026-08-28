-- Populate DB Script for Saga Manager
-- Sagas
INSERT INTO saga (id, name, description, deleted, created_at, created_by, updated_at, updated_by)
VALUES (880536525792825259, 'DocumentoCarga', 'Saga acompanhar a internalizacao do documento de carga', false,
        CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system');

-- State Steps
INSERT INTO state_step (id, name, description, is_composite, events, deleted, created_at, created_by, updated_at,
                        updated_by)
VALUES (880536525792825260, 'HubInt-DocumentoCarga-Recebido', 'Documento de carga recebido pelo HubInt', false,
        'DocumentoCargaRecebido', false, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
       (880536525792825261, 'HubInt-DocumentoCarga-Enviado', 'Documento de carga enviado pelo HubInt', false,
        'DocumentoCargaEnviado', false, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
       (880536525792825262, 'TmsOpe-DocumentoCarga-Recebido', 'Documento de carga recebido pelo TMSOPE', false,
        'DocumentoCargaEnviado', false, CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
       (880536525792825263, 'TmsOpe-DocumentoCarga-Inserido-DocumentoTransporte',
        'Documento de carga inserido no Documento de Transporte', false, 'DocumentoCargaInserido', false,
        CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system'),
       (880536525792825264, 'Documento-Carga-Agrupado',
        'Documento de carga agrupado', false, null, false,
        CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 'system');


-- Saga Step Relationships
INSERT INTO saga_step_relationship (id, saga_id, step_id)
VALUES (900536525792825263, 880536525792825259, 880536525792825260);

-- Composite State Relationships (Owner -> Child)
-- INSERT INTO composite_state_relationship (id, owner_id, child_id) VALUES
-- (4001, 2005, 2002),
-- (4002, 2005, 2003);

-- Step Relationships (Source -> Dest)
INSERT INTO step_relationship (id, source_id, dest_id)
VALUES (910536525792825263, 880536525792825261, 880536525792825260),
       (910536525792825264, 880536525792825262, 880536525792825261),
       (910536525792825265, 880536525792825263, 880536525792825262);
