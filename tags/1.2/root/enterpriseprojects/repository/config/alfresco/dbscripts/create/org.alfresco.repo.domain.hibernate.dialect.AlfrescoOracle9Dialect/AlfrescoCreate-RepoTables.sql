--
-- Title:      Core Repository Tables
-- Database:   Oracle
-- Since:      V3.3 Schema 4000
-- Author:     unknown
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--

CREATE TABLE alf_applied_patch
(
    id VARCHAR2(64 CHAR) NOT NULL,
    description VARCHAR2(1024 CHAR),
    fixes_from_schema NUMBER(10,0),
    fixes_to_schema NUMBER(10,0),
    applied_to_schema NUMBER(10,0),
    target_schema NUMBER(10,0),
    applied_on_date TIMESTAMP,
    applied_to_server VARCHAR2(64 CHAR),
    was_executed NUMBER(1,0),
    succeeded NUMBER(1,0),
    report VARCHAR2(1024 CHAR),
    PRIMARY KEY (id)
);

CREATE SEQUENCE alf_locale_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_locale
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    locale_str VARCHAR2(20 CHAR) NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX locale_str ON alf_locale (locale_str);

CREATE SEQUENCE alf_namespace_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_namespace
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    uri VARCHAR2(100 CHAR) NOT NULL,    
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX uri ON alf_namespace (uri);

CREATE SEQUENCE alf_qname_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_qname
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    ns_id NUMBER(19,0) NOT NULL,
    local_name VARCHAR2(200 CHAR) NOT NULL,
    CONSTRAINT fk_alf_qname_ns FOREIGN KEY (ns_id) REFERENCES alf_namespace (id),    
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX ns_id ON alf_qname (ns_id, local_name);

CREATE SEQUENCE alf_permission_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_permission
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    type_qname_id NUMBER(19,0) NOT NULL,
    name VARCHAR2(100 CHAR) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_alf_perm_tqn FOREIGN KEY (type_qname_id) REFERENCES alf_qname (id)
);
CREATE UNIQUE INDEX type_qname_id ON alf_permission (type_qname_id, name);
CREATE INDEX fk_alf_perm_tqn ON alf_permission (type_qname_id);

CREATE SEQUENCE alf_ace_context_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_ace_context
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    class_context VARCHAR2(1024 CHAR),
    property_context VARCHAR2(1024 CHAR),
    kvp_context VARCHAR2(1024 CHAR),
    PRIMARY KEY (id)
);

CREATE SEQUENCE alf_authority_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_authority
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    authority VARCHAR2(100 CHAR),
    crc NUMBER(19,0),
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX authority ON alf_authority (authority, crc);
CREATE INDEX idx_alf_auth_aut ON alf_authority (authority);

CREATE SEQUENCE alf_access_control_entry_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_access_control_entry
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    permission_id NUMBER(19,0) NOT NULL,
    authority_id NUMBER(19,0) NOT NULL,
    allowed NUMBER(1,0) NOT NULL,
    applies NUMBER(10,0) NOT NULL,
    context_id NUMBER(19,0),
    PRIMARY KEY (id),
    CONSTRAINT fk_alf_ace_auth FOREIGN KEY (authority_id) REFERENCES alf_authority (id),
    CONSTRAINT fk_alf_ace_ctx FOREIGN KEY (context_id) REFERENCES alf_ace_context (id),
    CONSTRAINT fk_alf_ace_perm FOREIGN KEY (permission_id) REFERENCES alf_permission (id)
);
CREATE UNIQUE INDEX permission_id ON alf_access_control_entry (permission_id, authority_id, allowed, applies);
CREATE INDEX fk_alf_ace_ctx ON alf_access_control_entry (context_id);
CREATE INDEX fk_alf_ace_perm ON alf_access_control_entry (permission_id);
CREATE INDEX fk_alf_ace_auth ON alf_access_control_entry (authority_id);

