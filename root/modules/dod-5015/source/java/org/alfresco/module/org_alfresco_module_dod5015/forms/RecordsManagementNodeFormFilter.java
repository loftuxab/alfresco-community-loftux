package org.alfresco.module.org_alfresco_module_dod5015.forms;

import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.CustomisableRmElement;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementCustomModel;
import org.alfresco.repo.forms.FieldDefinition;
import org.alfresco.repo.forms.Form;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of a form processor Filter.
 * 
 * <p>The filter ensures that any custom properties defined for the
 * records management type are provided as part of the Form and also
 * assigned to the same field group.</p>
 *
 * @author Gavin Cornwell
 */
public class RecordsManagementNodeFormFilter extends RecordsManagementFormFilter implements RecordsManagementCustomModel
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementNodeFormFilter.class);

    /*
     * @see org.alfresco.repo.forms.processor.Filter#afterGenerate(java.lang.Object, java.util.List, java.util.List, org.alfresco.repo.forms.Form, java.util.Map)
     */
    public void afterGenerate(Object item, List<String> fields, List<String> forcedFields, 
                Form form, Map<String, Object> context)
    {
        // as we're registered with the node form processor we know we are being
        // given a NodeRef, so check the current item is an RM type
        NodeRef nodeRef = (NodeRef)item;
        
        // if the node has the RM marker aspect look for the custom properties for the type
        if (this.nodeService.hasAspect(nodeRef, ASPECT_FILE_PLAN_COMPONENT))
        {
            if (this.nodeService.hasAspect(nodeRef, ASPECT_RECORD))
            {
                // determine whether the record has any custom RM properties
                if (this.nodeService.hasAspect(nodeRef, ASPECT_CUSTOM_RECORD_PROPERTIES))
                {
                    // add the 'rm-custom' field group 
                    addCustomRMGroup(form);
                }
                else
                {
                    // add field defintions for all the custom properties
                    addCustomRMProperties(CustomisableRmElement.RECORD, form);
                }
            }
            else
            {
                QName type = this.nodeService.getType(nodeRef);
                if (TYPE_RECORD_SERIES.equals(type))
                {
                    // determine whether the record series has any custom RM properties
                    if (this.nodeService.hasAspect(nodeRef, ASPECT_CUSTOM_RECORD_SERIES_PROPERTIES))
                    {
                        // add the 'rm-custom' field group 
                        addCustomRMGroup(form);
                    }
                    else
                    {
                        // add field defintions for all the custom properties
                        addCustomRMProperties(CustomisableRmElement.RECORD_SERIES, form);
                    }
                }
                else if (TYPE_RECORD_CATEGORY.equals(type))
                {
                    // determine whether the record category has any custom RM properties
                    if (this.nodeService.hasAspect(nodeRef, ASPECT_CUSTOM_RECORD_CATEGORY_PROPERTIES))
                    {
                        // add the 'rm-custom' field group 
                        addCustomRMGroup(form);
                    }
                    else
                    {
                        // add field defintions for all the custom properties
                        addCustomRMProperties(CustomisableRmElement.RECORD_CATEGORY, form);
                    }
                }
                else if (TYPE_RECORD_FOLDER.equals(type))
                {
                    // determine whether the record folder has any custom RM properties
                    if (this.nodeService.hasAspect(nodeRef, ASPECT_CUSTOM_RECORD_FOLDER_PROPERTIES))
                    {
                        // add the 'rm-custom' field group 
                        addCustomRMGroup(form);
                    }
                    else
                    {
                        // add field defintions for all the custom properties
                        addCustomRMProperties(CustomisableRmElement.RECORD_FOLDER, form);
                    }
                }
            }
        }
    }
    
    /**
     * Adds the Custom RM field group (id 'rm-custom') to all the field 
     * definitions representing RM custom properties.
     * 
     * @param form The form holding the field defintions
     */
    protected void addCustomRMGroup(Form form)
    {
        // iterate round existing fields and set group on each custom
        // RM field
        List<FieldDefinition> fieldDefs = form.getFieldDefinitions(); 
        for (FieldDefinition fieldDef : fieldDefs)
        {
            if (fieldDef.getName().startsWith(RM_CUSTOM_PREFIX))
            {
                fieldDef.setGroup(CUSTOM_RM_FIELD_GROUP);
                
                if (logger.isDebugEnabled())
                    logger.debug("Added \"" + fieldDef.getName() + "\" to RM custom field group");
            }
        }
    }
}
