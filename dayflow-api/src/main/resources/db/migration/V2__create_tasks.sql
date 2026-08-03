CREATE SEQUENCE tasks_seq START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE tasks (
    id NUMBER(19) NOT NULL,
    user_id NUMBER(19) NOT NULL,
    title VARCHAR2(200 CHAR) NOT NULL,
    description VARCHAR2(4000 CHAR),
    status VARCHAR2(20 CHAR) NOT NULL,
    priority VARCHAR2(20 CHAR) NOT NULL,
    start_date TIMESTAMP(6) WITH TIME ZONE,
    due_date TIMESTAMP(6) WITH TIME ZONE,
    completed_at TIMESTAMP(6) WITH TIME ZONE,
    active NUMBER(1) DEFAULT 1 NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    created_by VARCHAR2(100 CHAR) NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_by VARCHAR2(100 CHAR) NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_tasks PRIMARY KEY (id),
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES app_users (id),
    CONSTRAINT ck_tasks_active CHECK (active IN (0, 1)),
    CONSTRAINT ck_tasks_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT ck_tasks_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT'))
);

CREATE INDEX ix_tasks_user_active ON tasks (user_id, active);
CREATE INDEX ix_tasks_user_status ON tasks (user_id, status);
CREATE INDEX ix_tasks_user_priority ON tasks (user_id, priority);
CREATE INDEX ix_tasks_due_date ON tasks (due_date);
