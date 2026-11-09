-- MathWhiz database (PostgreSQL)
-- Run as the postgres user:  psql -U postgres -f schema.sql
CREATE DATABASE mathwhiz;

\c mathwhiz

CREATE TABLE IF NOT EXISTS scores (
    id          SERIAL PRIMARY KEY,
    player_name VARCHAR(50) NOT NULL,
    score       INT         NOT NULL CHECK (score BETWEEN 0 AND 10),
    played_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Top 10 leaderboard query used by the app
-- SELECT player_name, score, played_at FROM scores ORDER BY score DESC, played_at ASC LIMIT 10;
