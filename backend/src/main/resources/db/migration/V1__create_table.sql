CREATE SEQUENCE account_id
    INCREMENT BY 1
    MAXVALUE 9223372036854775807
    START WITH 1
    NO CYCLE;

CREATE SEQUENCE channel_id
    INCREMENT BY 1
    MAXVALUE 9223372036854775807
    START WITH 1
    NO CYCLE;

CREATE SEQUENCE message_Id
    INCREMENT BY 1
    MAXVALUE 9223372036854775807
    START WITH 1
    NO CYCLE;

CREATE TABLE account
(
    account_id   BIGINT      NOT NULL,
    mail_address VARCHAR(50) NOT NULL,
    user_name    VARCHAR(50) NOT NULL,
    PRIMARY KEY (account_id),
    UNIQUE(user_name)
);

CREATE TABLE password
(
    account_id   BIGINT      NOT NULL,
    password     VARCHAR(50) NOT NULL,
    PRIMARY KEY (account_id),
    FOREIGN KEY (account_id) REFERENCES account (account_id)
);

CREATE TABLE two_factor_authentication_setting
(
    account_id    BIGINT NOT NULL,
    status     VARCHAR(8) NOT NULL DEFAULT 'DISABLED',
    secret_key bytea,
    PRIMARY KEY (account_id),
    FOREIGN KEY (account_id) REFERENCES account (account_id)
);

CREATE TABLE channel
(
    channel_id BIGINT      NOT NULL,
    channel_name       VARCHAR(50) NOT NULL,
    type       VARCHAR(10) NOT NULL,
    PRIMARY KEY (channel_id)
);

CREATE TABLE channel_member
(
    channel_id BIGINT NOT NULL,
    account_id    BIGINT NOT NULL,
    PRIMARY KEY (channel_id, account_id),
    FOREIGN KEY (channel_id) REFERENCES channel (channel_id),
    FOREIGN KEY (account_id) REFERENCES account (account_id)
);

CREATE TABLE channel_owner
(
    channel_id BIGINT NOT NULL,
    account_id    BIGINT NOT NULL,
    PRIMARY KEY (channel_id, account_id),
    FOREIGN KEY (channel_id) REFERENCES channel (channel_id),
    FOREIGN KEY (account_id) REFERENCES account (account_id)
);

CREATE TABLE message (
    message_id BIGINT NOT NULL,
    channel_id BIGINT NOT NULL,
    account_id BIGINT  NOT NULL,
    text VARCHAR(500) NOT NULL,
    type VARCHAR(10) NOT NULL,
    send_date_time TIMESTAMP NOT NULL,
    PRIMARY KEY (message_id),
    FOREIGN KEY (channel_id) REFERENCES channel (channel_id),
    FOREIGN KEY (account_id) REFERENCES account (account_id)
);

CREATE TABLE read_message (
    channel_id BIGINT NOT NULL,
    account_id BIGINT  NOT NULL,
    last_read_message_id BIGINT NOT NULL,
    PRIMARY KEY (channel_id, account_id),
    FOREIGN KEY (channel_id) REFERENCES channel (channel_id),
    FOREIGN KEY (account_id) REFERENCES account (account_id)
);

CREATE TABLE chat_bot
(
    account_id    BIGINT NOT NULL,
    channel_id BIGINT NOT NULL,
    PRIMARY KEY (account_id),
    FOREIGN KEY (account_id) REFERENCES account (account_id),
    FOREIGN KEY (channel_id) REFERENCES channel (channel_id)
);
