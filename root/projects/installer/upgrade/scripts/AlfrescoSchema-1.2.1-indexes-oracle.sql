CREATE INDEX IDX_CA_PARENT ON child_assoc(parent_protocol, parent_identifier, parent_guid);
CREATE INDEX IDX_CA_CHILD ON child_assoc(child_protocol, child_identifier, child_guid);
CREATE INDEX IDX_NA_SOURCE on node_assoc(source_protocol, source_identifier, source_guid);
CREATE INDEX IDX_NA_TARGET on node_assoc(target_protocol, target_identifier, target_guid);
CREATE INDEX IDX_ASPECTS_REF ON node_aspects(protocol, identifier, guid);
CREATE INDEX IDX_NPE_REF ON node_perm_entry (protocol, identifier, guid);
CREATE INDEX IDX_CHANGE_TXN_ID ON node_status (change_txn_id);