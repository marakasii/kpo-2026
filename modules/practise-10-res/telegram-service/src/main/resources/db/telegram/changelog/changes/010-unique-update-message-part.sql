--liquibase formatted sql

--changeset telegram:parts-010
CREATE UNIQUE INDEX telegram_notification_update_part ON telegram_notifications(update_id, part_index);
