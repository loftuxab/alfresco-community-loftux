--
-- Title:      Audit tables
-- Database:   MS SQL
-- Since:      V3.2 Schema 3002
-- Author:     Pavel Yurkevich
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--

CREATE TABLE alf_audit_model
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   content_data_id NUMERIC(19,0) NOT NULL,
   content_crc NUMERIC(19,0) NOT NULL,   
   CONSTRAINT fk_alf_aud_mod_cd FOREIGN KEY (content_data_id) REFERENCES alf_content_data (id),
   PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_alf_aud_mod_cr ON alf_audit_model (content_crc);
CREATE INDEX fk_alf_aud_mod_cd ON alf_audit_model(content_data_id);

CREATE TABLE alf_audit_app
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   version INT NOT NULL,
   app_name_id NUMERIC(19,0) NOT NULL,
   audit_model_id NUMERIC(19,0) NOT NULL,
   disabled_paths_id NUMERIC(19,0) NOT NULL,
   CONSTRAINT fk_alf_aud_app_an FOREIGN KEY (app_name_id) REFERENCES alf_prop_value (id),
   CONSTRAINT idx_alf_aud_app_an UNIQUE (app_name_id),
   CONSTRAINT fk_alf_aud_app_mod FOREIGN KEY (audit_model_id) REFERENCES alf_audit_model (id) ON DELETE CASCADE,
   CONSTRAINT fk_alf_aud_app_dis FOREIGN KEY (disabled_paths_id) REFERENCES alf_prop_root (id),
   PRIMARY KEY (id)
);
CREATE INDEX fk_alf_aud_app_mod ON alf_audit_app(audit_model_id);
CREATE INDEX fk_alf_aud_app_dis ON alf_audit_app(disabled_paths_id);

CREATE TABLE alf_audit_entry
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   audit_app_id NUMERIC(19,0) NOT NULL,
   audit_time NUMERIC(19,0) NOT NULL,
   audit_user_id NUMERIC(19,0) NULL,
   audit_values_id NUMERIC(19,0) NULL,
   CONSTRAINT fk_alf_aud_ent_app FOREIGN KEY (audit_app_id) REFERENCES alf_audit_app (id) ON DELETE CASCADE,
   CONSTRAINT fk_alf_aud_ent_use FOREIGN KEY (audit_user_id) REFERENCES alf_prop_value (id),
   CONSTRAINT fk_alf_aud_ent_pro FOREIGN KEY (audit_values_id) REFERENCES alf_prop_root (id),
   PRIMARY KEY (id)
);
CREATE INDEX idx_alf_aud_ent_tm ON alf_audit_entry (audit_time);
CREATE INDEX fk_alf_aud_ent_app ON alf_audit_entry(audit_app_id);
CREATE INDEX fk_alf_aud_ent_use ON alf_audit_entry(audit_user_id);
CREATE INDEX fk_alf_aud_ent_pro ON alf_audit_entry(audit_values_id);
