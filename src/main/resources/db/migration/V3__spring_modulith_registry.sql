-- Corrected Spring Modulith Event Publication Registry Table for Spring Boot 4
CREATE TABLE IF NOT EXISTS event_publication (
    id UUID PRIMARY KEY,
    listener_id VARCHAR(512) NOT NULL,
    event_type VARCHAR(512) NOT NULL,
    serialized_event VARCHAR(4000) NOT NULL,
    publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date TIMESTAMP WITH TIME ZONE,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED', -- Renamed from 'state' to exactly 'status'
    completion_attempts INT NOT NULL DEFAULT 0,
    last_resubmission_date TIMESTAMP WITH TIME ZONE
);

-- Index to optimize transactional state sweeps for uncompleted or failed events
CREATE INDEX IF NOT EXISTS idx_event_publication_status_pub_date ON event_publication (status, publication_date)
WHERE
    completion_date IS NULL;