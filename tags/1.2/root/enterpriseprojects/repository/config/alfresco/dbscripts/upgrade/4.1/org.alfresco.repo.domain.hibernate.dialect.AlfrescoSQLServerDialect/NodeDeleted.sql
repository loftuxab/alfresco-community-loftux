--
-- Title:      Update alf_txn table and alf_node indexes to support SOLR tracking
-- Database:   MSSQL
-- Since:      V4.1 Schema 5113
-- Author:     Derek Hulley
-- Author:     Dmitry Velichkevich
--
-- Please contact support@alfresco.com if you need assistance with the upgrade.
--



--ASSIGN:SYSTEM_NS_ID=id
select id from alf_namespace where uri = 'http://www.alfresco.org/model/system/1.0';


--ASSIGN:LAST_ID=id
select max(id) as id from alf_qname;                                                                                     -- (optional)


set IDENTITY_INSERT alf_qname on;                                                                                        -- (optional)
insert into alf_qname (id, version, ns_id, local_name) values ((${LAST_ID} + 1), 0, ${SYSTEM_NS_ID}, 'deleted');         -- (optional)
set IDENTITY_INSERT alf_qname off;                                                                                       -- (optional)


--ASSIGN:DELETED_TYPE_ID=id
select id from alf_qname where ns_id = ${SYSTEM_NS_ID} and local_name = 'deleted';


drop index idx_alf_node_txn_del on alf_node;
drop index store_id on alf_node;  -- (optional)
drop index fk_alf_node_acl on alf_node;
drop index fk_alf_node_txn on alf_node;
drop index fk_alf_node_store on alf_node;
drop index fk_alf_node_tqn on alf_node;
drop index fk_alf_node_loc on alf_node;
drop index fk_alf_store_root on alf_store;

alter table alf_node drop constraint fk_alf_node_acl;
alter table alf_node drop constraint fk_alf_node_store;
alter table alf_node drop constraint fk_alf_node_tqn;
alter table alf_node drop constraint fk_alf_node_txn;
alter table alf_node drop constraint fk_alf_node_loc;

alter table alf_child_assoc drop constraint fk_alf_cass_cnode;
alter table alf_child_assoc drop constraint fk_alf_cass_pnode;

alter table alf_node_assoc drop constraint fk_alf_nass_snode;
alter table alf_node_assoc drop constraint fk_alf_nass_tnode;

alter table alf_subscriptions drop constraint fk_alf_sub_user;
alter table alf_subscriptions drop constraint fk_alf_sub_node;

alter table alf_node_properties drop constraint fk_alf_nprop_n;

alter table alf_usage_delta drop constraint fk_alf_usaged_n;

alter table alf_node_aspects drop constraint fk_alf_nasp_n;

alter table alf_store drop constraint fk_alf_store_root;


EXEC sp_rename 'alf_node', 't_alf_node';


create table alf_node
(
    id NUMERIC(19,0) IDENTITY not NULL,
    version NUMERIC(19,0) not NULL,
    store_id NUMERIC(19,0) not NULL,
    uuid NVARCHAR(36) not NULL,
    transaction_id NUMERIC(19,0) not NULL,
    type_qname_id NUMERIC(19,0) not NULL,
    locale_id NUMERIC(19,0) not NULL,
    acl_id NUMERIC(19,0),
    audit_creator NVARCHAR(255),
    audit_created NVARCHAR(30),
    audit_modifier NVARCHAR(255),
    audit_modified NVARCHAR(30),
    audit_accessed NVARCHAR(30),
    primary key (id),
    constraint fk_alf_node_acl foreign key (acl_id) references alf_access_control_list(id),
    constraint fk_alf_node_store foreign key (store_id) references alf_store(id),
    constraint fk_alf_node_tqn foreign key (type_qname_id) references alf_qname(id),
    constraint fk_alf_node_txn foreign key (transaction_id) references alf_transaction(id),
    constraint fk_alf_node_loc foreign key (locale_id) references alf_locale(id)
);

create unique index store_id on alf_node(store_id, uuid);
create index idx_alf_node_txn_type on alf_node(transaction_id, type_qname_id);
create index fk_alf_node_acl on alf_node(acl_id);
create index fk_alf_node_store on alf_node(store_id);
create index fk_alf_node_tqn on alf_node(type_qname_id);
create index fk_alf_node_loc on alf_node(locale_id);


set IDENTITY_INSERT alf_node on;
--FOREACH t_alf_node.id system.upgrade.alf_node_deleted_type.batchsize
insert into alf_node
(id, version, store_id, uuid, transaction_id, type_qname_id, locale_id, acl_id, audit_creator, audit_created, audit_modifier, audit_modified, audit_accessed)
(
    select
       id, version, store_id, uuid, transaction_id, (case when 1 = node_deleted then ${DELETED_TYPE_ID} else type_qname_id end), locale_id, acl_id,
       audit_creator, audit_created, audit_modifier, audit_modified, audit_accessed
    from
       t_alf_node
    where
       id >= ${LOWERBOUND} AND id <= ${UPPERBOUND}
);
set IDENTITY_INSERT alf_node off;

drop table t_alf_node;


create index fk_alf_store_root on alf_store(root_node_id);

alter table alf_child_assoc add constraint fk_alf_cass_cnode foreign key (child_node_id) references alf_node(id);
alter table alf_child_assoc add constraint fk_alf_cass_pnode foreign key (parent_node_id) references alf_node(id);

alter table alf_node_assoc add constraint fk_alf_nass_snode foreign key (source_node_id) references alf_node(id);
alter table alf_node_assoc add constraint fk_alf_nass_tnode foreign key (target_node_id) references alf_node(id);

alter table alf_subscriptions add constraint fk_alf_sub_user foreign key (user_node_id) references alf_node(id);
alter table alf_subscriptions add constraint fk_alf_sub_node foreign key (node_id) references alf_node(id);

alter table alf_node_properties add constraint fk_alf_nprop_n foreign key (node_id) references alf_node(id);

alter table alf_usage_delta add constraint fk_alf_usaged_n foreign key (node_id) references alf_node(id);

alter table alf_node_aspects add constraint fk_alf_nasp_n foreign key (node_id) references alf_node(id);

alter table alf_store add constraint fk_alf_store_root foreign key (root_node_id) references alf_node(id);



--
-- Record script finish
--
DELETE FROM alf_applied_patch WHERE id = 'patch.db-V4.1-NodeDeleted';
INSERT INTO alf_applied_patch
  (id, description, fixes_from_schema, fixes_to_schema, applied_to_schema, target_schema, applied_on_date, applied_to_server, was_executed, succeeded, report)
  VALUES
  (
    'patch.db-V4.1-NodeDeleted', 'Manually executed script upgrade V4.1: Remove node_deleted',
    0, 6014, -1, 6015, null, 'UNKNOWN', ${TRUE}, ${TRUE}, 'Script completed'
  );