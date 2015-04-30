--
-- Title:      Upgraded Activiti tables from 5.14 to 5.16.2 version
-- Database:   Oracle9
-- Since:      V5.0 Schema 8004
-- Author:     Pavel Yurkevich
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--
-- Upgraded Activiti tables from 5.14 to 5.16.2 version, sql statements were copied from original activiti jar file.

alter table ACT_RU_TASK 
    add CATEGORY_ NVARCHAR2(255);
    
drop index ACT_UNIQ_RU_BUS_KEY;  

alter table ACT_RE_DEPLOYMENT 
    add TENANT_ID_ NVARCHAR2(255) default '';  
    
alter table ACT_RE_PROCDEF 
    add TENANT_ID_ NVARCHAR2(255) default ''; 
    
alter table ACT_RU_EXECUTION
    add TENANT_ID_ NVARCHAR2(255) default '';  
    
alter table ACT_RU_TASK
    add TENANT_ID_ NVARCHAR2(255) default '';
    
alter table ACT_RU_JOB
    add TENANT_ID_ NVARCHAR2(255) default ''; 
    
alter table ACT_RE_MODEL
    add TENANT_ID_ NVARCHAR2(255) default ''; 
    
alter table ACT_RU_EVENT_SUBSCR
   add TENANT_ID_ NVARCHAR2(255) default '';  
   
alter table ACT_RU_EVENT_SUBSCR
   add PROC_DEF_ID_ NVARCHAR2(64);   
    
alter table ACT_RE_PROCDEF
    drop constraint ACT_UNIQ_PROCDEF;
    
alter table ACT_RE_PROCDEF
    add constraint ACT_UNIQ_PROCDEF
    unique (KEY_,VERSION_, TENANT_ID_);          

update ACT_GE_PROPERTY set VALUE_ = '5.15' where NAME_ = 'schema.version';

alter table ACT_HI_TASKINST
    add CATEGORY_ NVARCHAR2(255);
    
drop index ACT_UNIQ_HI_BUS_KEY;    
    
alter table ACT_HI_VARINST
    add CREATE_TIME_ TIMESTAMP(6); 
    
alter table ACT_HI_VARINST
    add LAST_UPDATED_TIME_ TIMESTAMP(6);   
    
alter table ACT_HI_PROCINST
    add TENANT_ID_ NVARCHAR2(255) default ''; 
       
alter table ACT_HI_ACTINST
    add TENANT_ID_ NVARCHAR2(255) default ''; 
    
alter table ACT_HI_TASKINST
    add TENANT_ID_ NVARCHAR2(255) default '';    
    
alter table ACT_HI_ACTINST
    modify ASSIGNEE_ NVARCHAR2(255);
    
update ACT_GE_PROPERTY set VALUE_ = '5.15.1' where NAME_ = 'schema.version';

alter table ACT_RU_TASK
    add FORM_KEY_ NVARCHAR2(255);
    
alter table ACT_RU_EXECUTION
    add NAME_ NVARCHAR2(255);
    
create table ACT_EVT_LOG (
    LOG_NR_ NUMBER(19),
    TYPE_ NVARCHAR2(64),
    PROC_DEF_ID_ NVARCHAR2(64),
    PROC_INST_ID_ NVARCHAR2(64),
    EXECUTION_ID_ NVARCHAR2(64),
    TASK_ID_ NVARCHAR2(64),
    TIME_STAMP_ TIMESTAMP(6) not null,
    USER_ID_ NVARCHAR2(255),
    DATA_ BLOB,
    LOCK_OWNER_ NVARCHAR2(255),
    LOCK_TIME_ TIMESTAMP(6) null,
    IS_PROCESSED_ NUMBER(3) default 0,
    primary key (LOG_NR_)
);

create sequence act_evt_log_seq;    
    
    
update ACT_GE_PROPERTY set VALUE_ = '5.16' where NAME_ = 'schema.version';

alter table ACT_HI_PROCINST
    add NAME_ NVARCHAR2(255);
    
update ACT_GE_PROPERTY set VALUE_ = '5.16.1' where NAME_ = 'schema.version';
update ACT_GE_PROPERTY set VALUE_ = '5.16.2' where NAME_ = 'schema.version';

--
-- Record script finish
--
DELETE FROM alf_applied_patch WHERE id = 'patch.db-V5.0-upgrade-to-activiti-5.16.2';
INSERT INTO alf_applied_patch
  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
  VALUES
  (
    'patch.db-V5.0-upgrade-to-activiti-5.16.2', 'Manually executed script upgrade V5.0: Upgraded Activiti tables to 5.16.2 version',
    0, 8003, -1, 8004, null, 'UNKNOWN', ${TRUE}, ${TRUE}, 'Script completed'
  );