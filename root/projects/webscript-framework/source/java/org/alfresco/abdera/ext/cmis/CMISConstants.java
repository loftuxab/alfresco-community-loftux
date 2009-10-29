/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.abdera.ext.cmis;

import javax.xml.namespace.QName;


/**
 * CMIS Version: 0.62
 * 
 * CMIS Namespace and Schema definitions for the Abdera ATOM library.
 * 
 * @author davidc
 */
public interface CMISConstants
{
    // Namespace
    public static final String CMIS_NS = "http://docs.oasis-open.org/ns/cmis/core/200901";
    public static final String CMISRA_NS = "http://docs.oasis-open.org/ns/cmis/restatom/200901";
    public static final String CMISLINK_NS = "http://docs.oasis-open.org/ns/cmis/link/200901";
    
    // Mimetypes
    public static final String MIMETYPE_ENTRY = "application/atom+xml;type=entry";
    public static final String MIMETYPE_FEED = "application/atom+xml;type=feed";
    public static final String MIMETYPE_CMIS_QUERY = "application/cmisquery+xml";
    public static final String MIMETYPE_CMIS_ALLOWABLE_ACTIONS = "application/cmisallowableactions+xml";
    public static final String MIMETYPE_CMISATOM = "application/cmisatom+xml";
    public static final String MIMETYPE_CMISTREE = "application/cmistree+xml";
    
    // CMIS Service Document
    public static final QName COLLECTION_TYPE = new QName(CMISRA_NS, "collectionType");
    public static final String COLLECTION_ROOT = "root";
    public static final String COLLECTION_CHECKEDOUT = "checkedout";
    public static final String COLLECTION_UNFILED = "unfiled";
    public static final String COLLECTION_TYPES = "types";
    public static final String COLLECTION_QUERY = "query";
    
    // CMIS Repository Info
    public static final QName REPOSITORY_INFO = new QName(CMISRA_NS, "repositoryInfo");
    public static final QName REPOSITORY_ID = new QName(CMIS_NS, "repositoryId");
    public static final QName REPOSITORY_NAME = new QName(CMIS_NS, "repositoryName");
    public static final QName REPOSITORY_RELATIONSHIP = new QName(CMIS_NS, "repositoryRelationship");
    public static final QName REPOSITORY_DESCRIPTION = new QName(CMIS_NS, "repositoryDescription");
    public static final QName VENDOR_NAME = new QName(CMIS_NS, "vendorName");
    public static final QName PRODUCT_NAME = new QName(CMIS_NS, "productName");
    public static final QName PRODUCT_VERSION = new QName(CMIS_NS, "productVersion");
    public static final QName ROOT_FOLDER_ID = new QName(CMIS_NS, "rootFolderId");
    public static final QName VERSION_SUPPORTED = new QName(CMIS_NS, "cmisVersionSupported");
    
    // CMIS Capabilities
    public static final QName CAPABILITIES = new QName(CMIS_NS, "capabilities");
    public static final QName CAPABILITY_MULTIFILING = new QName(CMIS_NS, "capabilityMultifiling");
    public static final QName CAPABILITY_UNFILING = new QName(CMIS_NS, "capabilityUnfiling");
    public static final QName CAPABILITY_VERSION_SPECIFIC_FILING = new QName(CMIS_NS, "capabilityVersionSpecificFiling");
    public static final QName CAPABILITY_PWC_UPDATEABLE = new QName(CMIS_NS, "capabilityPWCUpdateable");
    public static final QName CAPABILITY_PWC_SEARCHABLE = new QName(CMIS_NS, "capabilityPWCSearchable");
    public static final QName CAPABILITY_ALL_VERIONS_SEARCHABLE = new QName(CMIS_NS, "capabilityAllVersionsSearchable");
    public static final QName CAPABILITY_QUERY = new QName(CMIS_NS, "capabilityQuery");
    public static final QName CAPABILITY_JOIN = new QName(CMIS_NS, "capabilityJoin");
    