CREATE SEQUENCE alf_acl_change_set_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_acl_change_set
(
    id NUMBER(19,0) NOT NULL,
    commit_time_ms NUMBER(19,0),
    PRIMARY KEY (id)
);
CREATE INDEX idx_alf_acs_ctms ON alf_acl_change_set (commit_time_ms);

CREATE SEQUENCE alf_access_control_list_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_access_control_list
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    acl_id VARCHAR2(36 CHAR)  NOT NULL,
    latest NUMBER(1,0) NOT NULL,
    acl_version NUMBER(19,0) NOT NULL,
    inherits NUMBER(1,0) NOT NULL,
    inherits_from NUMBER(19,0),
    type NUMBER(10,0) NOT NULL,
    inherited_acl NUMBER(19,0),
    is_versioned NUMBER(1,0) NOT NULL,
    requires_version NUMBER(1,0) NOT NULL,
    acl_change_set NUMBER(19,0),
    PRIMARY KEY (id),
    CONSTRAINT fk_alf_acl_acs FOREIGN KEY (acl_change_set) REFERENCES alf_acl_change_set (id)
);
CREATE UNIQUE INDEX acl_id ON alf_access_control_list (acl_id, latest, acl_version);
CREATE INDEX idx_alf_acl_inh ON alf_access_control_list (inherits, inherits_from);
CREATE INDEX fk_alf_acl_acs ON alf_access_control_list (acl_change_set);

CREATE SEQUENCE alf_acl_member_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_acl_member
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    acl_id NUMBER(19,0) NOT NULL,
    ace_id NUMBER(19,0) NOT NULL,
    pos NUMBER(10,0) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_alf_aclm_ace FOREIGN KEY (ace_id) REFERENCES alf_access_control_entry (id),
    CONSTRAINT fk_alf_aclm_acl FOREIGN KEY (acl_id) REFERENCES alf_access_control_list (id)
);
CREATE UNIQUE INDEX aclm_acl_id ON alf_acl_member (acl_id, ace_id, pos);
CREATE INDEX fk_alf_aclm_acl ON alf_acl_member (acl_id);
CREATE INDEX fk_alf_aclm_ace ON alf_acl_member (ace_id);

CREATE SEQUENCE alf_authority_alias_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_authority_alias
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    auth_id NUMBER(19,0) NOT NULL,
    alias_id NUMBER(19,0) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_alf_autha_aut FOREIGN KEY (auth_id) REFERENCES alf_authority (id),
    CONSTRAINT fk_alf_autha_ali FOREIGN KEY (alias_id) REFERENCES alf_authority (id)
);
CREATE UNIQUE INDEX auth_id ON alf_authority_alias (auth_id, alias_id);
CREATE INDEX fk_alf_autha_ali ON alf_authority_alias (alias_id);
CREATE INDEX fk_alf_autha_aut ON alf_authority_alias (auth_id);

CREATE SEQUENCE alf_server_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_server
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    ip_address VARCHAR2(39 CHAR) NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX ip_address ON alf_server (ip_address);

CREATE SEQUENCE alf_transaction_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_transaction
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    server_id NUMBER(19,0),
    change_txn_id VARCHAR2(56 CHAR) NOT NULL,
    commit_time_ms NUMBER(19,0),
    PRIMARY KEY (id),
    CONSTRAINT fk_alf_txn_svr FOREIGN KEY (server_id) REFERENCES alf_server (id)
);
CREATE INDEX idx_alf_txn_ctms ON alf_transaction (commit_time_ms, id);
CREATE INDEX fk_alf_txn_svr ON alf_transaction (server_id);

CREATE SEQUENCE alf_store_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_store
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    protocol VARCHAR2(50 CHAR) NOT NULL,
    identifier VARCHAR2(100 CHAR) NOT NULL,
    root_node_id NUMBER(19,0),
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX protocol ON alf_store (protocol, identifier);

