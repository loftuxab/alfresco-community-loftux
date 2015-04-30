--
-- Title:      Increase column sizes for Activiti
-- Database:   DB2
-- Since:      V4.1 Schema 5112
-- Author:     Alex Mukha
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--
-- ALF-14983 : Upgrade scripts to increase column sizes for Activiti

-- ACT_RU_TASK table
drop index ACT_IDX_TASK_CREATE;
drop index ACT_IDX_TASK_EXEC; --(optional)
drop index ACT_IDX_TASK_PROCINST; --(optional)
drop index ACT_IDX_TASK_PROC_DEF_ID; --(optional)
alter table ACT_RU_TASK drop constraint ACT_FK_TASK_EXE;
alter table ACT_RU_TASK drop constraint ACT_FK_TASK_PROCINST;
alter table ACT_RU_TASK drop constraint ACT_FK_TASK_PROCDEF;
alter table ACT_RU_IDENTITYLINK drop constraint ACT_FK_TSKASS_TASK;

rename ACT_RU_TASK to t_ACT_RU_TASK;

create table ACT_RU_TASK (
    ID_ varchar(64) not null,
    REV_ integer,
    EXECUTION_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    NAME_ varchar(255),
    PARENT_TASK_ID_ varchar(64),
    DESCRIPTION_ varchar(4000),
    TASK_DEF_KEY_ varchar(255),
    OWNER_ varchar(255),
    ASSIGNEE_ varchar(255),
    DELEGATION_ varchar(64),
    PRIORITY_ integer,
    CREATE_TIME_ timestamp,
    DUE_DATE_ timestamp,
    primary key (ID_)
);

create index ACT_IDX_TASK_CREATE on ACT_RU_TASK(CREATE_TIME_);
create index ACT_IDX_TASK_EXEC on ACT_RU_TASK(EXECUTION_ID_);
create index ACT_IDX_TASK_PROCINST on ACT_RU_TASK(PROC_INST_ID_);
create index ACT_IDX_TASK_PROC_DEF_ID on ACT_RU_TASK(PROC_DEF_ID_);
alter table ACT_RU_TASK add constraint ACT_FK_TASK_EXE foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_);
alter table ACT_RU_TASK add constraint ACT_FK_TASK_PROCINST foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_);
alter table ACT_RU_TASK add constraint ACT_FK_TASK_PROCDEF foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_);

insert into ACT_RU_TASK
(ID_, REV_, EXECUTION_ID_, PROC_INST_ID_, PROC_DEF_ID_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, TASK_DEF_KEY_, OWNER_, ASSIGNEE_, DELEGATION_, PRIORITY_, CREATE_TIME_, DUE_DATE_)
(
    select
       ID_, REV_, EXECUTION_ID_, PROC_INST_ID_, PROC_DEF_ID_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, TASK_DEF_KEY_, OWNER_, ASSIGNEE_, DELEGATION_, PRIORITY_, CREATE_TIME_, DUE_DATE_
    from
       t_ACT_RU_TASK
);

drop table t_ACT_RU_TASK;

alter table ACT_RU_IDENTITYLINK add constraint ACT_FK_TSKASS_TASK foreign key (TASK_ID_) references ACT_RU_TASK (ID_);

-- ACT_RU_IDENTITYLINK table

drop index ACT_IDX_IDENT_LNK_USER;
drop index ACT_IDX_IDENT_LNK_GROUP;
drop index ACT_IDX_IDENT_LNK_TASK; --(optional)

alter table ACT_RU_IDENTITYLINK drop constraint ACT_FK_TSKASS_TASK;

rename ACT_RU_IDENTITYLINK to t_ACT_RU_IDENTITYLINK;

create table ACT_RU_IDENTITYLINK (
    ID_ varchar(64) not null,
    REV_ integer,
    GROUP_ID_ varchar(255),
    TYPE_ varchar(255),
    USER_ID_ varchar(255),
    TASK_ID_ varchar(64),
    primary key (ID_)
);

