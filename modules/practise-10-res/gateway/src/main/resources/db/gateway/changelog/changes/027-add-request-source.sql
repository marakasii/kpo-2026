--liquibase formatted sql

--changeset course:027
ALTER TABLE ai_requests ADD COLUMN source VARCHAR(16) NOT NULL DEFAULT 'API';