CREATE SEQUENCE alf_node_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_node
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    store_id NUMBER(19,0) NOT NULL,
    uuid VARCHAR2(36 CHAR) NOT NULL,
    transaction_id NUMBER(19,0) NOT NULL,
    type_qname_id NUMBER(19,0) NOT NULL,
    locale_id NUMBER(19,0) NOT NULL,
    acl_id NUMBER(19,0),
    audit_creator VARCHAR2(255 CHAR),
    audit_created VARCHAR2(30 CHAR),
    audit_modifier VARCHAR2(255 CHAR),
    audit_modified VARCHAR2(30 CHAR),
    audit_accessed VARCHAR2(30 CHAR),
    PRIMARY KEY (id),
    CONSTRAINT fk_alf_node_acl FOREIGN KEY (acl_id) REFERENCES alf_access_control_list (id),
    CONSTRAINT fk_alf_node_store FOREIGN KEY (store_id) REFERENCES alf_store (id),
    CONSTRAINT fk_alf_node_tqn FOREIGN KEY (type_qname_id) REFERENCES alf_qname (id),
    CONSTRAINT fk_alf_node_txn FOREIGN KEY (transaction_id) REFERENCES alf_transaction (id),
    CONSTRAINT fk_alf_node_loc FOREIGN KEY (locale_id) REFERENCES alf_locale (id)
);
CREATE UNIQUE INDEX store_id ON alf_node (store_id, uuid);
CREATE INDEX idx_alf_node_mdq ON alf_node (store_id, type_qname_id, id);
CREATE INDEX idx_alf_node_cor ON alf_node (audit_creator, store_id, type_qname_id, id);
CREATE INDEX idx_alf_node_crd ON alf_node (audit_created, store_id, type_qname_id, id);
CREATE INDEX idx_alf_node_mor ON alf_node (audit_modifier, store_id, type_qname_id, id);
CREATE INDEX idx_alf_node_mod ON alf_node (audit_modified, store_id, type_qname_id, id);
CREATE INDEX idx_alf_node_txn_type ON alf_node (transaction_id, type_qname_id);
CREATE INDEX fk_alf_node_acl ON alf_node (acl_id);
CREATE INDEX fk_alf_node_store ON alf_node (store_id);
CREATE INDEX idx_alf_node_tqn ON alf_node (type_qname_id, store_id, id);
CREATE INDEX fk_alf_node_loc ON alf_node (locale_id);

CREATE INDEX fk_alf_store_root ON alf_store (root_node_id);
ALTER TABLE alf_store ADD CONSTRAINT fk_alf_store_root FOREIGN KEY (root_node_id) REFERENCES alf_node (id);

CREATE SEQUENCE alf_child_assoc_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_child_assoc
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    parent_node_id NUMBER(19,0) NOT NULL,
    type_qname_id NUMBER(19,0) NOT NULL,
    child_node_name_crc NUMBER(19,0) NOT NULL,
    child_node_name VARCHAR2(50 CHAR) NOT NULL,
    child_node_id NUMBER(19,0) NOT NULL,
    qname_ns_id NUMBER(19,0) NOT NULL,
    qname_localname VARCHAR2(255 CHAR) NOT NULL,
    qname_crc NUMBER(19,0) NOT NULL,
    is_primary NUMBER(1,0),
    assoc_index NUMBER(10,0),
    PRIMARY KEY (id),
    CONSTRAINT fk_alf_cass_cnode FOREIGN KEY (child_node_id) REFERENCES alf_node (id),
    CONSTRAINT fk_alf_cass_pnode FOREIGN KEY (parent_node_id) REFERENCES alf_node (id),
    CONSTRAINT fk_alf_cass_qnns FOREIGN KEY (qname_ns_id) REFERENCES alf_namespace (id),
    CONSTRAINT fk_alf_cass_tqn FOREIGN KEY (type_qname_id) REFERENCES alf_qname (id)
);
CREATE UNIQUE INDEX parent_node_id ON alf_child_assoc (parent_node_id, type_qname_id, child_node_name_crc, child_node_name);
CREATE INDEX idx_alf_cass_pnode ON alf_child_assoc (parent_node_id, assoc_index, id);
CREATE INDEX fk_alf_cass_cnode ON alf_child_assoc (child_node_id);
CREATE INDEX fk_alf_cass_tqn ON alf_child_assoc (type_qname_id);
CREATE INDEX fk_alf_cass_qnns ON alf_child_assoc (qname_ns_id);
CREATE INDEX idx_alf_cass_qncrc ON alf_child_assoc (qname_crc, type_qname_id, parent_node_id);
CREATE INDEX idx_alf_cass_pri ON alf_child_assoc (parent_node_id, is_primary, child_node_id);

