package org.alfresco.opencmis.dictionary;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public interface CMISNodeInfo
{
    String getObjectId();

    CMISObjectVariant getObjectVariant();

    boolean isVariant(CMISObjectVariant var);

    NodeRef getNodeRef();

    String getCurrentNodeId();

    NodeRef getCurrentNodeNodeRef();

    String getCurrentObjectId();

    boolean isCurrentVersion();

    boolean isPWC();

    boolean hasPWC();

    boolean isVersion();

    boolean isLatestVersion();

    boolean isLatestMajorVersion();

    boolean isMajorVersion();

    String getVersionLabel();

    String getCheckinComment();

    AssociationRef getAssociationRef();

    TypeDefinitionWrapper getType();

    boolean isFolder();

    boolean isRootFolder();

    boolean isDocument();

    boolean isRelationship();
    
    boolean isItem();

    String getName();

    String getPath();

    Serializable getCreationDate();

    Serializable getModificationDate();

    Serializable getPropertyValue(String id);

    boolean containsPropertyValue(String id);

    void putPropertyValue(String id, Serializable value);

    List<CMISNodeInfo> getParents();

    Map<QName, Serializable> getNodeProps();

    Set<QName> getNodeAspects();
}