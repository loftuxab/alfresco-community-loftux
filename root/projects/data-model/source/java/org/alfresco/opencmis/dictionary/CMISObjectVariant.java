package org.alfresco.opencmis.dictionary;

public enum CMISObjectVariant
{
    INVALID_ID, // not a valid object id
    NOT_EXISTING, // valid id but object doesn't exist
    NOT_A_CMIS_OBJECT, // object is not mapped to CMIS
    FOLDER, // object is a folder
    ITEM, // object is an item
    CURRENT_VERSION, // object is a document (current version)
    VERSION, // object is a version (not updatable)
    PWC, // object is a PWC
    ASSOC, // object is a relationship
    PERMISSION_DENIED
    // user has no permissions
}
