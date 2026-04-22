ALTER TABLE scenario_lines
    ADD COLUMN audio_url VARCHAR(512),
    ADD COLUMN audio_status VARCHAR(32),
    ADD COLUMN audio_generated_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN audio_source_text_hash VARCHAR(255);

UPDATE scenario_lines
SET audio_status = 'NOT_GENERATED'
WHERE audio_status IS NULL;

ALTER TABLE scenario_lines
    ALTER COLUMN audio_status SET DEFAULT 'NOT_GENERATED',
    ALTER COLUMN audio_status SET NOT NULL;
