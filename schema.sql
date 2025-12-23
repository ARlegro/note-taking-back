CREATE TABLE "users"
(
    "id"          UUID                  NOT NULL,
    "email"       VARCHAR(255)          NOT NULL,
    "provider"    VARCHAR(20)           NOT NULL,
    "provider_id" VARCHAR(255)          NOT NULL,
    "created_at"  TIMESTAMP DEFAULT NOW NOT NULL,
    "updated_at"  TIMESTAMP NULL
);

COMMENT
ON COLUMN "users"."id" IS 'PK';

COMMENT
ON COLUMN "users"."provider" IS 'ENUM(Google, Github, Kakao)';

CREATE TABLE "user_settings"
(
    "id"                   UUID                        NOT NULL,
    "user_id"              UUID                        NOT NULL,
    "theme"                VARCHAR(20)                 NOT NULL,
    "default_visibility"   VARCHAR(20) DEFAULT PRIVATE NOT NULL,
    "trash_retention_days" INTEGERS    DEFAULT 30      NOT NULL,
    "auto_save_enabled"    BOOLEAN     DEFAULT true    NOT NULL,
    "created_at"           TIMESTAMPTZ DEFAULT NOW     NOT NULL,
    "udpated_at"           TIMESTAMPTZ                 NOT NULL
);

COMMENT
ON COLUMN "user_settings"."id" IS 'PK';

COMMENT
ON COLUMN "user_settings"."user_id" IS 'FK';

COMMENT
ON COLUMN "user_settings"."theme" IS 'ENUM(light, dark)';

COMMENT
ON COLUMN "user_settings"."default_visibility" IS 'ENUM(PRIVATE, PUBLIC)';

CREATE TABLE "Notes"
(
    "id"               UUID                    NOT NULL,
    "owner_id"         UUID                    NOT NULL,
    "folder_id"        UUID                    NOT NULL,
    "content_markdown" TEXT        DEFAULT ""  NOT NULL,
    "version"          INTEGER     DEFAULT 1   NOT NULL,
    "deleted_at"       TIMESTAMPTZ NULL,
    "created_at"       TIMESTAMPTZ DEFAULT NOW NOT NULL,
    "updated_at"       TIMESTAMPTZ             NOT NULL
);

COMMENT
ON COLUMN "Notes"."id" IS 'PK';

COMMENT
ON COLUMN "Notes"."owner_id" IS 'FK';

COMMENT
ON COLUMN "Notes"."folder_id" IS 'FK';

CREATE TABLE "note_tags"
(
    "id"         UUID NOT NULL,
    "note_id"    UUID NOT NULL,
    "tag_id"     UUID NOT NULL,
    "created_at" UUID NOT NULL
);

COMMENT
ON COLUMN "note_tags"."id" IS 'PK';

COMMENT
ON COLUMN "note_tags"."note_id" IS 'FK';

COMMENT
ON COLUMN "note_tags"."tag_id" IS 'FK';

CREATE TABLE "tags"
(
    "id"         UUID                    NOT NULL,
    "owner_id"   UUID                    NOT NULL,
    "name"       VARCHAR(50)             NOT NULL,
    "created_at" TIMESTAMPTZ DEFAULT NOW NOT NULL,
    "updated_at" TIMESTAMPTZ NULL
);

COMMENT
ON COLUMN "tags"."id" IS 'PK';

COMMENT
ON COLUMN "tags"."owner_id" IS 'FK';

