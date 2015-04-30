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

package org.alfresco.enterprise.repo.forms.jmx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.alfresco.repo.forms.Field;
import org.alfresco.repo.forms.FormData;
import org.alfresco.repo.forms.FormException;
import org.alfresco.repo.forms.FormNotFoundException;
import org.alfresco.repo.forms.Item;
import org.alfresco.repo.forms.FormData.FieldData;
import org.alfresco.repo.forms.processor.FilteredFormProcessor;
import org.alfresco.repo.forms.processor.FormCreationData;
import org.alfresco.repo.forms.processor.node.FormFieldConstants;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.security.AuthorityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Form processor implementation that exposes MBean attributes and parameter-less
 * operations as a form.
 *
 * @author Gavin Cornwell
 */
public class JMXFormProcessor extends FilteredFormProcessor<ObjectName, ObjectName>
{
    private static final Log logger = LogFactory.getLog(JMXFormProcessor.class);

    public static final String OPERATIONS_FIELD_NAME = "mbean_operations";
    public static final String OPERATIONS_FIELD_NAME_PERSIST = FormFieldConstants.PROP_DATA_PREFIX + OPERATIONS_FIELD_NAME;
    public static final String IGNORE_TYPE = "$type";
    
    /** The MBean server. */
    private MBeanServerConnection mbeanServer;
    
    /** Authority service */
    private AuthorityService authorityService;
    
    /** List of MBean operations to ignore */
    private List<String> ignoredOperations;

    /**
     * @param mBeanServer       MBeanServerConnection bean
     */
    public void setMBeanServer(MBeanServerConnection mBeanServer)
    {
        this.mbeanServer = mBeanServer;
    }
    
