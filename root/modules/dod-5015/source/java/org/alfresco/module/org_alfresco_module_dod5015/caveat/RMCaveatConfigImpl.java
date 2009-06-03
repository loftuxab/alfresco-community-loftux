/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_dod5015.caveat;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.JSONtoFmModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

/**
 * RM Caveat Config impl
 * 
 * @author janv
 */
public class RMCaveatConfigImpl implements ContentServicePolicies.OnContentUpdatePolicy,
                                           NodeServicePolicies.BeforeDeleteNodePolicy,
                                           NodeServicePolicies.OnCreateNodePolicy
{
    private static Log logger = LogFactory.getLog(RMCaveatConfigImpl.class);
    
    private PolicyComponent policyComponent;
    private ContentService contentService;
    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;
    private AuthorityService authorityService;
    private PersonService personService;
    private NodeService nodeService;
    
    // Default
    private StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    
    private static final String CAVEAT_CONFIG_NAME = "caveatConfig.json";
    
    private static final QName DATATYPE_TEXT = DataTypeDefinition.TEXT;
    
    // TODO - convert to SimpleCache to be cluster-aware (for dynamic changes to caveat config across a cluster)
    private Map<String, Map<String, List<String>>> caveatConfig = new ConcurrentHashMap<String, Map<String, List<String>>>(2);
    
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }
    
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
    
    public void setStoreRef(String storeRef)
    {
        this.storeRef = new StoreRef(storeRef);
    }
    
    
    /**
     * Initialise behaviours and caveat config cache
     */
    public void init()
    {
        // Register interest in the onContentUpdate policy
        policyComponent.bindClassBehaviour(
                ContentServicePolicies.ON_CONTENT_UPDATE,
                RecordsManagementModel.TYPE_CAVEAT_CONFIG,
                new JavaBehaviour(this, "onContentUpdate"));
        
        // Register interest in the beforeDeleteNode policy
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "beforeDeleteNode"),
                RecordsManagementModel.TYPE_CAVEAT_CONFIG,
                new JavaBehaviour(this, "beforeDeleteNode"));
        
        // Register interest in the onCreateNode policy
        policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                RecordsManagementModel.TYPE_CAVEAT_CONFIG,
                new JavaBehaviour(this, "onCreateNode"));
        
        NodeRef caveatConfigNodeRef = getCaveatConfig();
        if (caveatConfigNodeRef != null)
        {
            validateAndReset(caveatConfigNodeRef);
        }
    }
    
    public void onContentUpdate(NodeRef nodeRef, boolean newContent)
    {
        if (logger.isInfoEnabled())
        {
            logger.info("Caveat config: onContentUpdate: "+nodeRef+", "+newContent);
        }
        
        validateAndReset(nodeRef);
    }
    
    public void beforeDeleteNode(NodeRef nodeRef)
    {
        if (logger.isInfoEnabled())
        {
            logger.info("Caveat config: beforeDeleteNode: "+nodeRef);
        }
        
        validateAndReset(nodeRef);
    }
    
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        if (logger.isInfoEnabled())
        {
            logger.info("Caveat config: onCreateNode: "+childAssocRef);
        }
        
        validateAndReset(childAssocRef.getChildRef());
    }
    
    @SuppressWarnings("unchecked")
    protected void validateAndReset(NodeRef nodeRef)
    {
        ContentReader cr = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
        if (cr != null)
        {
            // TODO - check who can change caveat config !
            // TODO - locking (or checkout/checkin)
            
            String caveatConfigData = cr.getContentString();
            if (caveatConfigData != null)
            {
                NodeRef existing = getCaveatConfig();
                if ((existing != null && (! existing.equals(nodeRef))))
                {
                    throw new AlfrescoRuntimeException("Cannot create more than one caveat config (existing="+existing+", new="+nodeRef+")");
                }
                
                try
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug(caveatConfigData);
                    }
                    
                    Collection<QName> props = dictionaryService.getProperties(RecordsManagementModel.RM_MODEL, DATATYPE_TEXT);
                    
                    Map<String, Object> caveatConfigMap = JSONtoFmModel.convertJSONObjectToMap(caveatConfigData);
                    
                    for (Map.Entry<String, Object> conEntry : caveatConfigMap.entrySet())
                    {
                        String conStr = conEntry.getKey();
                        
                        QName conQName = QName.resolveToQName(namespaceService, conStr);
                        
                        if (! QName.splitPrefixedQName(conStr)[0].equals(RecordsManagementModel.RM_PREFIX))
                        {
                            throw new AlfrescoRuntimeException("Unexpected prefix: "+conQName.getPrefixString()+" (expected: "+RecordsManagementModel.RM_PREFIX+")");
                        }
                        
                        
                        Map<String, List<String>> caveatMap = (Map<String, List<String>>)conEntry.getValue();
                        
                        List<String> allowedValues = null;
                        boolean found = false;
                        
                        for (QName propertyName : props)
                        {
                            PropertyDefinition propDef = dictionaryService.getProperty(propertyName);
                            List<ConstraintDefinition> conDefs = propDef.getConstraints();
                            for (ConstraintDefinition conDef : conDefs)
                            {
                                final Constraint con = conDef.getConstraint();
                                if (con instanceof RMListOfValuesConstraint)
                                {
                                    String conName = ((RMListOfValuesConstraint)con).getShortName();
                                    if (conName.equals(conStr))
                                    {
                                        // note: assumes only one caveat/LOV against a given property
                                        allowedValues = AuthenticationUtil.runAs(new RunAsWork<List<String>>()
                                        {
                                            public List<String> doWork()
                                            {
                                                return ((RMListOfValuesConstraint)con).getAllowedValues();
                                            }
                                        }, AuthenticationUtil.getSystemUserName());
                                        
                                        found = true;
                                        break;
                                    }
                                }
                            }
                        }
                        
                        if (! found)
                        {
                            throw new AlfrescoRuntimeException("Constraint does not exist (or is not used) in RM model: "+conStr);
                        }
                        
                        if (allowedValues != null)
                        {
                            if (logger.isInfoEnabled())
                            {
                                logger.info("Processing constraint: "+conQName);
                            }
                            
                            for (Map.Entry<String, List<String>> caveatEntry : caveatMap.entrySet())
                            {
                                String authorityName = caveatEntry.getKey();
                                List<String> caveatList = caveatEntry.getValue();
                                
                                // validate authority (user or group)
                                if ((! authorityService.authorityExists(authorityService.getName(AuthorityType.GROUP, authorityName)) && ! personService.personExists(authorityName)))
                                {
                                    // TODO - review warnings (& I18N)
                                    String msg = "User/group does not exist: "+authorityName+" (constraint="+conStr+")";
                                    logger.warn(msg);
                                }
                                
                                // validate caveat list
                                for (String value : caveatList)
                                {
                                    if (! allowedValues.contains(value))
                                    {
                                        // TODO - review warnings (& add I18N)
                                        String msg = "Invalid value in list: "+value+" (authority="+authorityName+", constraint="+conStr+")";
                                        logger.warn(msg);
                                    }
                                }
                            }
                        }
                        else
                        {
                            // TODO - review warnings (& add I18N)
                            logger.warn("Constraint does not have LOV: "+conQName);
                        }
                    }
                    
                    // Valid, so update
                    caveatConfig.clear();
                    
                    for (Map.Entry<String, Object> conEntry : caveatConfigMap.entrySet())
                    {
                        String conStr = conEntry.getKey();
                        Map<String, List<String>> caveatMap = (Map<String, List<String>>)conEntry.getValue();
                        
                        caveatConfig.put(conStr, caveatMap);
                    }
                }
                catch (JSONException e)
                {
                    throw new AlfrescoRuntimeException("Invalid caveat config syntax: "+e);
                }
            }
        }
    }
    
    public NodeRef getCaveatConfig()
    {
        NodeRef rootNode = nodeService.getRootNode(storeRef);
        return nodeService.getChildByName(rootNode, RecordsManagementModel.ASSOC_CAVEAT_CONFIG, CAVEAT_CONFIG_NAME);
    }
    
    public NodeRef updateOrCreateCaveatConfig(File file)
    {
        NodeRef caveatConfig = getCaveatConfig();
        
        if (caveatConfig == null)
        {
            NodeRef rootNode = nodeService.getRootNode(storeRef);
            nodeService.addAspect(rootNode, VersionModel.ASPECT_VERSION_STORE_ROOT, null);
            
            // Create caveat config
            caveatConfig = nodeService.createNode(rootNode,
                                                  RecordsManagementModel.ASSOC_CAVEAT_CONFIG,
                                                  QName.createQName(RecordsManagementModel.RM_URI, CAVEAT_CONFIG_NAME),
                                                  RecordsManagementModel.TYPE_CAVEAT_CONFIG).getChildRef();
            
            nodeService.setProperty(caveatConfig, ContentModel.PROP_NAME, CAVEAT_CONFIG_NAME);
        }
        // Update the content
        ContentWriter writer = this.contentService.getWriter(caveatConfig, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent(file);
        
        return caveatConfig;
    }
    
    // Get list of all caveats name
    public Set<String> getRMConstraintNames()
    {
        return caveatConfig.keySet();
    }
    
    // Get allowed values for given caveat (for current user)
    public List<String> getRMAllowedValues(String constraintName)
    {
        List<String> allowedValues = new ArrayList<String>(0);
        
        String userName = AuthenticationUtil.getRunAsUser();
        if (userName != null)
        {
            Set<String> userGroupNames = authorityService.getAuthoritiesForUser(userName);
            allowedValues = getRMAllowedValues(userName, userGroupNames, constraintName);
        }
        
        return allowedValues;
    }
    
    private List<String> getRMAllowedValues(String userName, Set<String> userGroupNames, String constraintName)
    {
        // note: userName and userGroupNames must not be null
        List<String> allowedValues = new ArrayList<String>(5);
        
        Map<String, List<String>> caveatConstraintDef = caveatConfig.get(constraintName);
        
        if (caveatConstraintDef != null)
        {
            for (Map.Entry<String, List<String>> entry : caveatConstraintDef.entrySet())
            {
                String authorityName = entry.getKey();
                if (userName.equals(authorityName) || userGroupNames.contains(authorityName))
                {
                    // union of allowed values
                    allowedValues.addAll(entry.getValue());
                }
            }
        }
        
        return allowedValues;
    }
    
    /**
     * Check whether access to 'record component' node is vetoed for current user due to caveat(s)
     * 
     * @param nodeRef
     * @return false, if caveat(s) veto access otherwise return true
     */
    public boolean hasAccess(NodeRef nodeRef)
    {
        if (! nodeService.hasAspect(nodeRef, RecordsManagementModel.ASPECT_RECORD_COMPONENT_ID))
        {
            return true;
        }
        else
        {
            // is a Record Component - ie. Record Series, Record Category, Record Folder or Record
            
            String userName = AuthenticationUtil.getRunAsUser();
            if (userName != null)
            {
                Set<String> userGroupNames = authorityService.getAuthoritiesForUser(userName);
                
                // check all text properties
                Map<QName, Serializable> props = nodeService.getProperties(nodeRef);
                for (Map.Entry<QName, Serializable> entry : props.entrySet())
                {
                    QName propName = entry.getKey();
                    PropertyDefinition propDef = dictionaryService.getProperty(propName);
                    
                    if ((propDef != null) && (propDef.getDataType().getName().equals(DATATYPE_TEXT)))
                    {
                        List<ConstraintDefinition> conDefs = propDef.getConstraints();
                        for (ConstraintDefinition conDef : conDefs)
                        {
                            Constraint con = conDef.getConstraint();
                            if (con instanceof RMListOfValuesConstraint)
                            {
                                String conName = ((RMListOfValuesConstraint)con).getShortName();
                                if (! caveatConfig.containsKey(conName))
                                {
                                    continue;
                                }
                                else
                                {
                                    List<String> allowedValues = getRMAllowedValues(userName, userGroupNames, conName);
                                    
                                    @SuppressWarnings("unchecked")
                                    List<String> propValues = (List<String>)entry.getValue();
                                    
                                    if (! isAllowed(propValues, allowedValues))
                                    {
                                        if (logger.isDebugEnabled())
                                        {
                                            logger.debug("Veto access: caveat="+conName+", userName="+userName+", nodeRef="+nodeRef+", propName="+propName+", propValues="+propValues+", allowedValues="+allowedValues);
                                        }
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            return true;
        }
    }
    
    private boolean isAllowed(List<String> propValues, List<String> userGroupValues)
    {
        // check user/group values match all those on record
        for (String propValue : propValues)
        {
            if (! userGroupValues.contains(propValue))
            {
                return false;
            }
        }
        
        return true;
    }
}
