-- Baseline migration. It only proves that Flyway runs.
-- Add the real schema in V2__..., V3__... (one change per file, never edit an applied file).
CREATE TABLE IF NOT EXISTS lektie_meta (
    key   TEXT PRIMARY KEY,
    value TEXT NOT NULL
);

INSERT INTO lektie_meta (key, value) VALUES ('schema_owner', 'db/migrations')
ON CONFLICT (key) DO NOTHING;
