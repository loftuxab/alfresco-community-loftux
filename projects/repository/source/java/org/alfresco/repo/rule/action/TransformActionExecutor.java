/**
 * Created on Jun 15, 2005
 */
package org.alfresco.repo.rule.action;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.rule.common.ParameterDefinitionImpl;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NoTransformerException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.namespace.QName;

/**
 * @author Roy Wetherall
 */
public class TransformActionExecutor extends RuleActionExecutorAbstractBase 
{
	public static final String NAME = "transform";
	public static final String PARAM_MIME_TYPE = "mime-type";
	public static final String PARAM_DESTINATION_FOLDER = "destination-folder";
    public static final String PARAM_ASSOC_TYPE_QNAME = "assoc-type";
    public static final String PARAM_ASSOC_QNAME = "assoc-name";
	
	private DictionaryService dictionaryService;
	private NodeService nodeService;
	private ContentService contentService;
	private CopyService copyService;
    private MimetypeService mimetypeService;
    

    public void setMimetypeService(MimetypeService mimetypeService) 
    {
        this.mimetypeService = mimetypeService;
    }
    
    public void setNodeService(NodeService nodeService) 
	{
		this.nodeService = nodeService;
	}
	
	public void setDictionaryService(DictionaryService dictionaryService) 
	{
		this.dictionaryService = dictionaryService;
	}
	
	public void setContentService(ContentService contentService) 
	{
		this.contentService = contentService;
	}
	
	public void setCopyService(CopyService copyService) 
	{
		this.copyService = copyService;
	}
	

	@Override
	protected void addParameterDefintions(List<ParameterDefinition> paramList) 
	{
		paramList.add(new ParameterDefinitionImpl(PARAM_MIME_TYPE, ParameterType.STRING, true, getParamDisplayLabel(PARAM_MIME_TYPE)));
		paramList.add(new ParameterDefinitionImpl(PARAM_DESTINATION_FOLDER, ParameterType.NODE_REF, true, getParamDisplayLabel(PARAM_DESTINATION_FOLDER)));
		paramList.add(new ParameterDefinitionImpl(PARAM_ASSOC_TYPE_QNAME, ParameterType.QNAME, true, getParamDisplayLabel(PARAM_ASSOC_TYPE_QNAME)));
		paramList.add(new ParameterDefinitionImpl(PARAM_ASSOC_QNAME, ParameterType.QNAME, true, getParamDisplayLabel(PARAM_ASSOC_QNAME)));
	}

	/**
	 * @see org.alfresco.repo.rule.action.RuleActionExecutorAbstractBase#executeImpl(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	protected void executeImpl(
			RuleAction ruleAction,
			NodeRef actionableNodeRef,
			NodeRef actionedUponNodeRef) 
	{
		if (this.nodeService.exists(actionedUponNodeRef) == true)
		{
			// First check that the node is a sub-type of content
			QName typeQName = this.nodeService.getType(actionedUponNodeRef);
			if (this.dictionaryService.isSubClass(typeQName, ContentModel.TYPE_CONTENT) == true)
			{
				// Get the mime type
				String mimeType = (String)ruleAction.getParameterValue(PARAM_MIME_TYPE);
				
				// Get the details of the copy destination
				NodeRef destinationParent = (NodeRef)ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER);
		        QName destinationAssocTypeQName = (QName)ruleAction.getParameterValue(PARAM_ASSOC_TYPE_QNAME);
		        QName destinationAssocQName = (QName)ruleAction.getParameterValue(PARAM_ASSOC_QNAME);
		        
				// Copy the content node
		        NodeRef copyNodeRef = this.copyService.copy(
		                actionedUponNodeRef, 
		                destinationParent,
		                destinationAssocTypeQName,
		                destinationAssocQName,
		                false);
				
				// Set the mime type on the copy
				this.nodeService.setProperty(copyNodeRef, ContentModel.PROP_MIME_TYPE, mimeType);
				
                // Adjust the name of the copy
                String originalMimetype = (String)nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_MIME_TYPE);
                String originalName = (String)nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME);
                String newName = transformName(originalName, originalMimetype, mimeType);
                nodeService.setProperty(copyNodeRef, ContentModel.PROP_NAME, newName);
                String originalTitle = (String)nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_TITLE);
                if (originalTitle != null && originalTitle.length() > 0)
                {
                    String newTitle = transformName(originalTitle, originalMimetype, mimeType);
                    nodeService.setProperty(copyNodeRef, ContentModel.PROP_TITLE, newTitle);
                }
                
				// Get the content reader and writer
				ContentReader contentReader = this.contentService.getReader(actionedUponNodeRef);
				ContentWriter contentWriter = this.contentService.getUpdatingWriter(copyNodeRef);
				
				// Try and transform the content
                try
                {
				    this.contentService.transform(contentReader, contentWriter);
                }
                catch(NoTransformerException e)
                {
                    // TODO: Revisit this for alternative solutions
                    nodeService.deleteNode(copyNodeRef);
                }
			}	
		}
	}
    

    /**
     * Transform name from original extension to new extension
     * 
     * @param original
     * @param originalMimetype
     * @param newMimetype
     * @return
     */
    private String transformName(String original, String originalMimetype, String newMimetype)
    {
        String transformed = original;
        String originalExtension = mimetypeService.getExtension(originalMimetype);
        String newExtension = mimetypeService.getExtension(newMimetype);
        int ext = original.lastIndexOf(originalExtension);
        if (ext != -1)
        {
            transformed = original.substring(0, ext) + newExtension;
        }
        return transformed;
    }
    
}
