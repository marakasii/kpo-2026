--liquibase formatted sql

--changeset telegram:parts-008
ALTER TABLE telegram_notifications ADD COLUMN part_index INTEGER NOT NULL DEFAULT 0 CHECK (part_index >= 0);
