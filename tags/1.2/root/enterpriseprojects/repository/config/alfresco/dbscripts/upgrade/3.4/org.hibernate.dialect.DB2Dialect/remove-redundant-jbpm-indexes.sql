--
-- Title:      Upgrade to V3.4 - Remove redundant indexes in jbpm tables
-- Database:   DB2
-- Since:      V3.4 schema 4211
-- Author:     Pavel Yurkevich
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--

DROP INDEX IDX_PLDACTR_ACTID;    -- (optional)
DROP INDEX IDX_PROCIN_KEY;    -- (optional)
DROP INDEX IDX_TASKINST_TSK;    -- (optional)
DROP INDEX IDX_TASK_ACTORID;    -- (optional)

--
-- Record script finish
--
DELETE FROM alf_applied_patch WHERE id = 'patch.db-V3.4-remove-redundant-jbpm-indexes';
INSERT INTO alf_applied_patch
  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
  VALUES
  (
    'patch.db-V3.4-remove-redundant-jbpm-indexes', 'Manually executed script upgrade V3.4',
     0, 6010, -1, 6011, null, 'UNKOWN', ${TRUE}, ${TRUE}, 'Script completed'
   );
