-- Event schema
-- !Ups
CREATE TABLE Event (
    id SERIAL PRIMARY KEY,
    title text not null,
    content text,
    date date not null,
    start_time timestamp not null,
    end_time timestamp not null,
    owner_id bigint not null,
    created_at timestamp not null default current_timestamp
);

-- !Downs
DROP TABLE Event;