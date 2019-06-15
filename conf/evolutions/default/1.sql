-- User schema
-- !Ups
CREATE TABLE "User" (
    id SERIAL PRIMARY KEY,
    email text not null unique,
    expo_token text unique,
    name text,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

-- !Downs
DROP TABLE "User";


