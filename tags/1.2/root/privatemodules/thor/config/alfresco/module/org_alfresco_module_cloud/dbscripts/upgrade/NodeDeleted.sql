--
-- Title:      Update alf_txn table and alf_node indexes to support SOLR tracking
-- Database:   MySQL
-- Since:      V4.1 Schema 5113
-- Author:     Derek Hulley
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--



--ASSIGN:SYSTEM_NS_ID=id
select id from alf_namespace where uri = 'http://www.alfresco.org/model/system/1.0';


insert into alf_qname (version, ns_id, local_name) values (0, ${SYSTEM_NS_ID}, 'deleted');         -- (optional)


--ASSIGN:DELETED_TYPE_ID=id
select id from alf_qname where ns_id = ${SYSTEM_NS_ID} and local_name = 'deleted';

-- Give the correct type to all deleted nodes
update alf_node set type_qname_id = ${DELETED_TYPE_ID} where node_deleted = 1;

-- Drop the unused column
alter table alf_node
   change node_deleted node_deleted bit not null default 0,
   add key idx_alf_node_txn_type (transaction_id, type_qname_id)
;

--
-- Record script finish
--
DELETE FROM alf_applied_patch WHERE id = 'patch.db-V4.1-NodeDeleted';
INSERT INTO alf_applied_patch
  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
  VALUES
  (
    'patch.db-V4.1-NodeDeleted', 'Manually executed script upgrade V4.1: Remove node_deleted',
    0, 6014, -1, 6015, null, 'UNKNOWN', ${TRUE}, ${TRUE}, 'Script completed'
  );
