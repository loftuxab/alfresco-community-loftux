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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.element.ConfigElementAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Custom config element that represents form values for the client.
 * 
 * @author Neil McErlean.
 */
public class FormConfigElement extends ConfigElementAdapter
{
    private static final long serialVersionUID = -7008510360503886308L;
    private static Log logger = LogFactory.getLog(FormConfigElement.class);

    public static final String FORM_ID = "form";
    private String submissionURL;
//    private List<StringPair> modelOverrides = new ArrayList<StringPair>();
    
    /**
     * Map of the required roles for create-form templates.
     * Key = the template String. Value = the requires-role String.
     */
    private final Map<String, String> rolesForCreateTemplates = new LinkedHashMap<String, String>();
    private final Map<String, String> rolesForEditTemplates = new LinkedHashMap<String, String>();
    private final Map<String, String> rolesForViewTemplates = new LinkedHashMap<String, String>();
    
    FieldVisibilityManager fieldVisibilityManager = new FieldVisibilityManager();
    private final Map<String, FormSet> sets = new LinkedHashMap<String, FormSet>();
    private Map<String, FormField> fields = new LinkedHashMap<String, FormField>();
    
    public FormConfigElement()
    {
        super(FORM_ID);
    }

    public FormConfigElement(String name)
    {
        super(name);
    }

    /**
     * @see org.alfresco.config.ConfigElement#getChildren()
     */
    @Override
    public List<ConfigElement> getChildren()
    {
        throw new ConfigException(
                "Reading the default-controls config via the generic interfaces is not supported");
    }

    /**
     * @see org.alfresco.config.ConfigElement#combine(org.alfresco.config.ConfigElement)
     */
    @Override
    public ConfigElement combine(ConfigElement otherConfigElement)
    {
        FormConfigElement otherFormElem = (FormConfigElement)otherConfigElement;
        FormConfigElement result = new FormConfigElement();
        
        combineSubmissionURL(otherFormElem, result);
        
        combineTemplates(otherFormElem, result);
        
        combineFieldVisibilities(otherFormElem, result);
        
        combineSets(otherFormElem, result);
        
        combineFields(otherFormElem, result);
        
//        combineModelOverrides(otherFormElem, result);
        
        return result;
    }

    private void combineFields(FormConfigElement otherFormElem,
            FormConfigElement result)
    {
        Map<String, FormField> newFields = new LinkedHashMap<String, FormField>();
        for (String nextFieldId : this.fields.keySet())
        {
            FormField nextFieldFromThis = this.fields.get(nextFieldId);
            if (otherFormElem.getFields().containsKey(nextFieldId))
            {
                FormField combinedField = nextFieldFromThis
                    .combine(otherFormElem.getFields().get(nextFieldId));
                newFields.put(nextFieldId, combinedField);
            }
            else
            {
                newFields.put(nextFieldId, nextFieldFromThis);
            }
        }

        for (String nextFieldId : otherFormElem.fields.keySet())
        {
            if (!this.fields.containsKey(nextFieldId))
            {
                newFields.put(nextFieldId, otherFormElem.fields.get(nextFieldId));
            }
            else
            {
                // handled by above loop.
            }
        }
        result.setFields(newFields);
    }

//    private void combineModelOverrides(FormConfigElement otherFormElem,
//            FormConfigElement result)
//    {
//        for (StringPair override : modelOverrides)
//        {
//            result.addModelOverrides(override.getName(), override.getValue());
//        }
//        for (StringPair override : otherFormElem.modelOverrides)
//        {
//            result.addModelOverrides(override.getName(), override.getValue());
//        }
//    }

