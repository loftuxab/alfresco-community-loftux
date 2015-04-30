--
-- Title:      Create Content tables
-- Database:   MS SQL
-- Since:      V3.2 Schema 2012
-- Author:     
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--

CREATE TABLE alf_mimetype
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   version NUMERIC(19,0) NOT NULL,
   mimetype_str NVARCHAR(100) NOT NULL,
   PRIMARY KEY (id),
   UNIQUE (mimetype_str)
);

CREATE TABLE alf_encoding
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   version NUMERIC(19,0) NOT NULL,
   encoding_str NVARCHAR(100) NOT NULL,
   PRIMARY KEY (id),
   UNIQUE (encoding_str)
);

-- This table may exist during upgrades, but must be removed.
-- The drop statement is therefore optional.
DROP TABLE alf_content_url;                     --(optional)

CREATE TABLE alf_content_url
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   content_url NVARCHAR(255) NOT NULL,
   content_url_short NVARCHAR(12) NOT NULL,
   content_url_crc NUMERIC(19,0) NOT NULL,
   content_size NUMERIC(19,0) NOT NULL,
   orphan_time NUMERIC(19,0) NULL,
   PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_alf_conturl_cr ON alf_content_url (content_url_short, content_url_crc);
CREATE INDEX idx_alf_conturl_ot ON alf_content_url (orphan_time);
CREATE INDEX idx_alf_conturl_sz ON alf_content_url (content_size, id);

CREATE TABLE alf_content_data
(
   id NUMERIC(19,0) IDENTITY NOT NULL,
   version NUMERIC(19,0) NOT NULL,
   content_url_id NUMERIC(19,0) NULL,
   content_mimetype_id NUMERIC(19,0) NULL,
   content_encoding_id NUMERIC(19,0) NULL,
   content_locale_id NUMERIC(19,0) NULL,
   CONSTRAINT fk_alf_cont_url FOREIGN KEY (content_url_id) REFERENCES alf_content_url (id),
   CONSTRAINT fk_alf_cont_mim FOREIGN KEY (content_mimetype_id) REFERENCES alf_mimetype (id),
   CONSTRAINT fk_alf_cont_enc FOREIGN KEY (content_encoding_id) REFERENCES alf_encoding (id),
   CONSTRAINT fk_alf_cont_loc FOREIGN KEY (content_locale_id) REFERENCES alf_locale (id),
   PRIMARY KEY (id)
);
CREATE INDEX fk_alf_cont_url ON alf_content_data (content_url_id);
CREATE INDEX fk_alf_cont_mim ON alf_content_data (content_mimetype_id);
CREATE INDEX fk_alf_cont_enc ON alf_content_data (content_encoding_id);
CREATE INDEX fk_alf_cont_loc ON alf_content_data (content_locale_id);
