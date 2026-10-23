-- Manual Oracle migration for an existing Event Management System schema.
-- Apply once before starting the refactored backend. Not auto-run by Spring Boot.
ALTER TABLE event ADD (version NUMBER(19,0) DEFAULT 0 NOT NULL);
