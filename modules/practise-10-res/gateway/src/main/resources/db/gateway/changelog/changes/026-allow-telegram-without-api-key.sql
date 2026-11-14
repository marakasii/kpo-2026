--liquibase formatted sql

--changeset course:026
ALTER TABLE ai_requests ALTER COLUMN api_key_id DROP NOT NULL;
