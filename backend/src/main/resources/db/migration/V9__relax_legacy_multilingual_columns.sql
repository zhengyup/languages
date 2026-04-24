ALTER TABLE scenario_lines
    ALTER COLUMN hanzi_text DROP NOT NULL;

ALTER TABLE vocabulary_items
    ALTER COLUMN pinyin DROP NOT NULL;
