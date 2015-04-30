--
-- Title:      Fix jbpm tables
-- Database:   DB2
-- Since:      V3.3 schema 4013
-- Author:     janv
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--
-- Increase JBPM 3.3.1 default clob(255) (see jbpm.jpdl.db2.sql) to clob(4000)

ALTER TABLE jbpm_action            ALTER COLUMN expression_         SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_comment           ALTER COLUMN message_            SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_delegation        ALTER COLUMN classname_          SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_delegation        ALTER COLUMN configuration_      SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_exceptionhandler  ALTER COLUMN exceptionclassname_ SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_job               ALTER COLUMN exception_          SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_log               ALTER COLUMN exception_          SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_log               ALTER COLUMN message_            SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_log               ALTER COLUMN oldstringvalue_     SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_log               ALTER COLUMN newstringvalue_     SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_node              ALTER COLUMN description_        SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_processdefinition ALTER COLUMN description_        SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_task              ALTER COLUMN description_        SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_taskinstance      ALTER COLUMN description_        SET DATA TYPE CLOB(4000);
ALTER TABLE jbpm_transition        ALTER COLUMN description_        SET DATA TYPE CLOB(4000);