    // CMIS Object
    public static final QName OBJECT = new QName(CMISRA_NS, "object");
    public static final QName PROPERTIES = new QName(CMIS_NS, "properties");
    public static final QName PROPERTY_ID = new QName("pdid");
    public static final QName PROPERTY_DISPLAY_NAME = new QName("displayName");
    public static final QName PROPERTY_VALUE = new QName(CMIS_NS, "value");
    public static final QName STRING_PROPERTY = new QName(CMIS_NS, "propertyString");
    public static final QName DECIMAL_PROPERTY = new QName(CMIS_NS, "propertyDecimal");
    public static final QName INTEGER_PROPERTY = new QName(CMIS_NS, "propertyInteger");
    public static final QName BOOLEAN_PROPERTY = new QName(CMIS_NS, "propertyBoolean");
    public static final QName DATETIME_PROPERTY = new QName(CMIS_NS, "propertyDateTime");
    public static final QName URI_PROPERTY = new QName(CMIS_NS, "propertyUri");
    public static final QName ID_PROPERTY = new QName(CMIS_NS, "propertyId");
    public static final QName XML_PROPERTY = new QName(CMIS_NS, "propertyXml");
    public static final QName HTML_PROPERTY = new QName(CMIS_NS, "propertyHtml");

    // CMIS Data Types
    public static final String DATATYPE_STRING = "string";
    public static final String DATATYPE_DECIMAL = "decimal";
    public static final String DATATYPE_INTEGER = "integer";
    public static final String DATATYPE_BOOLEAN = "boolean";
    public static final String DATATYPE_DATETIME = "datetime";
    public static final String DATATYPE_URI = "uri";
    public static final String DATATYPE_ID = "id";
    public static final String DATATYPE_XML = "xml";
    public static final String DATATYPE_HTML = "html";
    
    // CMIS Relationships
    public static final String REL_SERVICE = "repository";
    public static final String REL_UP = "up";
    public static final String REL_DOWN = "down";
    public static final String REL_DESCRIBED_BY = "describedby";
    public static final String REL_VERSION_HISTORY = "all-versions";
    public static final String REL_CURRENT_VERSION = "current-version";
    public static final String REL_WORKING_COPY = "working-copy";
    public static final String REL_ROOT_DESCENDANTS = CMISLINK_NS + "/rootdescendants";
    public static final String REL_TYPES_DESCENDANTS = CMISLINK_NS + "/typesdescendants";
    public static final String REL_FOLDER_TREE = CMISLINK_NS + "/foldertree";
    public static final String REL_ALLOWABLE_ACTIONS = CMISLINK_NS + "/allowableactions";
    public static final String REL_POLICIES = CMISLINK_NS + "/policies";
    public static final String REL_RELATIONSHIPS = CMISLINK_NS + "/relationships";
    public static final String REL_ASSOC_SOURCE = CMISLINK_NS + "/source";
    public static final String REL_ASSOC_TARGET = CMISLINK_NS + "/target";
    
    // CMIS Nested feed
    public static final QName CHILDREN = new QName(CMISRA_NS, "children");
    
    // CMIS Property Types
    public static final String PROP_TYPE_STRING = "string";
    public static final String PROP_TYPE_DECIMAL = "decimal";
    public static final String PROP_TYPE_INTEGER = "integer";
    public static final String PROP_TYPE_BOOLEAN = "boolean";
    public static final String PROP_TYPE_DATETIME = "datetime";
    public static final String PROP_TYPE_URI = "uri";
    public static final String PROP_TYPE_ID = "id";
    public static final String PROP_TYPE_XML = "xml";
    public static final String PROP_TYPE_HTML = "html";
    
