--
-- Title:      Upgraded Activiti tables to 5.11 version
-- Database:   SQLServer
-- Since:      V4.1 Schema 6023
-- Author:     Frederik Heremans
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--
-- Upgraded Activiti tables to 5.11 version
alter table ACT_RE_PROCDEF drop constraint ACT_UNIQ_PROCDEF;

alter table ACT_RE_PROCDEF alter column KEY_ nvarchar(255) not null;

alter table ACT_RE_PROCDEF alter column VERSION_ int not null;

alter table ACT_RE_PROCDEF
    add constraint ACT_UNIQ_PROCDEF
    unique (KEY_,VERSION_);

alter table ACT_RE_DEPLOYMENT add CATEGORY_ nvarchar(255);
    
alter table ACT_RE_PROCDEF
    add DESCRIPTION_ nvarchar(4000);
    
alter table ACT_RU_TASK
    add SUSPENSION_STATE_ int;
    
update ACT_RU_TASK set SUSPENSION_STATE_ = 1; 

alter table ACT_RU_EXECUTION
    add constraint ACT_FK_EXE_PROCDEF 
    foreign key (PROC_DEF_ID_) 
    references ACT_RE_PROCDEF (ID_);

create table ACT_RE_MODEL (
    ID_ nvarchar(64) not null,
    REV_ int,
    NAME_ nvarchar(255),
    KEY_ nvarchar(255),
    CATEGORY_ nvarchar(255),
    CREATE_TIME_ datetime,
    LAST_UPDATE_TIME_ datetime,
    VERSION_ int,
    META_INFO_ nvarchar(4000),
    DEPLOYMENT_ID_ nvarchar(64),
    EDITOR_SOURCE_VALUE_ID_ nvarchar(64),
    EDITOR_SOURCE_EXTRA_VALUE_ID_ nvarchar(64),
    primary key (ID_)
);

alter table ACT_RE_MODEL 
    add constraint ACT_FK_MODEL_SOURCE 
    foreign key (EDITOR_SOURCE_VALUE_ID_) 
    references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RE_MODEL 
    add constraint ACT_FK_MODEL_SOURCE_EXTRA 
    foreign key (EDITOR_SOURCE_EXTRA_VALUE_ID_) 
    references ACT_GE_BYTEARRAY (ID_);
    
alter table ACT_RE_MODEL 
    add constraint ACT_FK_MODEL_DEPLOYMENT 
    foreign key (DEPLOYMENT_ID_) 
    references ACT_RE_DEPLOYMENT (ID_);   

delete from ACT_GE_PROPERTY where NAME_ = 'historyLevel';

alter table ACT_RU_JOB
    add PROC_DEF_ID_ nvarchar(64);

create table ACT_HI_VARINST (
    ID_ nvarchar(64) not null,
    PROC_INST_ID_ nvarchar(64),
    EXECUTION_ID_ nvarchar(64),
    TASK_ID_ nvarchar(64),
    NAME_ nvarchar(255) not null,
    VAR_TYPE_ nvarchar(100),
    REV_ int,
    BYTEARRAY_ID_ nvarchar(64),
    DOUBLE_ double precision,
    LONG_ numeric(19,0),
    TEXT_ nvarchar(4000),
    TEXT2_ nvarchar(4000),
    primary key (ID_)
);

create index ACT_IDX_HI_PROCVAR_PROC_INST on ACT_HI_VARINST(PROC_INST_ID_);
create index ACT_IDX_HI_PROCVAR_NAME_TYPE on ACT_HI_VARINST(NAME_, VAR_TYPE_);
  
alter table ACT_HI_ACTINST
   add TASK_ID_ nvarchar(64);

alter table ACT_HI_ACTINST
   add CALL_PROC_INST_ID_ nvarchar(64);   

alter table ACT_HI_DETAIL
   alter column PROC_INST_ID_ nvarchar(64) null;

alter table ACT_HI_DETAIL
   alter column EXECUTION_ID_ nvarchar(64) null;
   
--
-- Update engine properties table
--
UPDATE ACT_GE_PROPERTY SET VALUE_ = '5.11' WHERE NAME_ = 'schema.version';
UPDATE ACT_GE_PROPERTY SET VALUE_ = VALUE_ + ' upgrade(5.11)' WHERE NAME_ = 'schema.history';

--
-- Record script finish
--
DELETE FROM alf_applied_patch WHERE id = 'patch.db-V4.2-upgrade-to-activiti-5.11';
INSERT INTO alf_applied_patch
  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
  VALUES
  (
    'patch.db-V4.2-upgrade-to-activiti-5.11', 'Manually executed script upgrade V4.2: Upgraded Activiti tables to 5.11 version',
    0, 5111, -1, 5112, null, 'UNKNOWN', ${TRUE}, ${TRUE}, 'Script completed'
  );