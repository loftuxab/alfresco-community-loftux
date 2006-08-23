-- ------------------------------------------------------
-- Alfresco Schema conversion V1.3 to V1.4
-- 
-- Author: Derek Hulley
-- ------------------------------------------------------

--
-- Record script start
--
insert into applied_patch
  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
  values
  (
    'patch.schemaUpdateScript-V1.4-1', 'Manually execute script upgrade V1.4 part 1',
    0, 18, -1, 19, now(), 'UNKOWN', 0, 0, 'Script started'
  );

--
-- Delete intermediate tables from previous upgrades
--

DROP TABLE IF EXISTS T_access_control_entry;
DROP TABLE IF EXISTS T_access_control_list;
DROP TABLE IF EXISTS T_applied_patch;
DROP TABLE IF EXISTS T_auth_ext_keys;
DROP TABLE IF EXISTS T_authority;
DROP TABLE IF EXISTS T_child_assoc;
DROP TABLE IF EXISTS T_node;
DROP TABLE IF EXISTS T_node_aspects;
DROP TABLE IF EXISTS T_node_assoc;
DROP TABLE IF EXISTS T_node_properties;
DROP TABLE IF EXISTS T_node_status;
DROP TABLE IF EXISTS T_permission;
DROP TABLE IF EXISTS T_store;
DROP TABLE IF EXISTS T_version_count;

--
-- Unique name constraint
--

CREATE TABLE T_dup_child_assoc (
  parent_node_id bigint(20) default NULL,
  child_node_id bigint(20) default NULL,
  type_qname varchar(255) NOT NULL,
  instance_count int NOT NULL,
  first_id bigint(20),
  PRIMARY KEY  (parent_node_id, type_qname, child_node_id)
);
ALTER TABLE T_dup_child_assoc ADD INDEX IDX_COUNT(instance_count);
ALTER TABLE T_dup_child_assoc ADD INDEX IDX_FIRST_ID(first_id);

-- Copy in all child associations
insert into T_dup_child_assoc
  (
    parent_node_id, type_qname, child_node_id,
    instance_count
  )
  select
    ca.parent_node_id, ca.type_qname, ca.child_node_id,
    count(ca.id)
  from
    child_assoc ca
  group by
    ca.parent_node_id, ca.type_qname, ca.child_node_id;

-- Record the ID of the first instance of the duplicate
update t_dup_child_assoc dup set dup.first_id =
  (
    select min(id) from child_assoc ca
      where
        ca.parent_node_id = dup.parent_node_id and
        ca.child_node_id = dup.child_node_id and
        ca.type_qname = dup.type_qname
  );

-- Delete duplicate instances
delete from child_assoc
  where id not in
  (
    select first_id from t_dup_child_assoc
  );

-- Remove non-duplicated assocs
delete from T_dup_child_assoc
  where
    instance_count < 2;

-- Apply new schema changes to child assoc table
ALTER TABLE child_assoc
  ADD COLUMN child_node_name VARCHAR(50) NOT NULL DEFAULT 'V1.4 upgrade' AFTER type_qname,
  ADD COLUMN child_node_name_crc bigint(20) NOT NULL DEFAULT -1 AFTER child_node_name;

UPDATE child_assoc
  SET child_node_name_crc = id * -1;

ALTER TABLE child_assoc
  ADD UNIQUE INDEX IDX_CHILD_NAMECRC(parent_node_id, type_qname, child_node_name_crc);
ALTER TABLE child_assoc
  ADD UNIQUE INDEX IDX_ASSOC(parent_node_id, type_qname, child_node_id);

-- Apply unique index for node associations
ALTER TABLE node_assoc
  ADD UNIQUE INDEX IDX_ASSOC(source_node_id, type_qname, target_node_id);

--
-- Rename tables to give 'alf_' prefix
--
ALTER TABLE access_control_entry RENAME TO alf_access_control_entry;
ALTER TABLE access_control_list RENAME TO alf_access_control_list;
ALTER TABLE applied_patch RENAME TO alf_applied_patch;
ALTER TABLE auth_ext_keys RENAME TO alf_auth_ext_keys;
ALTER TABLE authority RENAME TO alf_authority;
ALTER TABLE child_assoc RENAME TO alf_child_assoc;
ALTER TABLE node RENAME TO alf_node;
ALTER TABLE node_aspects RENAME TO alf_node_aspects;
ALTER TABLE node_assoc RENAME TO alf_node_assoc;
ALTER TABLE node_properties RENAME TO alf_node_properties;
ALTER TABLE node_status RENAME TO alf_node_status;
ALTER TABLE permission RENAME TO alf_permission;
ALTER TABLE store RENAME TO alf_store;
ALTER TABLE version_count RENAME TO alf_version_count;

--
-- Record script finish
--
delete from alf_applied_patch where id = 'patch.schemaUpdateScript-V1.4-1';
insert into alf_applied_patch
  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
  values
  (
    'patch.schemaUpdateScript-V1.4-1', 'Manually execute script upgrade V1.4 part 1',
    0, 18, -1, 19, now(), 'UNKOWN', 1, 1, 'Script completed'
  );