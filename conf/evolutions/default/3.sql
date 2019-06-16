-- Participant schema
-- !Ups
CREATE TABLE Participant (
    id SERIAL PRIMARY KEY,
    status int not null default 0, -- 0: not awake, 1: awake
    event_id bigint not null,
    user_id bigint not null,
    created_at timestamp not null default current_timestamp
);

-- !Downs
DROP TABLE Participant;