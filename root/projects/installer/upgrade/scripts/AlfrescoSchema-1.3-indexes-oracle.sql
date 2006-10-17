--
-- Ensure that indexes required for foreign keys have been added. (Oracle Schema 1.3)
--
CREATE INDEX FKF064DF7560601995 ON alf_access_control_entry (permission_id);
CREATE INDEX FKF064DF75B25A50BF ON alf_access_control_entry (authority_id);
CREATE INDEX FKF064DF75B9553F6C ON alf_access_control_entry (acl_id);
CREATE INDEX FK31D3BA097B7FDE43 ON alf_auth_ext_keys (id);
CREATE INDEX FKC6EFFF3274173FF4 ON alf_child_assoc (child_node_id);
CREATE INDEX FKC6EFFF328E50E582 ON alf_child_assoc (parent_node_id);
CREATE INDEX FK33AE02B9553F6C   ON alf_node (acl_id);
CREATE INDEX FK33AE02D24ADD25   ON alf_node (protocol, identifier);
CREATE INDEX FKC962BF907F2C8017 ON alf_node_properties (node_id);
CREATE INDEX FK2B91A9DE7F2C8017 ON alf_node_aspects (node_id);
CREATE INDEX FK5BAEF398B69C43F3 ON alf_node_assoc (source_node_id);
CREATE INDEX FK5BAEF398A8FC7769 ON alf_node_assoc (target_node_id);
CREATE INDEX FK38ECB8CF7F2C8017 ON alf_node_status (node_id);
CREATE INDEX FK68AF8E122DBA5BA  ON alf_store (root_node_id);
