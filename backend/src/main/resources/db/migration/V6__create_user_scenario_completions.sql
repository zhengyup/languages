CREATE TABLE user_scenario_completions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    scenario_id BIGINT NOT NULL REFERENCES scenarios (id) ON DELETE CASCADE,
    completed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_user_scenario_completions_user_scenario UNIQUE (user_id, scenario_id)
);
