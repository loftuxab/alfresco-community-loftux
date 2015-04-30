--
-- Title:      Property Value tables
-- Database:   MS SQL
-- Since:      V3.2 Schema 3001
-- Author:     Pavel Yurkevich
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--

CREATE TABLE alf_prop_class
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   java_class_name NVARCHAR(255) NOT NULL,
   java_class_name_short NVARCHAR(32) NOT NULL,
   java_class_name_crc NUMERIC(19,0) NOT NULL,   
   PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_alf_propc_crc ON alf_prop_class (java_class_name_crc, java_class_name_short);
CREATE INDEX idx_alf_propc_clas ON alf_prop_class (java_class_name);

CREATE TABLE alf_prop_date_value
(
   date_value NUMERIC(19,0) NOT NULL,
   full_year INT NOT NULL,
   half_of_year TINYINT NOT NULL,
   quarter_of_year TINYINT NOT NULL,
   month_of_year TINYINT NOT NULL,
   week_of_year TINYINT NOT NULL,
   week_of_month TINYINT NOT NULL,
   day_of_year INT NOT NULL,
   day_of_month TINYINT NOT NULL,
   day_of_week TINYINT NOT NULL,   
   PRIMARY KEY (date_value)
);
CREATE INDEX idx_alf_propdt_dt ON alf_prop_date_value (full_year, month_of_year, day_of_month);

CREATE TABLE alf_prop_double_value
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   double_value DOUBLE PRECISION NOT NULL,   
   PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_alf_propd_val ON alf_prop_double_value (double_value);

-- Stores unique, case-sensitive string values --
CREATE TABLE alf_prop_string_value
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   string_value NVARCHAR(1024) NOT NULL,
   string_end_lower NVARCHAR(16) NOT NULL,
   string_crc NUMERIC(19,0) NOT NULL,   
   PRIMARY KEY (id)
);
--CREATE INDEX idx_alf_props_str ON alf_prop_string_value (string_value);
CREATE UNIQUE INDEX idx_alf_props_crc ON alf_prop_string_value (string_end_lower, string_crc);

CREATE TABLE alf_prop_serializable_value
(
   id BIGINT IDENTITY NOT NULL,
   serializable_value IMAGE NOT NULL,
   PRIMARY KEY (id)
);

CREATE TABLE alf_prop_value
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   actual_type_id NUMERIC(19,0) NOT NULL,
   persisted_type TINYINT NOT NULL,
   long_value NUMERIC(19,0) NOT NULL,   
   PRIMARY KEY (id)
);
CREATE INDEX idx_alf_propv_per ON alf_prop_value (persisted_type, long_value);
CREATE UNIQUE INDEX idx_alf_propv_act ON alf_prop_value (actual_type_id, long_value);

CREATE TABLE alf_prop_root
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   version INT NOT NULL,
   PRIMARY KEY (id)
);

CREATE TABLE alf_prop_link
(
   root_prop_id NUMERIC(19,0) NOT NULL,
   prop_index NUMERIC(19,0) NOT NULL,
   contained_in NUMERIC(19,0) NOT NULL,
   key_prop_id NUMERIC(19,0) NOT NULL,
   value_prop_id NUMERIC(19,0) NOT NULL,
   PRIMARY KEY (root_prop_id, contained_in, prop_index)
);
ALTER TABLE alf_prop_link ADD CONSTRAINT fk_alf_propln_root FOREIGN KEY (root_prop_id) REFERENCES alf_prop_root (id) ON DELETE CASCADE;
ALTER TABLE alf_prop_link ADD CONSTRAINT fk_alf_propln_key FOREIGN KEY (key_prop_id) REFERENCES alf_prop_value (id) ON DELETE CASCADE;
-- next statement is used without 'ON DELETE CASCADE' cause sql server not allow two constrains to the same column do that
ALTER TABLE alf_prop_link ADD CONSTRAINT fk_alf_propln_val FOREIGN KEY (value_prop_id) REFERENCES alf_prop_value (id);
CREATE INDEX fk_alf_propln_key ON alf_prop_link(key_prop_id);
CREATE INDEX fk_alf_propln_val ON alf_prop_link(value_prop_id);

CREATE INDEX idx_alf_propln_for ON alf_prop_link (root_prop_id, key_prop_id, value_prop_id);

CREATE TABLE alf_prop_unique_ctx
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   version INT NOT NULL,
   value1_prop_id NUMERIC(19,0) NOT NULL,
   value2_prop_id NUMERIC(19,0) NOT NULL,
   value3_prop_id NUMERIC(19,0) NOT NULL,   
   prop1_id NUMERIC(19,0),   
   PRIMARY KEY (id)
);
ALTER TABLE alf_prop_unique_ctx ADD CONSTRAINT fk_alf_propuctx_v1 FOREIGN KEY (value1_prop_id) REFERENCES alf_prop_value (id) ON DELETE CASCADE;
-- next statement is used without 'ON DELETE CASCADE' cause sql server not allow two constrains to the same column do that
ALTER TABLE alf_prop_unique_ctx ADD CONSTRAINT fk_alf_propuctx_v2 FOREIGN KEY (value2_prop_id) REFERENCES alf_prop_value (id);
ALTER TABLE alf_prop_unique_ctx ADD CONSTRAINT fk_alf_propuctx_v3 FOREIGN KEY (value3_prop_id) REFERENCES alf_prop_value (id);
ALTER TABLE alf_prop_unique_ctx ADD CONSTRAINT fk_alf_propuctx_p1 FOREIGN KEY (prop1_id) REFERENCES alf_prop_root (id);

CREATE UNIQUE INDEX idx_alf_propuctx ON alf_prop_unique_ctx (value1_prop_id, value2_prop_id, value3_prop_id);
CREATE INDEX fk_alf_propuctx_v2 ON alf_prop_unique_ctx(value2_prop_id);
CREATE INDEX fk_alf_propuctx_v3 ON alf_prop_unique_ctx(value3_prop_id);
CREATE INDEX fk_alf_propuctx_p1 ON alf_prop_unique_ctx(prop1_id);
