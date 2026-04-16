CREATE TABLE vocabulary_items (
    id BIGSERIAL PRIMARY KEY,
    scenario_line_id BIGINT NOT NULL REFERENCES scenario_lines (id) ON DELETE CASCADE,
    expression TEXT NOT NULL,
    pinyin TEXT NOT NULL,
    gloss TEXT NOT NULL,
    explanation TEXT,
    start_char_index INTEGER,
    end_char_index INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
