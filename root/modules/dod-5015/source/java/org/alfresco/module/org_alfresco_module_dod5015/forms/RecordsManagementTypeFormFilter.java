package org.alfresco.module.org_alfresco_module_dod5015.forms;

import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.repo.forms.FieldDefinition;
import org.alfresco.repo.forms.Form;
import org.alfresco.repo.forms.FormData;
import org.alfresco.repo.forms.processor.AbstractFilter;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.NamespaceService;
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
public class RecordsManagementTypeFormFilter extends AbstractFilter implements DOD5015Model
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementTypeFormFilter.class);
    
    protected NamespaceService namespaceService;
    
    /**
     * Sets the NamespaceService instance
     * 
     * @param namespaceService The NamespaceService instance
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
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
        }
    }

    /*
     * @see org.alfresco.repo.forms.processor.Filter#afterPersist(java.lang.Object, org.alfresco.repo.forms.FormData, java.lang.Object)
     */
    public void afterPersist(Object item, FormData data, Object persistedObject)
    {
        // ignored
    }

    /*
     * @see org.alfresco.repo.forms.processor.Filter#beforeGenerate(java.lang.Object, java.util.List, java.util.List, org.alfresco.repo.forms.Form)
     */
    public void beforeGenerate(Object item, List<String> fields, List<String> forcedFields,
                Form form)
    {
        // ignored
    }

    /*
     * @see org.alfresco.repo.forms.processor.Filter#beforePersist(java.lang.Object, org.alfresco.repo.forms.FormData)
     */
    public void beforePersist(Object item, FormData data)
    {
        // ignored
    }
}
