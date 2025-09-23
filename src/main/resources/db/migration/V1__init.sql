CREATE TABLE anonymous_member
(
    id         BINARY(16)   NOT NULL,
    ip_address VARCHAR(255) NULL,
    CONSTRAINT pk_anonymous_member PRIMARY KEY (id)
);

CREATE TABLE board
(
    id            BINARY(16)   NOT NULL,
    created_at    datetime     NOT NULL,
    updated_at    datetime     NULL,
    created_by_id BINARY(16)   NULL,
    updated_by_id BINARY(16)   NULL,
    title         VARCHAR(255) NULL,
    content       LONGTEXT     NULL,
    CONSTRAINT pk_board PRIMARY KEY (id)
);

CREATE TABLE game_account_info
(
    id             BINARY(16)   NOT NULL,
    game_name      VARCHAR(255) NULL,
    tag_line       VARCHAR(255) NULL,
    icon_url       VARCHAR(255) NULL,
    lol_tier       VARCHAR(255) NULL,
    flex_tier      VARCHAR(255) NULL,
    tft_tier       VARCHAR(255) NULL,
    double_up_tier VARCHAR(255) NULL,
    CONSTRAINT pk_game_account_info PRIMARY KEY (id)
);

CREATE TABLE lol_match
(
    id           BINARY(16)   NOT NULL,
    game_info_id BINARY(16)   NULL,
    match_id     VARCHAR(255) NULL,
    CONSTRAINT pk_lol_match PRIMARY KEY (id)
);

CREATE TABLE member
(
    id                   BINARY(16)   NOT NULL,
    created_at           datetime     NOT NULL,
    updated_at           datetime     NULL,
    created_by_id        BINARY(16)   NULL,
    updated_by_id        BINARY(16)   NULL,
    status               BIT(1)       NOT NULL,
    name                 VARCHAR(255) NULL,
    game_account_info_id BINARY(16)   NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
);

CREATE TABLE save_file
(
    file_uuid          VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NULL,
    upload_date_time   datetime     NULL,
    uploader_id        BINARY(16)   NOT NULL,
    CONSTRAINT pk_save_file PRIMARY KEY (file_uuid)
);

CREATE TABLE tft_match
(
    id           BINARY(16)   NOT NULL,
    game_info_id BINARY(16)   NULL,
    match_id     VARCHAR(255) NULL,
    CONSTRAINT pk_tft_match PRIMARY KEY (id)
);

CREATE TABLE vote
(
    id         BINARY(16)   NOT NULL,
    board_id   BINARY(16)   NOT NULL,
    member_id  BINARY(16)   NULL,
    ip_address VARCHAR(255) NULL,
    vote_type  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_vote PRIMARY KEY (id)
);

ALTER TABLE vote
    ADD CONSTRAINT uc_844b2c7697a4efb440e75c0ac UNIQUE (board_id, member_id);

ALTER TABLE vote
    ADD CONSTRAINT uc_dad7a8a1497db100c39be24f7 UNIQUE (board_id);

ALTER TABLE board
    ADD CONSTRAINT FK_BOARD_ON_CREATEDBY FOREIGN KEY (created_by_id) REFERENCES member (id);

ALTER TABLE board
    ADD CONSTRAINT FK_BOARD_ON_UPDATEDBY FOREIGN KEY (updated_by_id) REFERENCES member (id);

ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_ON_CREATEDBY FOREIGN KEY (created_by_id) REFERENCES member (id);

ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_ON_GAMEACCOUNTINFO FOREIGN KEY (game_account_info_id) REFERENCES game_account_info (id);

ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_ON_UPDATEDBY FOREIGN KEY (updated_by_id) REFERENCES member (id);

ALTER TABLE save_file
    ADD CONSTRAINT FK_SAVE_FILE_ON_UPLOADER FOREIGN KEY (uploader_id) REFERENCES member (id);

ALTER TABLE vote
    ADD CONSTRAINT FK_VOTE_ON_BOARD FOREIGN KEY (board_id) REFERENCES board (id);

ALTER TABLE vote
    ADD CONSTRAINT FK_VOTE_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);