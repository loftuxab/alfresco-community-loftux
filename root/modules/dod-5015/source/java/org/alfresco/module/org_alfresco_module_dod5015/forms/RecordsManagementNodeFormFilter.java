package org.alfresco.module.org_alfresco_module_dod5015.forms;

import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.repo.forms.FieldDefinition;
import org.alfresco.repo.forms.FieldGroup;
import org.alfresco.repo.forms.Form;
import org.alfresco.repo.forms.FormData;
import org.alfresco.repo.forms.processor.AbstractFilter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
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
public class RecordsManagementNodeFormFilter extends AbstractFilter implements DOD5015Model
{
    /** Logger */
    private static Log logger = LogFactory.getLog(RecordsManagementNodeFormFilter.class);
    
    protected static final String CUSTOM_RM_FIELD_GROUP_ID = "rm-custom";
    
    protected NamespaceService namespaceService;
    protected NodeService nodeService;
    protected RecordsManagementAdminService rmAdminService;
    
    /**
     * Sets the NamespaceService instance
     * 
     * @param namespaceService The NamespaceService instance
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    /**
     * Sets the node service 
     * 
     * @param nodeService The NodeService instance
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Sets the RecordsManagementAdminService instance
     * 
     * @param rmAdminService The RecordsManagementAdminService instance
     */
    public void setRecordsManagementAdminService(RecordsManagementAdminService rmAdminService)
    {
        this.rmAdminService = rmAdminService;
    }
    
    /*
     * @see org.alfresco.repo.forms.processor.Filter#afterGenerate(java.lang.Object, java.util.List, java.util.List, org.alfresco.repo.forms.Form)
     */
    public void afterGenerate(Object item, List<String> fields, List<String> forcedFields, Form form)
    {
        // as we're registered with the node form processor we know we are being
        // given a NodeRef, so check the current item is an RM type
        NodeRef nodeRef = (NodeRef)item;
        
        // if the node has the RM marker aspect look for the custom properties for the type
        if (this.nodeService.hasAspect(nodeRef, ASPECT_FILE_PLAN_COMPONENT))
        {
            FieldGroup customRMFieldGroup = new FieldGroup(CUSTOM_RM_FIELD_GROUP_ID, null, false, false, null);
            
            // iterate round existing fields and set group on each custom
            // RM field
            List<FieldDefinition> fieldDefs = form.getFieldDefinitions(); 
            for (FieldDefinition fieldDef : fieldDefs)
            {
                if (fieldDef.getName().startsWith(RM_CUSTOM_PREFIX))
                {
                    fieldDef.setGroup(customRMFieldGroup);
                    
                    if (logger.isDebugEnabled())
                        logger.debug("Added \"" + fieldDef.getName() + "\" to RM custom field group");
                }
            }
        }
    }

    /*
     * @see org.alfresco.repo.forms.processor.Filter#beforePersist(java.lang.Object, org.alfresco.repo.forms.FormData)
     */
    public void beforePersist(Object item, FormData data)
    {
        // ignored
    }
    
    /*
     * @see org.alfresco.repo.forms.processor.Filter#beforeGenerate(java.lang.Object, java.util.List, java.util.List, org.alfresco.repo.forms.Form)
     */
    public void beforeGenerate(Object item, List<String> fields, List<String> forcedFields, Form form)
    {
        // ignored
    }
    
    /*
     * @see org.alfresco.repo.forms.processor.Filter#afterPersist(java.lang.Object, org.alfresco.repo.forms.FormData, java.lang.Object)
     */
    public void afterPersist(Object item, FormData data, Object persistedObject)
    {
        // ignored
    }
}
