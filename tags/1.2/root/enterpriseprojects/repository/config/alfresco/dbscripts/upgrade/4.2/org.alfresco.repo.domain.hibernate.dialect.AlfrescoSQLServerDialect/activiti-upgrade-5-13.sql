--
-- Title:      Upgraded Activiti tables to 5.13 version
-- Database:   SQLServer
-- Since:      V4.1 Schema 6029
-- Author:     Frederik Heremans
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--
-- Upgraded Activiti tables to 5.13 version
alter table ACT_HI_TASKINST
  add CLAIM_TIME_ datetime;

alter table ACT_HI_TASKINST
  add FORM_KEY_ nvarchar(255);
  
alter table ACT_RU_IDENTITYLINK
  add PROC_INST_ID_ nvarchar(64);
  
alter table ACT_RU_IDENTITYLINK
    add constraint ACT_FK_IDL_PROCINST
    foreign key (PROC_INST_ID_) 
    references ACT_RU_EXECUTION (ID_);     
  
create index ACT_IDX_HI_ACT_INST_EXEC on ACT_HI_ACTINST(EXECUTION_ID_, ACT_ID_);  

create table ACT_HI_IDENTITYLINK (
    ID_ nvarchar(64),
    GROUP_ID_ nvarchar(255),
    TYPE_ nvarchar(255),
    USER_ID_ nvarchar(255),
    TASK_ID_ nvarchar(64),
    PROC_INST_ID_ nvarchar(64),
    primary key (ID_)
);

create index ACT_IDX_HI_IDENT_LNK_USER on ACT_HI_IDENTITYLINK(USER_ID_);
create index ACT_IDX_HI_IDENT_LNK_TASK on ACT_HI_IDENTITYLINK(TASK_ID_);
create index ACT_IDX_HI_IDENT_LNK_PROCINST on ACT_HI_IDENTITYLINK(PROC_INST_ID_);

-- Using tinyint instead of int. No risk of precision-loss, as the values in this column are either 0 or 1
alter table ACT_RE_PROCDEF alter column SUSPENSION_STATE_ tinyint;
alter table ACT_RU_EXECUTION alter column SUSPENSION_STATE_ tinyint;

-- Additional indexes added to prevent full-table locking on referenced tables by foreign-key column
create index ACT_IDX_EXECUTION_PROC on ACT_RU_EXECUTION(PROC_DEF_ID_);
create index ACT_IDX_EXECUTION_PARENT on ACT_RU_EXECUTION(PARENT_ID_);
create index ACT_IDX_EXECUTION_SUPER on ACT_RU_EXECUTION(SUPER_EXEC_);
create index ACT_IDX_EXECUTION_IDANDREV on ACT_RU_EXECUTION(ID_, REV_);
create index ACT_IDX_VARIABLE_BA on ACT_RU_VARIABLE(BYTEARRAY_ID_);
create index ACT_IDX_VARIABLE_EXEC on ACT_RU_VARIABLE(EXECUTION_ID_);
create index ACT_IDX_VARIABLE_PROCINST on ACT_RU_VARIABLE(PROC_INST_ID_);
create index ACT_IDX_IDENT_LNK_TASK on ACT_RU_IDENTITYLINK(TASK_ID_);
create index ACT_IDX_IDENT_LNK_PROCINST on ACT_RU_IDENTITYLINK(PROC_INST_ID_);
create index ACT_IDX_TASK_EXEC on ACT_RU_TASK(EXECUTION_ID_);
create index ACT_IDX_TASK_PROCINST on ACT_RU_TASK(PROC_INST_ID_);

-- Update binary data-types from image to varbinary(max)
alter table ACT_GE_BYTEARRAY alter column BYTES_ varbinary(max);
alter table ACT_HI_COMMENT alter column FULL_MSG_ varbinary(max);
alter table ACT_ID_INFO alter column PASSWORD_ varbinary(max);
   
--
-- Update engine properties table
--
UPDATE ACT_GE_PROPERTY SET VALUE_ = '5.13' WHERE NAME_ = 'schema.version';
UPDATE ACT_GE_PROPERTY SET VALUE_ = VALUE_ + ' upgrade(5.13)' WHERE NAME_ = 'schema.history';

--
-- Record script finish
--
DELETE FROM alf_applied_patch WHERE id = 'patch.db-V4.2-upgrade-to-activiti-5.13';
INSERT INTO alf_applied_patch
  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
  VALUES
  (
    'patch.db-V4.2-upgrade-to-activiti-5.13', 'Manually executed script upgrade V4.2: Upgraded Activiti tables to 5.13 version',
    0, 6028, -1, 6029, null, 'UNKNOWN', ${TRUE}, ${TRUE}, 'Script completed'
  );