    private void combineSets(FormConfigElement otherFormElem,
            FormConfigElement result)
    {
        for (String nextOldSet : sets.keySet())
        {
            FormSet nextOldSetData = sets.get(nextOldSet);
            String setId = nextOldSetData.getSetId();
            String parentId = nextOldSetData.getParentId();
            String appearance = nextOldSetData.getAppearance();
            result.addSet(setId, parentId, appearance);
        }
        for (String nextNewSet : otherFormElem.sets.keySet())
        {
            FormSet nextNewSetData = otherFormElem.sets.get(nextNewSet);
            String setId = nextNewSetData.getSetId();
            String parentId = nextNewSetData.getParentId();
            String appearance = nextNewSetData.getAppearance();
            result.addSet(setId, parentId, appearance);
        }
    }

    private void combineFieldVisibilities(FormConfigElement otherFormElem,
            FormConfigElement result)
    {
        FieldVisibilityManager combinedManager
                = this.fieldVisibilityManager.combine(otherFormElem.fieldVisibilityManager);
        result.fieldVisibilityManager = combinedManager;
    }

    private void combineTemplates(FormConfigElement otherFormElem,
            FormConfigElement result)
    {
        for (String s : rolesForCreateTemplates.keySet())
        {
            String reqsRole = this.rolesForCreateTemplates.get(s);
            result.addFormTemplate("create-form", s, reqsRole);
        }
        for (String s : otherFormElem.rolesForCreateTemplates.keySet())
        {
            String reqsRole = otherFormElem.rolesForCreateTemplates.get(s);
            result.addFormTemplate("create-form", s, reqsRole);
        }
        
        for (String s : this.rolesForEditTemplates.keySet())
        {
            String reqsRole = this.rolesForEditTemplates.get(s);
            result.addFormTemplate("edit-form", s, reqsRole);
        }
        for (String s : otherFormElem.rolesForEditTemplates.keySet())
        {
            String reqsRole = otherFormElem.rolesForEditTemplates.get(s);
            result.addFormTemplate("edit-form", s, reqsRole);
        }
        
        for (String s : this.rolesForViewTemplates.keySet())
        {
            String reqsRole = this.rolesForViewTemplates.get(s);
            result.addFormTemplate("view-form", s, reqsRole);
        }
        for (String s : otherFormElem.rolesForViewTemplates.keySet())
        {
            String reqsRole = otherFormElem.rolesForViewTemplates.get(s);
            result.addFormTemplate("view-form", s, reqsRole);
        }
    }

    private void combineSubmissionURL(FormConfigElement otherFormElem,
            FormConfigElement result)
    {
        String otherSubmissionURL = otherFormElem.getSubmissionURL();
        result.setSubmissionURL(otherSubmissionURL == null ? this.submissionURL : otherSubmissionURL);
    }

    public String getSubmissionURL()
    {
        return this.submissionURL;
    }
    
    public Map<String, FormSet> getSets()
    {
        return Collections.unmodifiableMap(this.sets);
    }
    
    public Set<String> getSetIDs()
    {
        return Collections.unmodifiableSet(this.sets.keySet());
    }
    
    /**
     * This method returns a Map of those &lt;set&gt;s which have no declared parentID
     * i&#46;e&#46; those that are 'roots' in the tree of sets.
     * @return
     */
    public Map<String, FormSet> getRootSets()
    {
        Map<String, FormSet> result = new LinkedHashMap<String, FormSet>();
        for (Iterator<String> iter = sets.keySet().iterator(); iter.hasNext(); )
        {
            String nextKey = iter.next();
            FormSet nextSet = sets.get(nextKey);
            String nextParentID = nextSet.getParentId();
            if (nextParentID == null || nextParentID.trim().length() == 0)
            {
                result.put(nextKey, nextSet);
            }
        }
        return result;
    }
    
    public Map<String, FormField> getFields()
    {
        return Collections.unmodifiableMap(this.fields);
    }
    
    /**
     * This method returns true if the config XML contains any show tags under the
     * field-visibility tag. This is important as the presence of any show tags
     * changes the field visibility algorithm from one that manages which fields are
     * hidden to one that manages which fields are shown.
     * @return true if the field-visibility tag contains one or more show tags else false.
     */
    public boolean getFieldVisibilityContainsShow()
    {
        return !fieldVisibilityManager.isManagingHiddenFields();
    }
    
