--liquibase formatted sql

--changeset course:028
ALTER TABLE ai_requests ADD CONSTRAINT ai_requests_source_check CHECK ((source = 'API' AND api_key_id IS NOT NULL) OR (source = 'TELEGRAM' AND api_key_id IS NULL));
