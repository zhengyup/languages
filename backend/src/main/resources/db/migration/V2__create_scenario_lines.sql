CREATE TABLE scenario_lines (
    id BIGSERIAL PRIMARY KEY,
    scenario_id BIGINT NOT NULL REFERENCES scenarios (id) ON DELETE CASCADE,
    line_order INTEGER NOT NULL,
    speaker_name VARCHAR(255),
    hanzi_text TEXT NOT NULL,
    pinyin_text TEXT,
    english_translation TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_scenario_lines_scenario_id_line_order UNIQUE (scenario_id, line_order)
);