    public List<String> getVisibleCreateFieldNames()
    {
    	return getFieldNamesVisibleInMode(Mode.CREATE);
    }
    public List<String> getVisibleEditFieldNames()
    {
    	return getFieldNamesVisibleInMode(Mode.EDIT);
    }
    public List<String> getVisibleViewFieldNames()
    {
    	return getFieldNamesVisibleInMode(Mode.VIEW);
    }

    public Map<String, String> getCreateTemplates()
    {
        return Collections.unmodifiableMap(this.rolesForCreateTemplates);
    }
    
    public Map<String, String> getEditTemplates()
    {
        return Collections.unmodifiableMap(this.rolesForEditTemplates);
    }
    
    public Map<String, String> getViewTemplates()
    {
        return Collections.unmodifiableMap(this.rolesForViewTemplates);
    }
    
//    public List<StringPair> getModelOverrideProperties()
//    {
//        return Collections.unmodifiableList(modelOverrides);
//    }

    /* package */void setSubmissionURL(String newURL)
    {
        this.submissionURL = newURL;
    }

    /* package */void addFormTemplate(String nodeName, String template,
            String requiredRole)
    {
        if (requiredRole == null)
        {
            requiredRole = "";
        }
        
        if (nodeName.equals("create-form"))
        {
            rolesForCreateTemplates.put(template, requiredRole);
        }
        else if (nodeName.equals("edit-form"))
        {
            rolesForEditTemplates.put(template, requiredRole);
        }
        else if (nodeName.equals("view-form"))
        {
            rolesForViewTemplates.put(template, requiredRole);
        }
        else
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Unrecognised mode: " + nodeName);
            }
            return;
        }
    }

    /* package */void addFieldVisibility(String showOrHide, String fieldId,
            String mode)
    {
        fieldVisibilityManager.addInstruction(showOrHide, fieldId, mode);
    }

    /* package */void addSet(String setId, String parentSetId, String appearance)
    {
        FormSet newFormSetObject = new FormSet(setId, parentSetId, appearance);
        
        // I am disallowing the declaration of sets whose parents do not already exist.
        // The reason for this is to ensure that cycles within the parent structure
        // are not possible.
        if (parentSetId != null &&
                parentSetId.trim().length() != 0 &&
                !sets.containsKey(parentSetId))
        {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("Set [").append(setId).append("] has undefined parent [")
                .append(parentSetId).append("].");
            throw new ConfigException(errorMsg.toString());
        }
        
        sets.put(setId, newFormSetObject);
        
        // Set parent/child references
        if (parentSetId != null && parentSetId.trim().length() != 0)
        {
            FormSet parentObject = sets.get(parentSetId);
            
            newFormSetObject.setParent(parentObject);
            parentObject.addChild(newFormSetObject);
        }
    }

    /* package */void addField(String fieldId, List<String> attributeNames,
            List<String> attributeValues)
    {
        if (attributeNames == null)
        {
            attributeNames = Collections.emptyList();
        }
        if (attributeValues == null)
        {
            attributeValues = Collections.emptyList();
        }
        if (attributeNames.size() < attributeValues.size()
                && logger.isWarnEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("field ")
                .append(fieldId)
                .append(" has ")
                .append(attributeNames.size())
                .append(" xml attribute names and ")
                .append(attributeValues.size())
                .append(" xml attribute values. The trailing extra data will be ignored.");
            logger.warn(msg.toString());
        }
        
        Map<String, String> attrs = new LinkedHashMap<String, String>();
        for (int i = 0; i < attributeNames.size(); i++)
        {
            attrs.put(attributeNames.get(i), attributeValues.get(i));
        }
        fields.put(fieldId, new FormField(fieldId, attrs));
    }
    
    /* package */ void setFields(Map<String, FormField> newFieldsMap)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Setting new fields map " + newFieldsMap);
        }
        this.fields = newFieldsMap;
    }

    /* package */ void addControlForField(String fieldId, String template,
            List<String> controlParamNames, List<String> controlParamValues)
    {
        if (controlParamNames == null)
        {
            controlParamNames = Collections.emptyList();
        }
        if (controlParamValues == null)
        {
            controlParamValues = Collections.emptyList();
        }
        if (controlParamNames.size() < controlParamValues.size()
                && logger.isWarnEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("field ")
                .append(fieldId)
                .append(" has ")
                .append(controlParamNames.size())
                .append(" control-param names and ")
                .append(controlParamValues.size())
                .append(" control-param values. The trailing extra data will be ignored.");
            logger.warn(msg.toString());
        }
        
        FormField field = fields.get(fieldId);
        field.setTemplate(template);
        for (int i = 0; i < controlParamNames.size(); i++)
        {
            field.addControlParam(controlParamNames.get(i),
                    controlParamValues.get(i));
        }
    }

    /* package */void addConstraintForField(String fieldId, String type,
            String message, String messageId)
    {
        FormField field = fields.get(fieldId);
        field.addConstraintMessage(type, message, messageId);
    }