create index ACT_IDX_IDENT_LNK_USER on ACT_RU_IDENTITYLINK(USER_ID_);
create index ACT_IDX_IDENT_LNK_GROUP on ACT_RU_IDENTITYLINK(GROUP_ID_);
create index ACT_IDX_IDENT_LNK_TASK on ACT_RU_IDENTITYLINK(TASK_ID_);
alter table ACT_RU_IDENTITYLINK add constraint ACT_FK_TSKASS_TASK foreign key (TASK_ID_) references ACT_RU_TASK (ID_);

insert into ACT_RU_IDENTITYLINK
(ID_, REV_, GROUP_ID_, TYPE_, USER_ID_, TASK_ID_)
(
    select
       ID_, REV_, GROUP_ID_, TYPE_, USER_ID_, TASK_ID_
    from
       t_ACT_RU_IDENTITYLINK
);

drop table t_ACT_RU_IDENTITYLINK;


-- ACT_HI_TASKINST table

rename ACT_HI_TASKINST to t_ACT_HI_TASKINST;

create table ACT_HI_TASKINST (
    ID_ varchar(64) not null,
    PROC_DEF_ID_ varchar(64),
    TASK_DEF_KEY_ varchar(255),
    PROC_INST_ID_ varchar(64),
    EXECUTION_ID_ varchar(64),
    NAME_ varchar(255),
    PARENT_TASK_ID_ varchar(64),
    DESCRIPTION_ varchar(4000),
    OWNER_ varchar(255),
    ASSIGNEE_ varchar(255),
    START_TIME_ timestamp not null,
    END_TIME_ timestamp,
    DURATION_ bigint,
    DELETE_REASON_ varchar(4000),
    PRIORITY_ integer,
    DUE_DATE_ timestamp,
    primary key (ID_)
);

insert into ACT_HI_TASKINST
(ID_, PROC_DEF_ID_, TASK_DEF_KEY_, PROC_INST_ID_, EXECUTION_ID_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, OWNER_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, PRIORITY_, DUE_DATE_)
(
    select
       ID_, PROC_DEF_ID_, TASK_DEF_KEY_, PROC_INST_ID_, EXECUTION_ID_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, OWNER_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, PRIORITY_, DUE_DATE_
    from
       t_ACT_HI_TASKINST
);

drop table t_ACT_HI_TASKINST;

-- ACT_HI_ACTINST table
drop index ACT_IDX_HI_ACT_INST_START;
drop index ACT_IDX_HI_ACT_INST_END;

rename ACT_HI_ACTINST to t_ACT_HI_ACTINST;

create table ACT_HI_ACTINST (
    ID_ varchar(64) not null,
    PROC_DEF_ID_ varchar(64) not null,
    PROC_INST_ID_ varchar(64) not null,
    EXECUTION_ID_ varchar(64) not null,
    ACT_ID_ varchar(255) not null,
    ACT_NAME_ varchar(255),
    ACT_TYPE_ varchar(255) not null,
    ASSIGNEE_ varchar(255),
    START_TIME_ timestamp not null,
    END_TIME_ timestamp,
    DURATION_ bigint,
    primary key (ID_)
);

create index ACT_IDX_HI_ACT_INST_START on ACT_HI_ACTINST(START_TIME_);
create index ACT_IDX_HI_ACT_INST_END on ACT_HI_ACTINST(END_TIME_);

insert into ACT_HI_ACTINST
(ID_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_)
(
    select
       ID_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_
    from
       t_ACT_HI_ACTINST
);

drop table t_ACT_HI_ACTINST;

--
-- Record script finish
--
DELETE FROM alf_applied_patch WHERE id = 'patch.db-V4.1-increase-column-size-activiti';
INSERT INTO alf_applied_patch
  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
  VALUES
  (
    'patch.db-V4.1-increase-column-size-activiti', 'ALF-14983 : Upgrade scripts to increase column sizes for Activiti',
    0, 6012, -1, 6013, null, 'UNKNOWN', ${TRUE}, ${TRUE}, 'Script completed'
  );