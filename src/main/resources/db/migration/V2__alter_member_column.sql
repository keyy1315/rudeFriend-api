ALTER TABLE member
    ADD member_id VARCHAR(255) NULL;

ALTER TABLE member
    ADD password VARCHAR(255) NULL;

ALTER TABLE member
    ADD `role` VARCHAR(255) NULL;

ALTER TABLE member
    MODIFY member_id VARCHAR(255) NOT NULL;

ALTER TABLE member
    MODIFY password VARCHAR(255) NOT NULL;

ALTER TABLE member
    MODIFY `role` VARCHAR(255) NOT NULL;

ALTER TABLE member
    ADD CONSTRAINT uc_member_memberid UNIQUE (member_id);

ALTER TABLE member
    ADD CONSTRAINT uc_member_name UNIQUE (name);