//    /* package */void addModelOverrides(String name, String value)
//    {
//        StringPair modelOverride = new StringPair(name, value);
//        //TODO Consider using a different collection type here.
//        for (Iterator<StringPair> iter = modelOverrides.iterator(); iter.hasNext(); )
//        {
//            if (iter.next().getName().equals(name))
//            {
//                iter.remove();
//            }
//        }
//        modelOverrides.add(modelOverride);
//    }
    
    /**
     * @param templatesToRoles e.g. {/foo/create=Manager, /foo/view=Consumer, /foo/edit=}
     * @param currentRoles e.g. ["Consumer", "Manager"]
     * @return
     */
    private String findFirstMatchingTemplate(Map<String, String> templatesToRoles, List<String> currentRoles)
    {
    	if (currentRoles == null)
    	{
    		currentRoles = Collections.emptyList();
    	}
    	// If currentRoles is empty, return first template that requires no role.
    	// If currentRoles is not empty, return first template that either
    	// requires no role, or requires one of the currentRoles.
    	for (String template : templatesToRoles.keySet())
    	{
    		String requiredRolesForThisTemplate = templatesToRoles.get(template);
    		if (currentRoles.isEmpty() && requiredRolesForThisTemplate.trim().length() == 0)
			{
				return template;
			}
    		for (String role : currentRoles)
    		{
    			if (requiredRolesForThisTemplate.trim().length() == 0 ||
    					requiredRolesForThisTemplate.contains(role))
    			return template;
    		}
    	}
    	return null;
    }
    
    /**
     * 
     * @param m
     * @param roles a list of roles, can be an empty list or null.
     * @return <code>null</code> if there is no template available for the specified role(s).
     */
    public String getFormTemplate(Mode m, List<String> roles)
    {
        switch (m)
        {
        case CREATE: return findFirstMatchingTemplate(rolesForCreateTemplates, roles);
        case EDIT: return findFirstMatchingTemplate(rolesForEditTemplates, roles);
        case VIEW: return findFirstMatchingTemplate(rolesForViewTemplates, roles);
        default: return null;
        }
    }
    
    /**
     * This method checks whether the specified field is visible in the specified mode.
     * 
     * @param fieldId
     * @param m
     * @return
     */
    public boolean isFieldVisible(String fieldId, Mode m)
    {
        return fieldVisibilityManager.isFieldVisible(fieldId, m);
    }

    private List<String> getFieldNamesVisibleInMode(Mode mode)
    {
        List<String> result = fieldVisibilityManager.getFieldNamesVisibleInMode(mode);
        return result;
    }
}

