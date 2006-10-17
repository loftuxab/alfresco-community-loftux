-- ------------------------------------------------------
-- Alfresco Schema conversion cleanup V1.2.1 to V1.3
-- This will clear the database of all the old data
-- Execute only once the system is running correctly on
-- the converted schema and has been backed up.
-- 
-- Author: Derek Hulley
-- ------------------------------------------------------

--
-- Delete intermediate tables
--

DROP TABLE T_access_control_entry;
DROP TABLE T_access_control_list;
DROP TABLE T_auth_ext_keys;
DROP TABLE T_authority;
DROP TABLE T_child_assoc;
DROP TABLE T_node;
DROP TABLE T_node_aspects;
DROP TABLE T_node_assoc;
DROP TABLE T_node_status;
DROP TABLE T_permission;
DROP TABLE T_store;
DROP TABLE T_version_count;
