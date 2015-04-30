--
-- Title:      Update ACT_HI_PROCINST and ACT_RU_EXECUTION tables. Remove unused columns UNI_BUSINESS_KEY and UNI_PROC_DEF_ID
--             in both tables. Columns are unused after upgrade to Activiti 5.16.2
-- Database:   DB2
-- Since:      V5.0.1 Schema 8022
-- Author:     Alexander Malinovsky
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--
-- MNT-13262 : ACT: 4 unexpected columns found in database during clean upgrade from 4.0.2 > 5.0.1

-- Patch is applied only for DB2

--
-- Record script finish
--

--
-- ACT_HI_PROCINST
--
create table t_ACT_HI_PROCINST (
    ID_ varchar(64) not null,
    PROC_INST_ID_ varchar(64) not null,
    BUSINESS_KEY_ varchar(255),
    PROC_DEF_ID_ varchar(64) not null,
    START_TIME_ timestamp not null,
    END_TIME_ timestamp,
    DURATION_ bigint,
    START_USER_ID_ varchar(255),
    START_ACT_ID_ varchar(255),
    END_ACT_ID_ varchar(255),
    SUPER_PROCESS_INSTANCE_ID_ varchar(64),
    DELETE_REASON_ varchar(4000),
    TENANT_ID_ varchar(255) default '',
    NAME_ varchar(255),
    primary key (ID_)
);

INSERT INTO t_ACT_HI_PROCINST (ID_, PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, START_TIME_, END_TIME_, DURATION_, START_USER_ID_, START_ACT_ID_, END_ACT_ID_, SUPER_PROCESS_INSTANCE_ID_, DELETE_REASON_, TENANT_ID_, NAME_)
   SELECT ID_, PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, START_TIME_, END_TIME_, DURATION_, START_USER_ID_, START_ACT_ID_, END_ACT_ID_, SUPER_PROCESS_INSTANCE_ID_, DELETE_REASON_, TENANT_ID_, NAME_
   FROM ACT_HI_PROCINST;

drop index ACT_UNIQ_HI_BUS_KEY;  --(optional)
drop index ACT_IDX_HI_PRO_INST_END;
drop index ACT_IDX_HI_PRO_I_BUSKEY;
alter table ACT_HI_PROCINST drop constraint PROC_INST_ID_;  --(optional)
drop table ACT_HI_PROCINST;

rename t_ACT_HI_PROCINST to ACT_HI_PROCINST;

alter table ACT_HI_PROCINST add constraint PROC_INST_ID_ unique(PROC_INST_ID_);
create index ACT_IDX_HI_PRO_INST_END on ACT_HI_PROCINST(END_TIME_);
create index ACT_IDX_HI_PRO_I_BUSKEY on ACT_HI_PROCINST(BUSINESS_KEY_);

--
-- ACT_RU_EXECUTION
--
create table t_ACT_RU_EXECUTION (
    ID_ varchar(64) not null,
    REV_ integer,
    PROC_INST_ID_ varchar(64),
    BUSINESS_KEY_ varchar(255),
    PARENT_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    SUPER_EXEC_ varchar(64),
    ACT_ID_ varchar(255),
    IS_ACTIVE_ smallint check(IS_ACTIVE_ in (1,0)),
    IS_CONCURRENT_ smallint check(IS_CONCURRENT_ in (1,0)),
    IS_SCOPE_ smallint check(IS_SCOPE_ in (1,0)),
    IS_EVENT_SCOPE_ smallint check(IS_EVENT_SCOPE_ in (1,0)),
  	SUSPENSION_STATE_ integer,
  	CACHED_ENT_STATE_ integer,
	  TENANT_ID_ varchar(255) default '',
  	NAME_ varchar(255),
    primary key (ID_)
);

