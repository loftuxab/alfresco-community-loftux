--
-- Title:      Create lock tables
-- Database:   Oracle
-- Since:      V3.2 Schema 2011
-- Author:     
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--

CREATE SEQUENCE alf_lock_resource_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_lock_resource
(
   id NUMBER(19,0) NOT NULL,
   version NUMBER(19,0) NOT NULL,
   qname_ns_id NUMBER(19,0) NOT NULL,
   qname_localname VARCHAR2(255 CHAR) NOT NULL,
   CONSTRAINT fk_alf_lockr_ns FOREIGN KEY (qname_ns_id) REFERENCES alf_namespace (id),
   PRIMARY KEY (id)   
);
CREATE UNIQUE INDEX idx_alf_lockr_key ON alf_lock_resource (qname_ns_id, qname_localname);

CREATE SEQUENCE alf_lock_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_lock
(
   id NUMBER(19,0) NOT NULL,
   version NUMBER(19,0) NOT NULL,
   shared_resource_id NUMBER(19,0) NOT NULL,
   excl_resource_id NUMBER(19,0) NOT NULL,
   lock_token VARCHAR2(36 CHAR) NOT NULL,
   start_time NUMBER(19,0) NOT NULL,
   expiry_time NUMBER(19,0) NOT NULL,
   CONSTRAINT fk_alf_lock_shared FOREIGN KEY (shared_resource_id) REFERENCES alf_lock_resource (id),
   CONSTRAINT fk_alf_lock_excl FOREIGN KEY (excl_resource_id) REFERENCES alf_lock_resource (id),
   PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_alf_lock_key ON alf_lock (shared_resource_id, excl_resource_id);
CREATE INDEX fk_alf_lock_excl ON alf_lock (excl_resource_id);
