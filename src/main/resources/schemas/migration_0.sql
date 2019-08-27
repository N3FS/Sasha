CREATE TABLE `{prefix}users` (
    id       VARCHAR(36) PRIMARY KEY NOT NULL,
    username VARCHAR(16)             NOT NULL
);

CREATE TABLE `{prefix}tickets` (
    id              INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    reporter_id     VARCHAR(36)                    NOT NULL,
    reported_at     TIMESTAMP WITH TIME ZONE       NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE       NOT NULL,
    open            BOOL DEFAULT TRUE              NOT NULL,
    location_x      INT,
    location_y      INT,
    location_z      INT,
    location_world  VARCHAR,
    assignee_id     VARCHAR(36),
);

CREATE TABLE `{prefix}ticket_comments` (
    id              INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    ticket_id       INT                            NOT NULL,
    author_id       VARCHAR(36)                    NOT NULL,
    conversation_id INT                            NOT NULL,
    written_at      TIMESTAMP WITH TIME ZONE       NOT NULL,
    message         TEXT                           NOT NULL,
    new_open_state  BOOL
);

CREATE TABLE `{prefix}ticket_subscriptions` (
    id          INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    ticket_id   INT                            NOT NULL,
    user_id     INT                            NOT NULL,
    last_seen   TIMESTAMP WITH TIME ZONE       NOT NULL,
);

UPDATE `{prefix}meta` SET VERSION=1;