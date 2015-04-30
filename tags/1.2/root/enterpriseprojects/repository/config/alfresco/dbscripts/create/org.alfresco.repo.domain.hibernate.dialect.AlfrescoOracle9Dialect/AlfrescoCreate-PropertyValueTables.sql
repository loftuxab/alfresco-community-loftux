--
-- Title:      Property Value tables
-- Database:   Oracle
-- Since:      V3.2 Schema 3001
-- Author:     Pavel Yurkevich
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--

CREATE SEQUENCE alf_prop_class_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_prop_class
(
   id NUMBER(19,0) NOT NULL,
   java_class_name VARCHAR2(255 CHAR) NOT NULL,
   java_class_name_short VARCHAR2(32 CHAR) NOT NULL,
   java_class_name_crc NUMBER(19,0) NOT NULL,      
   PRIMARY KEY (id)
);
CREATE INDEX idx_alf_propc_clas ON alf_prop_class (java_class_name);
CREATE UNIQUE INDEX idx_alf_propc_crc ON alf_prop_class (java_class_name_crc, java_class_name_short);

CREATE SEQUENCE alf_prop_date_value_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_prop_date_value
(
   date_value NUMBER(19,0) NOT NULL,
   full_year NUMBER(5,0) NOT NULL,
   half_of_year NUMBER(3,0) NOT NULL,
   quarter_of_year NUMBER(3,0) NOT NULL,
   month_of_year NUMBER(3,0) NOT NULL,
   week_of_year NUMBER(3,0) NOT NULL,
   week_of_month NUMBER(3,0) NOT NULL,
   day_of_year NUMBER(5,0) NOT NULL,
   day_of_month NUMBER(3,0) NOT NULL,
   day_of_week NUMBER(3,0) NOT NULL,   
   PRIMARY KEY (date_value)
);
CREATE INDEX idx_alf_propdt_dt ON alf_prop_date_value (full_year, month_of_year, day_of_month);

CREATE SEQUENCE alf_prop_double_value_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_prop_double_value
(
   id NUMBER(19,0) NOT NULL,
   double_value DOUBLE PRECISION NOT NULL,   
   PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_alf_propd_val ON alf_prop_double_value (double_value);

-- Stores unique, case-sensitive string values --
CREATE SEQUENCE alf_prop_string_value_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_prop_string_value
(
   id NUMBER(19,0) NOT NULL,
   string_value VARCHAR2(1024 CHAR) NOT NULL,
   string_end_lower VARCHAR2(16 CHAR) NOT NULL,
   string_crc NUMBER(19,0) NOT NULL,
   PRIMARY KEY (id)
);
CREATE INDEX idx_alf_props_str ON alf_prop_string_value (string_value);
CREATE UNIQUE INDEX idx_alf_props_crc ON alf_prop_string_value (string_end_lower, string_crc);

CREATE SEQUENCE alf_prop_serial_value_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_prop_serializable_value
(
   id NUMBER(19,0) NOT NULL,
   serializable_value BLOB NOT NULL,
   PRIMARY KEY (id)
);

CREATE SEQUENCE alf_prop_value_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_prop_value
(
   id NUMBER(19,0) NOT NULL,
   actual_type_id NUMBER(19,0) NOT NULL,
   persisted_type NUMBER(3,0) NOT NULL,
   long_value NUMBER(19,0) NOT NULL,
   PRIMARY KEY (id)
);
CREATE INDEX idx_alf_propv_per ON alf_prop_value (persisted_type, long_value);
CREATE UNIQUE INDEX idx_alf_propv_act ON alf_prop_value (actual_type_id, long_value);

CREATE SEQUENCE alf_prop_root_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_prop_root
(
   id NUMBER(19,0) NOT NULL,
   version NUMBER(5,0) NOT NULL,
   PRIMARY KEY (id)
);

CREATE SEQUENCE alf_prop_link_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_prop_link
(
   root_prop_id NUMBER(19,0) NOT NULL,
   prop_index NUMBER(19,0) NOT NULL,
   contained_in NUMBER(19,0) NOT NULL,
   key_prop_id NUMBER(19,0) NOT NULL,
   value_prop_id NUMBER(19,0) NOT NULL,
   CONSTRAINT fk_alf_propln_root FOREIGN KEY (root_prop_id) REFERENCES alf_prop_root (id) ON DELETE CASCADE,
   CONSTRAINT fk_alf_propln_key FOREIGN KEY (key_prop_id) REFERENCES alf_prop_value (id) ON DELETE CASCADE,
   CONSTRAINT fk_alf_propln_val FOREIGN KEY (value_prop_id) REFERENCES alf_prop_value (id) ON DELETE CASCADE, 
   PRIMARY KEY (root_prop_id, contained_in, prop_index)
);
CREATE INDEX idx_alf_propln_for ON alf_prop_link (root_prop_id, key_prop_id, value_prop_id);
CREATE INDEX fk_alf_propln_key ON alf_prop_link(key_prop_id);
CREATE INDEX fk_alf_propln_val ON alf_prop_link(value_prop_id);

CREATE SEQUENCE alf_prop_unique_ctx_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_prop_unique_ctx
(
   id NUMBER(19,0) NOT NULL,
   version NUMBER(5,0) NOT NULL,
   value1_prop_id NUMBER(19,0) NOT NULL,
   value2_prop_id NUMBER(19,0) NOT NULL,
   value3_prop_id NUMBER(19,0) NOT NULL,
   prop1_id NUMBER(19,0) NULL,
   CONSTRAINT fk_alf_propuctx_v1 FOREIGN KEY (value1_prop_id) REFERENCES alf_prop_value (id) ON DELETE CASCADE,
   CONSTRAINT fk_alf_propuctx_v2 FOREIGN KEY (value2_prop_id) REFERENCES alf_prop_value (id) ON DELETE CASCADE,
   CONSTRAINT fk_alf_propuctx_v3 FOREIGN KEY (value3_prop_id) REFERENCES alf_prop_value (id) ON DELETE CASCADE,
   CONSTRAINT fk_alf_propuctx_p1 FOREIGN KEY (prop1_id) REFERENCES alf_prop_root (id),
   PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_alf_propuctx ON alf_prop_unique_ctx (value1_prop_id, value2_prop_id, value3_prop_id);
CREATE INDEX fk_alf_propuctx_v2 ON alf_prop_unique_ctx(value2_prop_id);
CREATE INDEX fk_alf_propuctx_v3 ON alf_prop_unique_ctx(value3_prop_id);
CREATE INDEX fk_alf_propuctx_p1 ON alf_prop_unique_ctx(prop1_id);