CREATE TABLE "note_attachments_ref"
(
    "id"            UUID                      NOT NULL,
    "note_id"       UUID                      NOT NULL,
    "attachment_id" UUID        DEFAULT NOW() NOT NULL,
    "created_at"    TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

COMMENT
ON COLUMN "note_attachments_ref"."id" IS 'PK';

COMMENT
ON COLUMN "note_attachments_ref"."note_id" IS 'FK';

COMMENT
ON COLUMN "note_attachments_ref"."attachment_id" IS 'FK';

CREATE TABLE "attachments"
(
    "id"          UUID        DEFAULT NOW() NOT NULL,
    "type"        VARCHAR(20) NULL,
    "storage_key" VARCHAR(55)               NOT NULL,
    "Field3"      VARCHAR(255) NULL,
    "created_at"  TIMESTAMPTZ DEFAULT NOW() NOT NULL
);

COMMENT
ON COLUMN "attachments"."id" IS 'PK';

COMMENT
ON COLUMN "attachments"."type" IS '나중에 ENUM 관리';

CREATE TABLE "shares_links"
(
    "id"          UUID                    NOT NULL,
    "permission"  VARCHAR(20)             NOT NULL,
    "target_type" VARCHAR(20)             NOT NULL,
    "target_id"   UUID NULL,
    "token"       VARCHAR(100)            NOT NULL,
    "created_by"  UUID                    NOT NULL,
    "expired_at"  TIMESTAMPTZ             NOT NULL,
    "created_at"  TIMESTAMPTZ DEFAULT NOW NOT NULL
);

COMMENT
ON COLUMN "shares_links"."id" IS 'PK';

COMMENT
ON COLUMN "shares_links"."permission" IS 'ENUM(Readable)';

COMMENT
ON COLUMN "shares_links"."target_type" IS 'ENUM(Note, Folder)';

COMMENT
ON COLUMN "shares_links"."token" IS 'UNIQUE';

COMMENT
ON COLUMN "shares_links"."created_by" IS 'FK';

CREATE TABLE "note_permissions"
(
    "id"         UUID                    NOT NULL,
    "note_id"    UUID                    NOT NULL,
    "grantee_id" UUID                    NOT NULL,
    "role"       VARCHAR(20)             NOT NULL,
    "created_at" TIMESTAMPTZ DEFAULT NOW NOT NULL,
    "updated_at" TIMESTAMPTZ NULL
);

COMMENT
ON COLUMN "note_permissions"."id" IS 'PK';

COMMENT
ON COLUMN "note_permissions"."note_id" IS 'FK';

COMMENT
ON COLUMN "note_permissions"."grantee_id" IS 'FK';

COMMENT
ON COLUMN "note_permissions"."role" IS 'ENUM(READABLE)';

CREATE TABLE "folder_permissions"
(
    "id"         UUID                    NOT NULL,
    "folder_id"  UUID                    NOT NULL,
    "user_id"    UUID                    NOT NULL,
    "role"       VARCHAR(20)             NOT NULL,
    "created_at" TIMESTAMPTZ DEFAULT NOW NOT NULL,
    "updated_at" TIMESTAMPTZ NULL
);

COMMENT
ON COLUMN "folder_permissions"."id" IS 'PK';

COMMENT
ON COLUMN "folder_permissions"."folder_id" IS 'FK';

COMMENT
ON COLUMN "folder_permissions"."user_id" IS 'FK';

COMMENT
ON COLUMN "folder_permissions"."role" IS 'ENUM';

CREATE TABLE "folders"
(
    "id"         UUID                    NOT NULL,
    "user_id"    UUID                    NOT NULL,
    "parent_id"  UUID                    NOT NULL,
    "name"       VARCHAR(100)            NOT NULL,
    "path"       VARCHAR(50) DEFAULT / NOT NULL,
    "deleted_at" TIMESTAMPTZ NULL,
    "created_at" TIMESTAMPTZ DEFAULT NOW NOT NULL,
    "updated_at" TIMESTAMPTZ             NOT NULL
);

COMMENT
ON COLUMN "folders"."id" IS 'PK';

COMMENT
ON COLUMN "folders"."user_id" IS 'FK';

COMMENT
ON COLUMN "folders"."parent_id" IS 'FK';

ALTER TABLE "users"
    ADD CONSTRAINT "PK_USERS" PRIMARY KEY (
                                           "id"
        );

ALTER TABLE "user_settings"
    ADD CONSTRAINT "PK_USER_SETTINGS" PRIMARY KEY (
                                                   "id"
        );

ALTER TABLE "Notes"
    ADD CONSTRAINT "PK_NOTES" PRIMARY KEY (
                                           "id"
        );

ALTER TABLE "note_tags"
    ADD CONSTRAINT "PK_NOTE_TAGS" PRIMARY KEY (
                                               "id"
        );

ALTER TABLE "tags"
    ADD CONSTRAINT "PK_TAGS" PRIMARY KEY (
                                          "id"
        );

ALTER TABLE "note_attachments_ref"
    ADD CONSTRAINT "PK_NOTE_ATTACHMENTS_REF" PRIMARY KEY (
                                                          "id"
        );

ALTER TABLE "attachments"
    ADD CONSTRAINT "PK_ATTACHMENTS" PRIMARY KEY (
                                                 "id"
        );

ALTER TABLE "shares_links"
    ADD CONSTRAINT "PK_SHARES_LINKS" PRIMARY KEY (
                                                  "id"
        );

ALTER TABLE "note_permissions"
    ADD CONSTRAINT "PK_NOTE_PERMISSIONS" PRIMARY KEY (
                                                      "id"
        );

ALTER TABLE "folder_permissions"
    ADD CONSTRAINT "PK_FOLDER_PERMISSIONS" PRIMARY KEY (
                                                        "id"
        );
d
ALTER TABLE "folders"
    ADD CONSTRAINT "PK_FOLDERS" PRIMARY KEY (
                                             "id"
        );

d