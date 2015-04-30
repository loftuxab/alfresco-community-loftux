--
-- Title:      Account table
-- Database:   MySQL InnoDB
-- Since:      Thor Schema 5013
-- Author:     janv
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--

CREATE TABLE alf_account
(
   id BIGINT NOT NULL AUTO_INCREMENT,
   domain VARCHAR(255) NOT NULL,
   type_id BIGINT NOT NULL,
   creation_date DATETIME NOT NULL,
   name VARCHAR(75),
   UNIQUE INDEX idx_alf_acc_dom (domain),
   PRIMARY KEY (id)
) ENGINE=InnoDB;

--
-- Record script finish
--
--DELETE FROM alf_applied_patch WHERE id = 'patch.db-V4.0-AccountTables';
--INSERT INTO alf_applied_patch
--  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
--  VALUES
--  (
--    'patch.db-V4.0-TenantTables', 'Manually executed script upgrade V4.0: Tenant Tables',
--    0, 5012, -1, 5013, null, 'UNKNOWN', ${TRUE}, ${TRUE}, 'Script completed'
--  );