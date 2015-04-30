--
-- Title:      Update ACT_HI_PROCINST table. Create normal name for unique constraint on PROC_INST_ID_
-- Database:   DB2
-- Since:      V4.1 Schema 5116
-- Author:     Dmitry Vaserin
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--
-- ALF-15828 : DB2: unexpected index found in database.

-- Patch is applied only for DB2, see ALF-15828

--
-- Record script finish
--

drop index ACT_UNIQ_HI_BUS_KEY;
drop index ACT_IDX_HI_PRO_INST_END;
drop index ACT_IDX_HI_PRO_I_BUSKEY;
alter table ACT_HI_PROCINST drop constraint PROC_INST_ID_;  --(optional)

rename ACT_HI_PROCINST to t_ACT_HI_PROCINST;

create table ACT_HI_PROCINST (
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
    UNI_BUSINESS_KEY varchar (255)  not null  generated always as (case when "BUSINESS_KEY_" is null then "ID_" else "BUSINESS_KEY_" end),
    UNI_PROC_DEF_ID varchar (64)  not null  generated always as (case when "PROC_DEF_ID_" is null then "ID_" else "PROC_DEF_ID_" end),
    DELETE_REASON_ varchar(4000),
    primary key (ID_)
);

alter table ACT_HI_PROCINST add constraint PROC_INST_ID_ unique(PROC_INST_ID_);
create unique index ACT_UNIQ_HI_BUS_KEY on ACT_HI_PROCINST(UNI_PROC_DEF_ID, UNI_BUSINESS_KEY);
create index ACT_IDX_HI_PRO_INST_END on ACT_HI_PROCINST(END_TIME_);
create index ACT_IDX_HI_PRO_I_BUSKEY on ACT_HI_PROCINST(BUSINESS_KEY_);

insert into ACT_HI_PROCINST
(ID_, PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, START_TIME_, END_TIME_, DURATION_, START_USER_ID_, START_ACT_ID_, END_ACT_ID_, SUPER_PROCESS_INSTANCE_ID_)
(
    select
       ID_, PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, START_TIME_, END_TIME_, DURATION_, START_USER_ID_, START_ACT_ID_, END_ACT_ID_, SUPER_PROCESS_INSTANCE_ID_
    from
       t_ACT_HI_PROCINST
);

drop table t_ACT_HI_PROCINST;

--
-- Record script finish
--
DELETE FROM alf_applied_patch WHERE id = 'patch.db-V4.1-rename-constraint-activiti';
INSERT INTO alf_applied_patch
  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
  VALUES
  (
    'patch.db-V4.1-rename-constraint-activiti', 'Manually executed script upgrade V4.1: Rename PROC_INST_ID_ constraint',
    0, 6020, -1, 6021, null, 'UNKNOWN', ${TRUE}, ${TRUE}, 'Script completed'
  );