CREATE TABLE alf_node_aspects
(
    node_id NUMBER(19,0) NOT NULL,
    qname_id NUMBER(19,0) NOT NULL,
    PRIMARY KEY (node_id, qname_id),
	CONSTRAINT fk_alf_nasp_n FOREIGN KEY (node_id) REFERENCES alf_node (id),
    CONSTRAINT fk_alf_nasp_qn FOREIGN KEY (qname_id) REFERENCES alf_qname (id)
);
CREATE INDEX fk_alf_nasp_n ON alf_node_aspects (node_id);
CREATE INDEX fk_alf_nasp_qn ON alf_node_aspects (qname_id);

CREATE SEQUENCE alf_node_assoc_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_node_assoc
(
    id NUMBER(19,0) NOT NULL,
    version NUMBER(19,0) NOT NULL,
    source_node_id NUMBER(19,0) NOT NULL,
    target_node_id NUMBER(19,0) NOT NULL,
    type_qname_id NUMBER(19,0) NOT NULL,
    assoc_index NUMBER(10,0) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_alf_nass_snode FOREIGN KEY (source_node_id) REFERENCES alf_node (id),
    CONSTRAINT fk_alf_nass_tnode FOREIGN KEY (target_node_id) REFERENCES alf_node (id),
    CONSTRAINT fk_alf_nass_tqn FOREIGN KEY (type_qname_id) REFERENCES alf_qname (id)
);
CREATE UNIQUE INDEX source_node_id ON alf_node_assoc (source_node_id, target_node_id, type_qname_id);
CREATE INDEX fk_alf_nass_snode ON alf_node_assoc (source_node_id, type_qname_id, assoc_index);
CREATE INDEX fk_alf_nass_tnode ON alf_node_assoc (target_node_id, type_qname_id);
CREATE INDEX fk_alf_nass_tqn ON alf_node_assoc (type_qname_id);

CREATE TABLE alf_node_properties
(
    node_id NUMBER(19,0) NOT NULL,
    actual_type_n NUMBER(10,0) NOT NULL,
    persisted_type_n NUMBER(10,0) NOT NULL,
    boolean_value NUMBER(1,0),
    long_value NUMBER(19,0),
    float_value FLOAT,
    double_value DOUBLE PRECISION,
    string_value VARCHAR2(1024 CHAR),
    serializable_value BLOB,
    qname_id NUMBER(19,0) NOT NULL,
    list_index NUMBER(10,0) NOT NULL,
    locale_id NUMBER(19,0) NOT NULL,
    PRIMARY KEY (node_id, qname_id, list_index, locale_id),
    CONSTRAINT fk_alf_nprop_loc FOREIGN KEY (locale_id) REFERENCES alf_locale (id),
    CONSTRAINT fk_alf_nprop_n FOREIGN KEY (node_id) REFERENCES alf_node (id),
    CONSTRAINT fk_alf_nprop_qn FOREIGN KEY (qname_id) REFERENCES alf_qname (id)
);
CREATE INDEX fk_alf_nprop_n ON alf_node_properties (node_id);
CREATE INDEX fk_alf_nprop_qn ON alf_node_properties (qname_id);
CREATE INDEX fk_alf_nprop_loc ON alf_node_properties (locale_id);
CREATE INDEX idx_alf_nprop_s ON alf_node_properties (qname_id, string_value, node_id);
CREATE INDEX idx_alf_nprop_l ON alf_node_properties (qname_id, long_value, node_id);
