--
-- Title:      Subscription tables
-- Database:   MS SQL
-- Since:      V4.0 Schema 5011
-- Author:     Florian Mueller
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--

-- Note that the foreign keys do not have ON DELETE CASCADE as this is handled in
-- the code for MS SQL specifically.

CREATE TABLE alf_subscriptions
(
  user_node_id NUMERIC(19,0) NOT NULL,
  node_id NUMERIC(19,0) NOT NULL,
  PRIMARY KEY (user_node_id, node_id),
  CONSTRAINT fk_alf_sub_user FOREIGN KEY (user_node_id) REFERENCES alf_node(id),
  CONSTRAINT fk_alf_sub_node FOREIGN KEY (node_id) REFERENCES alf_node(id)
);
CREATE INDEX fk_alf_sub_node ON alf_subscriptions (node_id);
