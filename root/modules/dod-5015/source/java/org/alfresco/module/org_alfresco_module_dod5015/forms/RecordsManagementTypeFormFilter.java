package org.alfresco.module.org_alfresco_module_dod5015.forms;

import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.CustomisableRmElement;
import org.alfresco.repo.forms.FieldDefinition;
import org.alfresco.repo.forms.Form;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of a form processor Filter.
 * 
 * <p>The filter implements the <code>afterGenerate</code> method to ensure a
 * default unique identifier is provided for the <code>rma:identifier</code>
 * property.</p>
 * <p>The filter also ensures that any custom properties defined for the
 * records management type are provided as part of the Form.</p>
 *
 * @author Gavin Cornwell
 */
public class RecordsManagementTypeFormFilter extends RecordsManagementFormFilter
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementTypeFormFilter.class);
    
    /*
     * @see org.alfresco.repo.forms.processor.Filter#afterGenerate(java.lang.Object, java.util.List, java.util.List, org.alfresco.repo.forms.Form)
     */
    public void afterGenerate(Object item, List<String> fields, List<String> forcedFields, Form form)
    {
        // as we're registered with the type form processor we know we are being
        // given a TypeDefinition, so check the current item is an RM type
        TypeDefinition type = (TypeDefinition)item;
        QName typeName = type.getName();
        
        if (TYPE_RECORD_SERIES.equals(typeName) || 
            TYPE_RECORD_CATEGORY.equals(typeName) ||
            TYPE_RECORD_FOLDER.equals(typeName))
        {
            if (logger.isDebugEnabled())
                logger.debug("Generating unique identifier for " + typeName.toPrefixString(this.namespaceService));
            
            // find the field definition for the rma:identifier property
            List<FieldDefinition> fieldDefs = form.getFieldDefinitions();
            String identifierPropName = PROP_IDENTIFIER.toPrefixString(this.namespaceService); 
            for (FieldDefinition fieldDef : fieldDefs)
            {
                if (fieldDef.getName().equals(identifierPropName))
                {
                    fieldDef.setDefaultValue(GUID.generate());
                    break;
                }
            }
            
            // add any custom properties for the type being created (we don't need to deal with
            // the record type in here as records are typically uploaded and then their metadata
            // edited after the fact)
            if (TYPE_RECORD_SERIES.equals(typeName))
            {
                addCustomRMProperties(CustomisableRmElement.RECORD_SERIES, form);
            }
            else if (TYPE_RECORD_CATEGORY.equals(typeName))
            {
                addCustomRMProperties(CustomisableRmElement.RECORD_CATEGORY, form);
            }
            else if (TYPE_RECORD_FOLDER.equals(typeName))
            {
                addCustomRMProperties(CustomisableRmElement.RECORD_FOLDER, form);
            }
        }
    }
}