INSERT INTO t_ACT_RU_EXECUTION (ID_, REV_, PROC_INST_ID_, BUSINESS_KEY_, PARENT_ID_, PROC_DEF_ID_, SUPER_EXEC_, ACT_ID_, IS_ACTIVE_, IS_CONCURRENT_, IS_SCOPE_,  IS_EVENT_SCOPE_, SUSPENSION_STATE_, CACHED_ENT_STATE_, TENANT_ID_, NAME_)
   SELECT ID_, REV_, PROC_INST_ID_, BUSINESS_KEY_, PARENT_ID_, PROC_DEF_ID_, SUPER_EXEC_, ACT_ID_, IS_ACTIVE_, IS_CONCURRENT_, IS_SCOPE_,  IS_EVENT_SCOPE_, SUSPENSION_STATE_, CACHED_ENT_STATE_, TENANT_ID_, NAME_
   FROM ACT_RU_EXECUTION;

drop index ACT_UNIQ_RU_BUS_KEY;  --(optional)
drop index ACT_IDX_EXEC_BUSKEY;  --(optional)
drop index ACT_IDX_EXECUTION_PROC;  --(optional)
drop index ACT_IDX_EXECUTION_PARENT;  --(optional)
drop index ACT_IDX_EXECUTION_SUPER;  --(optional)
drop index ACT_IDX_EXECUTION_IDANDREV;  --(optional)
drop index ACT_IDX_EXEC_PROC_INST_ID;  --(optional)
alter table ACT_RU_EXECUTION drop foreign key ACT_FK_EXE_PROCINST;  --(optional)
alter table ACT_RU_EXECUTION drop foreign key ACT_FK_EXE_PARENT;  --(optional)
alter table ACT_RU_EXECUTION drop foreign key ACT_FK_EXE_SUPER;  --(optional)
alter table ACT_RU_EXECUTION drop foreign key ACT_FK_EXE_PROCDEF;  --(optional)

drop table ACT_RU_EXECUTION;
rename t_ACT_RU_EXECUTION to ACT_RU_EXECUTION;

create index ACT_IDX_EXEC_BUSKEY on ACT_RU_EXECUTION(BUSINESS_KEY_);
create index ACT_IDX_EXECUTION_PROC on ACT_RU_EXECUTION(PROC_DEF_ID_);
create index ACT_IDX_EXECUTION_PARENT on ACT_RU_EXECUTION(PARENT_ID_);
create index ACT_IDX_EXECUTION_SUPER on ACT_RU_EXECUTION(SUPER_EXEC_);
create index ACT_IDX_EXECUTION_IDANDREV on ACT_RU_EXECUTION(ID_, REV_);
create index ACT_IDX_EXEC_PROC_INST_ID on ACT_RU_EXECUTION(PROC_INST_ID_);
alter table ACT_RU_EXECUTION add constraint ACT_FK_EXE_PROCINST foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_);
alter table ACT_RU_EXECUTION add constraint ACT_FK_EXE_PARENT foreign key (PARENT_ID_) references ACT_RU_EXECUTION (ID_);
alter table ACT_RU_EXECUTION add constraint ACT_FK_EXE_SUPER foreign key (SUPER_EXEC_) references ACT_RU_EXECUTION (ID_);
alter table ACT_RU_EXECUTION add constraint ACT_FK_EXE_PROCDEF foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_);

-- constraints from other tables
alter table ACT_RU_IDENTITYLINK
    add constraint ACT_FK_IDL_PROCINST foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_);
alter table ACT_RU_TASK
    add constraint ACT_FK_TASK_EXE foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_);
alter table ACT_RU_TASK
    add constraint ACT_FK_TASK_PROCINST foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_);
alter table ACT_RU_VARIABLE
    add constraint ACT_FK_VAR_EXE foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_);
alter table ACT_RU_VARIABLE
    add constraint ACT_FK_VAR_PROCINST foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION(ID_);
alter table ACT_RU_EVENT_SUBSCR
    add constraint ACT_FK_EVENT_EXEC foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION(ID_);


--
-- Record script finish
--
DELETE FROM alf_applied_patch WHERE id = 'patch.db-V5.0-remove-columns-after-upgrade-to-activiti-5.16.2';
INSERT INTO alf_applied_patch
  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
  VALUES
  (
    'patch.db-V5.0-remove-columns-after-upgrade-to-activiti-5.16.2', 'Manually executed script upgrade V5.0: Upgraded Activiti tables to 5.16.2 version',
    0, 9002, -1, 9003, null, 'UNKNOWN', ${TRUE}, ${TRUE}, 'Script completed'
  );