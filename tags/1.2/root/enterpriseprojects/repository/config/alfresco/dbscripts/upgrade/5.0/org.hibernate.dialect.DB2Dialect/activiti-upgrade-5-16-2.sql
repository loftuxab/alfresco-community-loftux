--
-- Title:      Upgraded Activiti tables from 5.14 to 5.16.2 version
-- Database:   DB2
-- Since:      V5.0 Schema 8004
-- Author:     Pavel Yurkevich
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--
-- Upgraded Activiti tables from 5.14 to 5.16.2 version, sql statements were copied from original activiti jar file.

alter table ACT_RU_TASK 
    add CATEGORY_ varchar(255);

Call Sysproc.admin_cmd ('REORG TABLE ACT_RU_TASK');
        
drop index ACT_UNIQ_RU_BUS_KEY;

-- DB2 *cannot* drop columns. Yes, this is 2013.
-- This means that for DB2 the columns will remain as they are (they won't be used)
-- alter table ACT_RU_EXECUTION drop colum UNI_BUSINESS_KEY;
-- alter table ACT_RU_EXECUTION drop colum UNI_PROC_DEF_ID;

Call Sysproc.admin_cmd ('REORG TABLE ACT_RU_EXECUTION');

alter table ACT_RE_DEPLOYMENT 
    add TENANT_ID_ varchar(255) default '';
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_RE_DEPLOYMENT');

alter table ACT_RE_PROCDEF 
    add TENANT_ID_ varchar(255) not null default '';
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_RE_PROCDEF');

alter table ACT_RU_EXECUTION
    add TENANT_ID_ varchar(255) default '';
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_RU_EXECUTION');  

alter table ACT_RU_TASK
    add TENANT_ID_ varchar(255) default '';
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_RU_TASK');   

alter table ACT_RU_JOB
    add TENANT_ID_ varchar(255) default '';
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_RU_JOB');  

alter table ACT_RE_MODEL
    add TENANT_ID_ varchar(255) default '';
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_RE_MODEL');  

alter table ACT_RU_EVENT_SUBSCR
   add TENANT_ID_ varchar(255) default '';  
   
alter table ACT_RU_EVENT_SUBSCR
   add PROC_DEF_ID_ varchar(64);      
   
Call Sysproc.admin_cmd ('REORG TABLE ACT_RU_EVENT_SUBSCR');     

alter table ACT_RE_PROCDEF
    drop unique ACT_UNIQ_PROCDEF;
    
alter table ACT_RE_PROCDEF
    add constraint ACT_UNIQ_PROCDEF
    unique (KEY_,VERSION_, TENANT_ID_);  
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_RE_PROCDEF');

update ACT_GE_PROPERTY set VALUE_ = '5.15' where NAME_ = 'schema.version';

alter table ACT_HI_TASKINST
    add CATEGORY_ varchar(255);
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_HI_TASKINST');    
    
drop index ACT_UNIQ_HI_BUS_KEY;

alter table ACT_HI_VARINST
    add CREATE_TIME_ timestamp; 
    
alter table ACT_HI_VARINST
    add LAST_UPDATED_TIME_ timestamp; 

-- DB2 *cannot* drop columns. Yes, this is 2013.
-- This means that for DB2 the columns will remain as they are (they won't be used)
-- alter table ACT_HI_PROCINST drop colum UNI_BUSINESS_KEY;
-- alter table ACT_HI_PROCINST drop colum UNI_PROC_DEF_ID;
-- Call Sysproc.admin_cmd ('REORG TABLE ACT_HI_PROCINST'); 


alter table ACT_HI_PROCINST
    add TENANT_ID_ varchar(255) default ''; 
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_HI_PROCINST');
       
alter table ACT_HI_ACTINST
    add TENANT_ID_ varchar(255) default ''; 
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_HI_ACTINST');    
    
alter table ACT_HI_TASKINST
    add TENANT_ID_ varchar(255) default '';  
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_HI_TASKINST');      

alter table ACT_HI_ACTINST alter column ASSIGNEE_ SET DATA TYPE varchar(255);

Call Sysproc.admin_cmd ('REORG TABLE ACT_HI_ACTINST');

update ACT_GE_PROPERTY set VALUE_ = '5.15.1' where NAME_ = 'schema.version';

alter table ACT_RU_TASK
    add FORM_KEY_ varchar(255);
    
Call Sysproc.admin_cmd ('REORG TABLE ACT_RU_TASK');
    
alter table ACT_RU_EXECUTION
    add NAME_ varchar(255);

Call Sysproc.admin_cmd ('REORG TABLE ACT_RU_EXECUTION');
    
create table ACT_EVT_LOG (
    LOG_NR_ bigint not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    TYPE_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    EXECUTION_ID_ varchar(64),
    TASK_ID_ varchar(64),
    TIME_STAMP_ timestamp not null,
    USER_ID_ varchar(255),
    DATA_ BLOB,
    LOCK_OWNER_ varchar(255),
    LOCK_TIME_ timestamp,
    IS_PROCESSED_ integer default 0,
    primary key (LOG_NR_)
);
        
update ACT_GE_PROPERTY set VALUE_ = '5.16' where NAME_ = 'schema.version';

alter table ACT_HI_PROCINST
	add NAME_ varchar(255);
	
Call Sysproc.admin_cmd ('REORG TABLE ACT_HI_PROCINST');

update ACT_GE_PROPERTY set VALUE_ = '5.16.1' where NAME_ = 'schema.version';

create index ACT_IDX_EVENT_SUBSCR_EXEC_ID on ACT_RU_EVENT_SUBSCR(EXECUTION_ID_);
create index ACT_IDX_EXECUTION_IDANDREV on ACT_RU_EXECUTION(ID_, REV_);
create index ACT_IDX_EXECUTION_PARENT on ACT_RU_EXECUTION(PARENT_ID_);
create index ACT_IDX_EXECUTION_PROC on ACT_RU_EXECUTION(PROC_DEF_ID_);
create index ACT_IDX_EXECUTION_SUPER on ACT_RU_EXECUTION(SUPER_EXEC_);
create index ACT_IDX_EXEC_PROC_INST_ID on ACT_RU_EXECUTION(PROC_INST_ID_);
create index ACT_IDX_IDENT_LNK_PROCINST on ACT_RU_IDENTITYLINK(PROC_INST_ID_);
create index ACT_IDX_IDENT_LNK_TASK on ACT_RU_IDENTITYLINK(TASK_ID_);
create index ACT_IDX_JOB_EXCEPTION_STACK_ID on ACT_RU_JOB(EXCEPTION_STACK_ID_);
create index ACT_IDX_TASK_EXEC on ACT_RU_TASK(EXECUTION_ID_);
create index ACT_IDX_TASK_PROCINST on ACT_RU_TASK(PROC_INST_ID_);
create index ACT_IDX_TASK_PROC_DEF_ID on ACT_RU_TASK(PROC_DEF_ID_);
create index ACT_IDX_VARIABLE_BA on ACT_RU_VARIABLE(BYTEARRAY_ID_);
create index ACT_IDX_VARIABLE_EXEC on ACT_RU_VARIABLE(EXECUTION_ID_);
create index ACT_IDX_VARIABLE_PROCINST on ACT_RU_VARIABLE(PROC_INST_ID_);

alter table ACT_HI_ACTINST
	add OWNER_ varchar(64);

Call Sysproc.admin_cmd ('REORG TABLE ACT_HI_ACTINST');
    
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