    /**
     * Sets the AuthorityService
     * 
     * @param authorityService AuthorityService instance
     */
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }
    
    public void setIgnoredOperations(List<String> ignoredOperations)
    {
        this.ignoredOperations = ignoredOperations;
    }
    
    @Override
    protected List<String> getDefaultIgnoredFields()
    {
        List<String> ignore = new ArrayList<String>(1);
        ignore.add(IGNORE_TYPE);
        
        return ignore;
    }

    @Override
    protected String getItemType(ObjectName bean)
    {
        return bean.toString();
    }

    @Override
    protected String getItemURI(ObjectName bean)
    {
        // There isn't a REST API for JMX bean so return empty string
        return "";
    }

    @Override
    protected Log getLogger()
    {
        return logger;
    }

    @Override
    protected ObjectName getTypedItem(Item item)
    {
        // Ensure the current user is an administrator
        if (!this.authorityService.hasAdminAuthority())
        {
            throw new AccessDeniedException("Only administrators can use the JMXFormProcessor");
        }
        
        ObjectName objectName = null;
        
        try
        {
            objectName = new ObjectName(decodeItemId(item.getId()));
            
            // make sure the object name is valid
            this.mbeanServer.getMBeanInfo(objectName);
        }
        catch (Exception e)
        {
            throw new FormNotFoundException(item, e);
        }
        
        return objectName;
    }
    
    /**
     * Decodes the item id, replaces the first underscore with a colon.
     * 
     * @param itemId The id of the item
     * @return The decoded item id
     */
    protected String decodeItemId(String itemId)
    {
        // the REST API replaces any colons with an underscore so we need 
        // to change them back. However, an underscore is a legal character
        // for an MBean so we can't just replace all underscores with colons.
        
        // For now we will just replace the first underscore as we know that
        // will always be present for example:
        
        // Alfresco:Name=Runtime
        // Alfresco:Type=Configuration,Category=email,id1=inbound
        
        return itemId.replaceFirst("_", ":");
    }
    
    /**
     * Decodes the given field name, removes prefix and decodes dots.
     * 
     * @param fieldName Name of the field to persist
     * @return The decoded field name
     */
    protected String decodeFieldName(String fieldName)
    {
        // NOTE: The UI adds "prop_" as a prefix and encodes any dot characters
        //       so these must be decoded and the prefix removed.
        
        return fieldName.substring(5).replaceAll(FormFieldConstants.DOT_CHARACTER_REPLACEMENT, 
                    FormFieldConstants.DOT_CHARACTER);
    }

    @Override
    protected ObjectName internalPersist(ObjectName objectName, FormData data)
    {
        String operationName = null;
        
        try
        {
            AttributeList attrList = new AttributeList(data.getNumberOfFields());
            Set<String> fields = data.getFieldNames();
            
            for (String fieldName : fields)
            {
                FieldData fieldData = data.getFieldData(fieldName);
                
                // if the field is the mbean_operations field remember the operation requested
                if (OPERATIONS_FIELD_NAME_PERSIST.equals(fieldName))
                {
                    operationName = (String)fieldData.getValue();
                    
                    if (logger.isDebugEnabled())
                        logger.debug("Found operation '" + operationName);
                }
                else
                {
                    if (fieldName.startsWith(FormFieldConstants.PROP_DATA_PREFIX))
                    {
                        // add the attribute
                        Attribute attr = new Attribute(decodeFieldName(fieldName), fieldData.getValue());
                        attrList.add(attr);
                        
                        if (logger.isDebugEnabled())
                            logger.debug("Persisting attribute '" + attr.getName() + 
                                        "' with value of '" + attr.getValue() + "'");
                    }
                    else if (logger.isDebugEnabled())
                    {
                        logger.debug("Ignoring field '" + fieldName + "' as it doesn't start with " + 
                                    FormFieldConstants.PROP_DATA_PREFIX);
                    }
                }
            }
            
            this.mbeanServer.setAttributes(objectName, attrList);
        }
        catch (Exception e)
        {
            throw new FormException("error_persist_attributes", new Object[] {e.getMessage()});
        }
        
        // if an operation was found execute it
        if (operationName != null && operationName.length() > 0)
        {
            try
            {
                if (logger.isDebugEnabled())
                    logger.debug("Invoking operation '" + operationName + "'");
                
                // execute the operation
                this.mbeanServer.invoke(objectName, operationName, new Object[]{}, new String[]{});
            }
            catch (Exception e)
            {
                throw new FormException("error_invoke_operation", new Object[] {operationName, e.getMessage()});
            }
        }
        
        return objectName;
    }

    @Override
    protected Object makeItemData(ObjectName objectName)
    {
        MBeanItemData itemData = null;
        
        try
        {
            MBeanInfo mbean = this.mbeanServer.getMBeanInfo(objectName);
            itemData = new MBeanItemData(objectName, mbean);
        }
        catch (Exception e)
        {
            throw new FormException("Failed to retrieve MBeanInfo", e);
        }
        
        return itemData;
    }

    @Override
    protected List<Field> generateDefaultFields(FormCreationData data, List<String> fieldsToIgnore)
    {
        List<Field> fields = new ArrayList<Field>(8);
        
        MBeanItemData itemData = (MBeanItemData)data.getItemData();
        MBeanInfo mbean = itemData.getMBean();

        // generate field for each attribute
        for (MBeanAttributeInfo attrInfo : mbean.getAttributes())
        {
            if (!fieldsToIgnore.contains(attrInfo.getName()))
            {
                fields.add(buildAttributeField(itemData.getObjectName(), attrInfo));
            }
        }
        
        // generate operations field
        fields.add(buildOperationsField(itemData));
        
        return fields;
    }
    
    @Override
    protected List<Field> generateSelectedFields(List<String> requestedFields, FormCreationData data)
    {
        List<Field> fields = new ArrayList<Field>(requestedFields.size());
        
        MBeanItemData itemData = (MBeanItemData)data.getItemData();
        MBeanInfo mbean = itemData.getMBean();
        
        // put attributes into map so we can look them up easily
        MBeanAttributeInfo[] attributes = mbean.getAttributes();
        Map<String, MBeanAttributeInfo> attributesMap = new HashMap<String, MBeanAttributeInfo>();
        for (MBeanAttributeInfo attrInfo : attributes)
        {
            attributesMap.put(attrInfo.getName(), attrInfo);
        }
        
        // iterate around the requested fields and build the appropriate Field object
        for (String fieldName : requestedFields)
        {
            if (OPERATIONS_FIELD_NAME.equals(fieldName))
            {
                // generate operations field
                fields.add(buildOperationsField(itemData));
            }
            else
            {
                if (attributesMap.containsKey(fieldName))
                {
                    // generate attribute field
                    fields.add(buildAttributeField(itemData.getObjectName(), 
                                attributesMap.get(fieldName)));
                }
                else if (logger.isWarnEnabled())
                {
                    logger.warn("Ignoring attribute '" + fieldName + "' as it does not exist on the MBean '" +
                                itemData.getObjectName().toString() + "'");
                }
            }
        }
        
        return fields;
    }
    
    /**
     * Creates a Field representing the given MBean.
     * 
     * @param objectName Name of the MBean to create a field for
     * @param attribute The attribute to create a field for
     * @return The new Field object
     */
    protected Field buildAttributeField(ObjectName objectName, MBeanAttributeInfo attribute)
    {
        Object value = null;
        
        try
        {
            value = this.mbeanServer.getAttribute(objectName, attribute.getName());
        }
        catch (Exception e)
        {
            throw new FormException("Failed to retrieve value of MBean Attribute", e);
        }
        
        return new MBeanAttributeField(attribute, value);
    }
    
    /**
     * Creates a Field representing the operations of the MBean.
     * 
     * @param itemData The DTO object representing the MBean
     * @return The new Field object
     */
    protected Field buildOperationsField(MBeanItemData itemData)
    {
        return new MBeanOperationsField(itemData.getMBean().getOperations(), this.ignoredOperations);
    }
    
    /**
     * DTO containing the ObjectName and MBeanInfo representation
     * of the item being requested.
     *
     * @author Gavin Cornwell
     */
    class MBeanItemData
    {
        private ObjectName objectName;
        private MBeanInfo mbean;
        
        public MBeanItemData(ObjectName objectName, MBeanInfo mbean)
        {
            this.objectName = objectName;
            this.mbean = mbean;
        }
        
        public ObjectName getObjectName()
        {
            return this.objectName;
        }
        
        public MBeanInfo getMBean()
        {
            return this.mbean;
        }
    }
}
