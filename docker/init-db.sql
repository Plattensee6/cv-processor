-- Initialize CV Processor Database
-- This script runs when the PostgreSQL container starts for the first time

-- Create database if it doesn't exist
SELECT 'CREATE DATABASE cvprocessor'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'cvprocessor')\gexec

-- Connect to the cvprocessor database
\c cvprocessor;

-- Create user if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'cvuser') THEN
        CREATE ROLE cvuser WITH LOGIN PASSWORD 'cvpass';
    END IF;
END
$$;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE cvprocessor TO cvuser;
GRANT ALL PRIVILEGES ON SCHEMA public TO cvuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO cvuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO cvuser;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO cvuser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO cvuser;
