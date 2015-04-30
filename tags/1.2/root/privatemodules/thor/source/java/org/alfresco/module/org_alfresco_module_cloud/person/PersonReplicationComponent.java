/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.module.org_alfresco_module_cloud.accounts.Account;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.EqualsHelper;


/**
 * Support for replicating and synchronising People between Tenants
 * 
 * @since Thor
 */
public class PersonReplicationComponent
    implements OnUpdatePropertiesPolicy, OnCreateAssociationPolicy
{
    // List of properties to remove on creation of a new persion
    private final List<QName> CREATE_PROPS_FILTER;
    private final List<QName> UPDATE_PROPS_FILTER;
    
    // List of preferences to replicate
    private final List<String> PREFERENCES;
    
    private JavaBehaviour onUpdatePersonPropertiesBehaviour;
    private JavaBehaviour onCreateImageChildAssociationBehaviour;
    
    private PersonService personService;
    private NodeService nodeService;
    private ContentService contentService;
    private PreferenceService preferenceService;
    private RegistrationService registrationService;
    private PolicyComponent policyComponent;

    public void setPersonService(PersonService service)
    {
        this.personService = service;
    }
    
    public void setNodeService(NodeService service)
    {
        this.nodeService = service;
    }

    public void setContentService(ContentService service)
    {
        this.contentService = service;
    }

    public void setPreferenceService(PreferenceService service)
    {
        this.preferenceService = service;
    }

    public void setRegistrationService(RegistrationService service)
    {
        this.registrationService = service;
    }
    
    public void setPolicyComponent(PolicyComponent service)
    {
        this.policyComponent = service;
    }


    private PersonReplicationComponent()
    {
        CREATE_PROPS_FILTER = new ArrayList<QName>();
        CREATE_PROPS_FILTER.add(ContentModel.PROP_STORE_PROTOCOL);
        CREATE_PROPS_FILTER.add(ContentModel.PROP_STORE_IDENTIFIER);
        CREATE_PROPS_FILTER.add(ContentModel.PROP_NODE_UUID);
        CREATE_PROPS_FILTER.add(ContentModel.PROP_NODE_DBID);
        CREATE_PROPS_FILTER.add(ContentModel.PROP_HOMEFOLDER);
        CREATE_PROPS_FILTER.add(ContentModel.PROP_HOME_FOLDER_PROVIDER);
        CREATE_PROPS_FILTER.add(ContentModel.PROP_EMAIL_FEED_ID);
        CREATE_PROPS_FILTER.add(ContentModel.PROP_SIZE_CURRENT);
        CREATE_PROPS_FILTER.add(ContentModel.PROP_SIZE_QUOTA);
        CREATE_PROPS_FILTER.add(ContentModel.PROP_PREFERENCE_VALUES);
        
        UPDATE_PROPS_FILTER = new ArrayList<QName>();
        UPDATE_PROPS_FILTER.add(ContentModel.PROP_OWNER);
        UPDATE_PROPS_FILTER.add(ContentModel.PROP_HOMEFOLDER);
        UPDATE_PROPS_FILTER.add(ContentModel.PROP_HOME_FOLDER_PROVIDER);
        UPDATE_PROPS_FILTER.add(ContentModel.PROP_EMAIL_FEED_ID);
        UPDATE_PROPS_FILTER.add(ContentModel.PROP_SIZE_CURRENT);
        UPDATE_PROPS_FILTER.add(ContentModel.PROP_SIZE_QUOTA);
        UPDATE_PROPS_FILTER.add(ContentModel.PROP_PREFERENCE_VALUES);
        
        PREFERENCES = new ArrayList<String>();
        PREFERENCES.add("locale");
    }
    
    public void init()
    {
        onUpdatePersonPropertiesBehaviour = new JavaBehaviour(this, "onUpdateProperties");
        this.policyComponent.bindClassBehaviour(
                OnUpdatePropertiesPolicy.QNAME,
                ContentModel.TYPE_PERSON,
                onUpdatePersonPropertiesBehaviour);

        onCreateImageChildAssociationBehaviour = new JavaBehaviour(this, "onCreateAssociation");
        this.policyComponent.bindAssociationBehaviour(
                OnCreateAssociationPolicy.QNAME,
                ContentModel.TYPE_PERSON,
                ContentModel.ASSOC_AVATAR,
                onCreateImageChildAssociationBehaviour);
    }

    
    
    
    private PersonInfo createPersonInfo(NodeRef person, Map<QName, Serializable> props, Map<String, Serializable> preferences, ContentReader avatar)
    {
        PersonInfo personInfo = new PersonInfo();
        Map<QName, Serializable> dataProps = new HashMap<QName, Serializable>();
        Map<QName, ContentReader> contentProps = new HashMap<QName, ContentReader>();
        
        for (Map.Entry<QName, Serializable> prop : props.entrySet())
        {
            QName name = prop.getKey();
            Serializable value = prop.getValue();
            if (value instanceof ContentData)
            {
                ContentReader reader = readContent(person, name);
                contentProps.put(name, reader);
            }
            else
            {
                dataProps.put(name, value);
            }
        }
        
        personInfo.props = dataProps;
        personInfo.contentProps = contentProps;
        personInfo.preferences = preferences;
        personInfo.avatar = avatar;
        return personInfo;
    }
    
    /**
     * Copy a person from one tenant to another
     * 
     * @param email  email of person to copy
     * @param sourceTenantId  source tenant id
     * @param destTenantId  destination tenant id
     */
    public void copyPerson(final String email, final String sourceTenantId, final String destTenantId)
    {
        final PersonInfo personInfo = TenantUtil.runAsTenant(new TenantRunAsWork<PersonInfo>()
        {
            public PersonInfo doWork() throws Exception
            {
                NodeRef person = personService.getPerson(email);
                if (person == null)
                {
                    throw new AlfrescoRuntimeException("Person " + email + " not found in tenant " + sourceTenantId);
                }
                
                Map<QName, Serializable> props = nodeService.getProperties(person);
                Map<String, Serializable> preferences = readPreferences(email, props);
                removeProperties(props, CREATE_PROPS_FILTER);
                ContentReader avatar = readPersonAvatar(person);
                return createPersonInfo(person, props, preferences, avatar);
            }
        }, sourceTenantId);
        
        TenantUtil.runAsTenant(new TenantRunAsWork<NodeRef>()
        {
            public NodeRef doWork() throws Exception
            {
                try
                {
                    onCreateImageChildAssociationBehaviour.disable();
                    onUpdatePersonPropertiesBehaviour.disable();
                    
                    // create person with copy of original person properties (non-content)
                    personInfo.props.put(CloudModel.PROP_EXTERNAL_PERSON, sourceTenantId+":"+destTenantId); // mark as external person
                    NodeRef person = personService.createPerson(personInfo.props);

                    // copy over any content properties
                    writeContentProperties(person, personInfo.contentProps);
                    
                    // copy over any preferences
                    writePreferences(email, personInfo.preferences);

                    // copy over avatar
                    if (personInfo.avatar != null)
                    {
                        writePersonAvatar(person, personInfo.avatar);
                    }
                    
                    return null;
                }
                finally
                {
                    onUpdatePersonPropertiesBehaviour.enable();
                    onCreateImageChildAssociationBehaviour.enable();
                }
            }
        }, destTenantId);
    }

    private void writeContentProperties(NodeRef node, Map<QName, ContentReader> contentProps)
    {
        for (Map.Entry<QName, ContentReader> content : contentProps.entrySet())
        {
            writeContent(node, content.getKey(), content.getValue());
        }
    }

    private void writeContent(NodeRef node, QName contentProp, ContentReader content)
    {
        ContentWriter writer = contentService.getWriter(node, contentProp, true);
        writer.setEncoding(content.getEncoding());
        writer.setLocale(content.getLocale());
        writer.setMimetype(content.getMimetype());
        writer.putContent(content.getReader());
    }
    
    @Override
    public void onUpdateProperties(final NodeRef nodeRef, final Map<QName, Serializable> before, final Map<QName, Serializable> after)
    {
        // check user name can be found in person
        final String email = (String)before.get(ContentModel.PROP_USERNAME);
        if (email == null)
        {
            return;
        }

        // are there any properties that have been updated and require synchronization
        final Map<QName, Serializable> modifiedProperties = getModifiedProperties(before, after);
        final Map<String, Serializable> preferences = readPreferences(email, modifiedProperties);
        removeProperties(modifiedProperties, UPDATE_PROPS_FILTER);
        if (modifiedProperties.size() == 0 && preferences.size() == 0)
        {
            return;
        }
        
        // for each tenant that person belongs to, synchronise modified properties 
        List<String> tenants = getTenants(email);
        if (tenants.size() < 2)
        {
            return;
        }

        // construct person info from properties
        final PersonInfo personInfo = createPersonInfo(nodeRef, modifiedProperties, preferences, null);

        for (String tenant : tenants)
        {
            TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
            {
                public Void doWork() throws Exception
                {
                    NodeRef person = personService.getPerson(email);
                    if (person == null || person.equals(nodeRef))
                    {
                        // person does not exist in tenant (unlikely), or is the source of the change
                        return null;
                    }
                    
                    try
                    {
                        onUpdatePersonPropertiesBehaviour.disable();
                        
                        // update properties (non-content)
                        Map<QName, Serializable> props = nodeService.getProperties(person);
                        props.putAll(personInfo.props);
                        nodeService.setProperties(person, props);
                        
                        // update content properties
                        writeContentProperties(person, personInfo.contentProps);
                        
                        // update preferences
                        writePreferences(email, personInfo.preferences);
                        
                        return null;
                    }
                    finally
                    {
                        onUpdatePersonPropertiesBehaviour.enable();
                    }
                }
            }, tenant);
        }
    }

    @Override
    public void onCreateAssociation(AssociationRef childAssocRef)
    {
        // check user name can be found in person
        final NodeRef person = childAssocRef.getSourceRef();
        Map<QName, Serializable> props = nodeService.getProperties(person);
        final String email = (String)props.get(ContentModel.PROP_USERNAME);
        if (email == null)
        {
            return;
        }
        
        // for each tenant that person belongs to, synchronise modified properties 
        List<String> tenants = getTenants(email);
        if (tenants.size() < 2)
        {
            return;
        }
        
        final ContentReader avatar = readContent(childAssocRef.getTargetRef(), ContentModel.PROP_CONTENT);
        if (avatar == null)
        {
            return;
        }
        
        for (String tenant : tenants)
        {
            TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
            {
                public Void doWork() throws Exception
                {
                    NodeRef tenantPerson = personService.getPerson(email);
                    if (tenantPerson == null || tenantPerson.equals(person))
                    {
                        // person does not exist in tenant (unlikely), or is the source of the change
                        return null;
                    }
                    
                    try
                    {
                        onCreateImageChildAssociationBehaviour.disable();
                        onUpdatePersonPropertiesBehaviour.disable();
                        writePersonAvatar(tenantPerson, avatar.getReader());
                        return null;
                    }
                    finally
                    {
                        onUpdatePersonPropertiesBehaviour.enable();
                        onCreateImageChildAssociationBehaviour.enable();
                    }
                }
            }, tenant);
        }
    }
    
    private Map<String, Serializable> readPreferences(String email, Map<QName, Serializable> properties)
    {
        Map<String, Serializable> preferences = Collections.emptyMap();
        if (properties.containsKey(ContentModel.PROP_PREFERENCE_VALUES))
        {
            preferences = new HashMap<String, Serializable>();
            for (String preference : PREFERENCES)
            {
                Serializable value = preferenceService.getPreference(email, preference);
                preferences.put(preference, value);
            }
        }
        return preferences;
    }
    
    /*package*/ ContentReader readPersonAvatar(NodeRef person)
    {
        ContentReader avatarReader = null;
        List<ChildAssociationRef> images = nodeService.getChildAssocs(person, ContentModel.ASSOC_PREFERENCE_IMAGE, RegexQNamePattern.MATCH_ALL);
        if (images != null && images.size() > 0)
        {
            NodeRef avatar = images.get(0).getChildRef();
            avatarReader = readContent(avatar, ContentModel.PROP_CONTENT);
        }
        return avatarReader; 
    }

    private ContentReader readContent(NodeRef avatar, QName content)
    {
        ContentReader avatarReader = null;
        ContentReader reader = contentService.getReader(avatar, content);
        if (reader != null && reader.exists())
        {
            ContentWriter writer = contentService.getTempWriter();
            writer.setEncoding(reader.getEncoding());
            writer.setLocale(reader.getLocale());
            writer.setMimetype(reader.getMimetype());
            writer.putContent(reader);
            avatarReader = writer.getReader();
        }
        return avatarReader;
    }

    private void writePreferences(String email, Map<String, Serializable> preferences)
    {
        if (!preferences.isEmpty())
        {
            preferenceService.setPreferences(email, preferences);
        }
    }
    
    /*package*/ void writePersonAvatar(NodeRef person, ContentReader avatar)
    {
        // ensure person supports preferences
        if (!nodeService.hasAspect(person, ContentModel.ASPECT_PREFERENCES))
        {
            nodeService.addAspect(person, ContentModel.ASPECT_PREFERENCES, null);
        }
        
        // remove existing avatar, if exists
        List<ChildAssociationRef> images = nodeService.getChildAssocs(person, ContentModel.ASSOC_PREFERENCE_IMAGE, RegexQNamePattern.MATCH_ALL);
        if (images != null && images.size() > 0)
        {
            for (ChildAssociationRef image : images)
            {
                nodeService.deleteNode(image.getChildRef());
            }
        }
        
        // create avatar
        ChildAssociationRef avatarRef = nodeService.createNode(person, ContentModel.ASSOC_PREFERENCE_IMAGE, ContentModel.ASSOC_PREFERENCE_IMAGE, ContentModel.TYPE_CONTENT);
        writeContent(avatarRef.getChildRef(), ContentModel.PROP_CONTENT, avatar);
        
        // wire up 'cm:avatar' target association - backward compatible with JSF web-client avatar
        // NOTE: the following remove ensures that archived associations are deleted
        List<AssociationRef> existingAvatars = nodeService.getTargetAssocs(person, ContentModel.ASSOC_AVATAR);
        if (existingAvatars != null && existingAvatars.size() > 0)
        {
            for (AssociationRef existingAvatar : existingAvatars)
            {
                nodeService.removeAssociation(existingAvatar.getSourceRef(), existingAvatar.getTargetRef(), ContentModel.ASSOC_AVATAR);
            }
        }
        nodeService.createAssociation(person, avatarRef.getChildRef(), ContentModel.ASSOC_AVATAR);
    }
    
    private Map<QName, Serializable> getModifiedProperties(Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        Map<QName, Serializable> remainder = new HashMap<QName, Serializable>(after);
        Map<QName, Serializable> modifiedProperties = new HashMap<QName, Serializable>();
        for (QName name : before.keySet())
        {
            if (after.containsKey(name) == true)
            {
                Serializable beforeValue = before.get(name);
                Serializable afterValue = after.get(name);
                if (EqualsHelper.nullSafeEquals(beforeValue, afterValue) != true)
                {
                    // The property has been changed
                    modifiedProperties.put(name, after.get(name));
                }
                
                // Remove the property from the remainder list
                remainder.remove(name);
            }
        }
        
        // Add any properties now remaining whose values have been added for the first time
        if (remainder.size() != 0)
        {
            modifiedProperties.putAll(remainder);
        }
        
        return modifiedProperties;
    }
    
    private void removeProperties(Map<QName, Serializable> props, List<QName> toRemove)
    {
        for (QName name : toRemove)
        {
            props.remove(name);
        }
    }
    
    private List<String> getTenants(String email)
    {
        Account homeAccount = registrationService.getHomeAccount(email);
        List<Account> secondaryAccounts = registrationService.getSecondaryAccounts(email);
        List<String> tenants = new ArrayList<String>(secondaryAccounts.size() + 1);
        if (homeAccount != null)
        {
            tenants.add(homeAccount.getTenantId());
        }
        for (Account secondaryAccount : secondaryAccounts)
        {
            tenants.add(secondaryAccount.getTenantId());
        }
        return tenants;
    }
    
    private static class PersonInfo
    {
        private Map<QName, Serializable> props;
        private Map<QName, ContentReader> contentProps;
        private Map<String, Serializable> preferences;
        private ContentReader avatar;
    }
}
