--
-- Title:      Activity tables
-- Database:   Oracle
-- Since:      V3.0 Schema 126
-- Author:     janv
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--

CREATE SEQUENCE alf_activity_feed_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_activity_feed
(
    id NUMBER(19,0) NOT NULL,
    post_id NUMBER(19,0),
    post_date TIMESTAMP NOT NULL,
    activity_summary VARCHAR2(1024 CHAR),
    feed_user_id VARCHAR2(255 CHAR),
    activity_type VARCHAR2(255 CHAR) NOT NULL,
    site_network VARCHAR2(255 CHAR),
    app_tool VARCHAR2(36 CHAR),
    post_user_id VARCHAR2(255 CHAR) NOT NULL,
    feed_date TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX feed_postdate_idx ON alf_activity_feed (post_date);
CREATE INDEX feed_postuserid_idx ON alf_activity_feed (post_user_id);
CREATE INDEX feed_feeduserid_idx ON alf_activity_feed (feed_user_id);
CREATE INDEX feed_sitenetwork_idx ON alf_activity_feed (site_network);

CREATE SEQUENCE alf_activity_feed_control_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_activity_feed_control
(
    id NUMBER(19,0) NOT NULL,
    feed_user_id VARCHAR2(255 CHAR) NOT NULL,
    site_network VARCHAR2(255 CHAR),
    app_tool VARCHAR2(36 CHAR),
    last_modified TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX feedctrl_feeduserid_idx ON alf_activity_feed_control (feed_user_id);

CREATE SEQUENCE alf_activity_post_seq START WITH 1 INCREMENT BY 1 ORDER;
CREATE TABLE alf_activity_post
(
    sequence_id NUMBER(19,0) NOT NULL,
    post_date TIMESTAMP NOT NULL,
    status VARCHAR2(10 CHAR) NOT NULL,
    activity_data VARCHAR2(1024 CHAR) NOT NULL,
    post_user_id VARCHAR2(255 CHAR) NOT NULL,
    job_task_node NUMBER(10,0) NOT NULL,
    site_network VARCHAR2(255 CHAR),
    app_tool VARCHAR2(36 CHAR),
    activity_type VARCHAR2(255 CHAR) NOT NULL,
    last_modified TIMESTAMP NOT NULL,
    PRIMARY KEY (sequence_id)
);
CREATE INDEX post_jobtasknode_idx ON alf_activity_post (job_task_node);
CREATE INDEX post_status_idx ON alf_activity_post (status);
