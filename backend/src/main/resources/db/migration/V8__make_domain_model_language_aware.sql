ALTER TABLE scenarios
    ADD COLUMN language VARCHAR(32);

UPDATE scenarios
SET language = 'MANDARIN'
WHERE language IS NULL;

ALTER TABLE scenarios
    ALTER COLUMN language SET DEFAULT 'MANDARIN',
    ALTER COLUMN language SET NOT NULL;

ALTER TABLE scenario_lines
    ADD COLUMN target_text TEXT,
    ADD COLUMN pronunciation_guide TEXT;

UPDATE scenario_lines
SET target_text = hanzi_text
WHERE target_text IS NULL;

UPDATE scenario_lines
SET pronunciation_guide = pinyin_text
WHERE pronunciation_guide IS NULL;

ALTER TABLE scenario_lines
    ALTER COLUMN target_text SET NOT NULL;

ALTER TABLE vocabulary_items
    ADD COLUMN pronunciation_guide TEXT;

UPDATE vocabulary_items
SET pronunciation_guide = pinyin
WHERE pronunciation_guide IS NULL;
