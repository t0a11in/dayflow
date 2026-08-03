CREATE SEQUENCE app_users_seq START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE SEQUENCE roles_seq START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE app_users (
    id NUMBER(19) NOT NULL,
    email VARCHAR2(254 CHAR) NOT NULL,
    password_hash VARCHAR2(255 CHAR) NOT NULL,
    first_name VARCHAR2(100 CHAR) NOT NULL,
    last_name VARCHAR2(100 CHAR) NOT NULL,
    active NUMBER(1) DEFAULT 1 NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    created_by VARCHAR2(100 CHAR) NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_by VARCHAR2(100 CHAR) NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_app_users PRIMARY KEY (id),
    CONSTRAINT uq_app_users_email UNIQUE (email),
    CONSTRAINT ck_app_users_active CHECK (active IN (0, 1))
);

CREATE TABLE roles (
    id NUMBER(19) NOT NULL,
    code VARCHAR2(50 CHAR) NOT NULL,
    name VARCHAR2(100 CHAR) NOT NULL,
    active NUMBER(1) DEFAULT 1 NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    created_by VARCHAR2(100 CHAR) NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_by VARCHAR2(100 CHAR) NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_code UNIQUE (code),
    CONSTRAINT ck_roles_active CHECK (active IN (0, 1))
);

CREATE TABLE user_roles (
    user_id NUMBER(19) NOT NULL,
    role_id NUMBER(19) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES app_users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE INDEX ix_user_roles_role ON user_roles (role_id);

INSERT INTO roles (id, code, name, active, created_at, created_by, updated_at, updated_by, version)
VALUES (roles_seq.NEXTVAL, 'ADMIN', 'Administrator', 1, SYSTIMESTAMP, 'system', SYSTIMESTAMP, 'system', 0);

INSERT INTO roles (id, code, name, active, created_at, created_by, updated_at, updated_by, version)
VALUES (roles_seq.NEXTVAL, 'USER', 'User', 1, SYSTIMESTAMP, 'system', SYSTIMESTAMP, 'system', 0);

INSERT INTO roles (id, code, name, active, created_at, created_by, updated_at, updated_by, version)
VALUES (roles_seq.NEXTVAL, 'HOUSEHOLD_OWNER', 'Household owner', 1, SYSTIMESTAMP, 'system', SYSTIMESTAMP, 'system', 0);

INSERT INTO roles (id, code, name, active, created_at, created_by, updated_at, updated_by, version)
VALUES (roles_seq.NEXTVAL, 'HOUSEHOLD_MEMBER', 'Household member', 1, SYSTIMESTAMP, 'system', SYSTIMESTAMP, 'system', 0);