    // CMIS Allowable Actions
    public static final QName ALLOWABLE_ACTIONS = new QName(CMIS_NS, "allowableActions");
    public static final QName CAN_DELETE = new QName(CMIS_NS, "canDelete");
    public static final QName CAN_UPDATE_PROPERTIES = new QName(CMIS_NS, "canUpdateProperties"); 
    public static final QName CAN_GET_PROPERTIES = new QName(CMIS_NS, "canGetProperties"); 
    public static final QName CAN_GET_RELATIONSHIPS = new QName(CMIS_NS, "canGetRelationships"); 
    public static final QName CAN_GET_PARENTS = new QName(CMIS_NS, "canGetParents"); 
    public static final QName CAN_GET_FOLDER_PARENT = new QName(CMIS_NS, "canGetFolderParent"); 
    public static final QName CAN_GET_DESCENDANTS = new QName(CMIS_NS, "canGetDescendants"); 
    public static final QName CAN_MOVE = new QName(CMIS_NS, "canMove"); 
    public static final QName CAN_DELETE_VERSION = new QName(CMIS_NS, "canDeleteVersion"); 
    public static final QName CAN_DELETE_CONTENT = new QName(CMIS_NS, "canDeleteContent"); 
    public static final QName CAN_CHECKOUT = new QName(CMIS_NS, "canCheckout"); 
    public static final QName CAN_CANCEL_CHECKOUT = new QName(CMIS_NS, "canCancelCheckout"); 
    public static final QName CAN_CHECKIN = new QName(CMIS_NS, "canCheckin"); 
    public static final QName CAN_SET_CONTENT = new QName(CMIS_NS, "canSetContent"); 
    public static final QName CAN_GET_ALL_VERSIONS = new QName(CMIS_NS, "canGetAllVersions"); 
    public static final QName CAN_ADD_TO_FOLDER = new QName(CMIS_NS, "canAddToFolder"); 
    public static final QName CAN_REMOVE_FROM_FOLDER = new QName(CMIS_NS, "canRemoveFromFolder"); 
    public static final QName CAN_VIEW_CONTENT = new QName(CMIS_NS, "canViewContent"); 
    public static final QName CAN_ADD_POLICY = new QName(CMIS_NS, "canAddPolicy"); 
    public static final QName CAN_GET_APPLIED_POLICIES = new QName(CMIS_NS, "canGetAppliedPolicies"); 
    public static final QName CAN_REMOVE_POLICY = new QName(CMIS_NS, "canRemovePolicy"); 
    public static final QName CAN_GET_CHILDREN = new QName(CMIS_NS, "canGetChildren"); 
    public static final QName CAN_CREATE_DOCUMENT = new QName(CMIS_NS, "canCreateDocument"); 
    public static final QName CAN_CREATE_FOLDER = new QName(CMIS_NS, "canCreateFolder"); 
    public static final QName CAN_CREATE_RELATIONSHIP = new QName(CMIS_NS, "canCreateRelationship"); 
    public static final QName CAN_CREATE_POLICY = new QName(CMIS_NS, "canCreatePolicy"); 
    public static final QName CAN_DELETE_TREE = new QName(CMIS_NS, "canDeleteTree");

    // CMIS Type Names
    public static final String TYPE_DOCUMENT = "cmis:document";
    public static final String TYPE_FOLDER = "cmis:folder";
    public static final String TYPE_RELATIONSHIP = "cmis:relationship";
    public static final String TYPE_POLICY = "cmis:policy";
    
    // CMIS Properties Names
    public static final String PROP_NAME = "cmis:Name";
    public static final String PROP_OBJECT_ID  = "cmis:ObjectId";
    public static final String PROP_BASE_TYPE_ID = "cmis:BaseTypeId";
    public static final String PROP_OBJECT_TYPE_ID = "cmis:ObjectTypeId";
    public static final String PROP_CREATED_BY = "cmis:CreatedBy";
    public static final String PROP_CREATION_DATE = "cmis:CreationDate";
    public static final String PROP_LAST_MODIFIED_BY = "cmis:LastModifiedBy";
    public static final String PROP_LAST_MODIFICATION_DATE = "cmis:LastModificationDate";
    public static final String PROP_IS_IMMUTABLE = "cmis:IsImmutable";
    public static final String PROP_IS_LATEST_VERSION = "cmis:IsLatestVersion";
    public static final String PROP_IS_MAJOR_VERSION = "cmis:IsMajorVersion";
    public static final String PROP_IS_LATEST_MAJOR_VERSION = "cmis:IsLatestMajorVersion";
    public static final String PROP_VERSION_LABEL = "cmis:VersionLabel";
    public static final String PROP_VERSION_SERIES_ID = "cmis:VersionSeriesId";
    public static final String PROP_IS_VERSION_SERIES_CHECKED_OUT = "cmis:IsVersionSeriesCheckedOut";
    public static final String PROP_VERSION_SERIES_CHECKED_OUT_BY = "cmis:VersionSeriesCheckedOutBy";
    public static final String PROP_VERSION_SERIES_CHECKED_OUT_ID = "cmis:VersionSeriesCheckedOutId";
    public static final String PROP_CHECKIN_COMMENT = "cmis:CheckinComment";
    public static final String PROP_CONTENT_STREAM_LENGTH = "cmis:ContentStreamLength";
    public static final String PROP_CONTENT_STREAM_MIMETYPE = "cmis:ContentStreamMimetype";
    public static final String PROP_CONTENT_STREAM_FILENAME = "cmis:ContentStreamFilename";
    public static final String PROP_CONTENT_STREAM_ID = "cmis:ContentStreamId";
    public static final String PROP_PATH = "cmis:PathName";
    public static final String PROP_SOURCE_ID = "cmis:SourceId";
    public static final String PROP_TARGET_ID = "cmis:TargetId";
    public static final String PROP_PARENT_ID = "cmis:ParentId";
    
    
}
