--liquibase formatted sql

--changeset telegram:parts-009
ALTER TABLE telegram_notifications DROP CONSTRAINT telegram_notifications_update_id_key;
