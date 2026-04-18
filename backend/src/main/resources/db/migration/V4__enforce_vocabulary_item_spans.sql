ALTER TABLE vocabulary_items
    ALTER COLUMN start_char_index SET NOT NULL,
    ALTER COLUMN end_char_index SET NOT NULL;

ALTER TABLE vocabulary_items
    ADD CONSTRAINT chk_vocabulary_items_span_range
        CHECK (start_char_index >= 0 AND end_char_index > start_char_index);

ALTER TABLE vocabulary_items
    ADD CONSTRAINT uk_vocabulary_items_line_span
        UNIQUE (scenario_line_id, start_char_index, end_char_index);
