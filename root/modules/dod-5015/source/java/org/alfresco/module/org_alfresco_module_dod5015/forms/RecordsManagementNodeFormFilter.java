package org.alfresco.module.org_alfresco_module_dod5015.forms;

import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.CustomisableRmElement;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementCustomModel;
import org.alfresco.repo.forms.FieldDefinition;
import org.alfresco.repo.forms.Form;
import org.alfresco.repo.forms.PropertyFieldDefinition;
import org.alfresco.repo.forms.processor.node.ContentModelFormProcessor;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
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
    
    protected static final String TRANSIENT_DECLARED = "rmDeclared";
    protected static final String TRANSIENT_RECORD_TYPE = "rmRecordType";

    protected DictionaryService dictionaryService;
    
    /**
     * Sets the data dictionary service
     * 
     * @param dictionaryService The DictionaryService instance
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
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
                
                // generate property definitions for the 'transient' properties
                generateDeclaredPropertyField(form, nodeRef);
                generateRecordTypePropertyField(form, nodeRef);
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
    
    /**
     * Generates the field definition for the transient <code>rmDeclared</code> property
     * 
     * @param form The Form instance to add the property to
     * @param nodeRef The node the form is being generated for
     */
    protected void generateDeclaredPropertyField(Form form, NodeRef nodeRef)
    {
        String dataKeyName = ContentModelFormProcessor.PROP_DATA_PREFIX + TRANSIENT_DECLARED;
        PropertyFieldDefinition declaredField = new PropertyFieldDefinition(
                    TRANSIENT_DECLARED, DataTypeDefinition.BOOLEAN.getLocalName());
        declaredField.setLabel(TRANSIENT_DECLARED);
        declaredField.setDescription(TRANSIENT_DECLARED);
        declaredField.setProtectedField(true);
        declaredField.setDataKeyName(dataKeyName);
        form.addFieldDefinition(declaredField);
        form.addData(dataKeyName, this.nodeService.hasAspect(nodeRef, ASPECT_DECLARED_RECORD));
    }
    
    /**
     * Generates the field definition for the transient <code>rmRecordType</code> property
     * 
     * @param form The Form instance to add the property to
     * @param nodeRef The node the form is being generated for
     */
    protected void generateRecordTypePropertyField(Form form, NodeRef nodeRef)
    {
        String dataKeyName = ContentModelFormProcessor.PROP_DATA_PREFIX + TRANSIENT_RECORD_TYPE;
        PropertyFieldDefinition recordTypeField = new PropertyFieldDefinition(
                    TRANSIENT_RECORD_TYPE, DataTypeDefinition.TEXT.getLocalName());
        recordTypeField.setLabel(TRANSIENT_RECORD_TYPE);
        recordTypeField.setDescription(TRANSIENT_RECORD_TYPE);
        recordTypeField.setProtectedField(true);
        recordTypeField.setDataKeyName(dataKeyName);
        form.addFieldDefinition(recordTypeField);
        
        // determine what record type value to return, use aspect/type title from model
        String recordType = null;
        QName type = this.nodeService.getType(nodeRef);
        if (TYPE_NON_ELECTRONIC_DOCUMENT.equals(type))
        {
            // get the non-electronic type title
            recordType = dictionaryService.getType(TYPE_NON_ELECTRONIC_DOCUMENT).getTitle();
        }
        else
        {
            // the aspect applied to record determines it's type
            if (nodeService.hasAspect(nodeRef, ASPECT_PDF_RECORD))
            {
                recordType = dictionaryService.getAspect(ASPECT_PDF_RECORD).getTitle();
            }
            else if (nodeService.hasAspect(nodeRef, ASPECT_WEB_RECORD))
            {
                recordType = dictionaryService.getAspect(ASPECT_WEB_RECORD).getTitle();
            }
            else if (nodeService.hasAspect(nodeRef, ASPECT_SCANNED_RECORD))
            {
                recordType = dictionaryService.getAspect(ASPECT_SCANNED_RECORD).getTitle();
            }
            else if (nodeService.hasAspect(nodeRef, ASPECT_DIGITAL_PHOTOGRAPH_RECORD))
            {
                recordType = dictionaryService.getAspect(ASPECT_DIGITAL_PHOTOGRAPH_RECORD).getTitle();
            }
            else
            {
                // no specific aspect applied so default to just "Record"
                recordType = dictionaryService.getAspect(ASPECT_RECORD).getTitle();
            }
        }
        
        form.addData(dataKeyName, recordType);
    }
}
