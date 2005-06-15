/**
 * Created on Jun 15, 2005
 */
package org.alfresco.repo.rule.action;

import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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

	/**
	 * @param ruleAction
	 * @param serviceRegistry
	 */
	public TransformActionExecutor(
			RuleAction ruleAction,
			ServiceRegistry serviceRegistry) 
	{
		super(ruleAction, serviceRegistry);
		
		this.dictionaryService = serviceRegistry.getDictionaryService();
		this.nodeService = serviceRegistry.getNodeService();
		this.contentService = serviceRegistry.getContentService();		
		this.copyService = serviceRegistry.getCopyService();
	}

	/**
	 * @see org.alfresco.repo.rule.action.RuleActionExecutorAbstractBase#executeImpl(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	protected void executeImpl(
			NodeRef actionableNodeRef,
			NodeRef actionedUponNodeRef) 
	{
		// First check that the node is a sub-type of content
		QName typeQName = this.nodeService.getType(actionedUponNodeRef);
		if (this.dictionaryService.isSubClass(typeQName, DictionaryBootstrap.TYPE_QNAME_CONTENT) == true)
		{
			// Get the mime type
			String mimeType = (String)this.ruleAction.getParameterValue(PARAM_MIME_TYPE);
			
			// Get the details of the copy destination
			NodeRef destinationParent = (NodeRef)this.ruleAction.getParameterValue(PARAM_DESTINATION_FOLDER);
	        QName destinationAssocTypeQName = (QName)this.ruleAction.getParameterValue(PARAM_ASSOC_TYPE_QNAME);
	        QName destinationAssocQName = (QName)this.ruleAction.getParameterValue(PARAM_ASSOC_QNAME);
	        
			// Copy the content node
	        NodeRef copyNodeRef = this.copyService.copy(
	                actionedUponNodeRef, 
	                destinationParent,
	                destinationAssocTypeQName,
	                destinationAssocQName,
	                false);
			
			// Set the mime type on the copy
			this.nodeService.setProperty(copyNodeRef, DictionaryBootstrap.PROP_QNAME_MIME_TYPE, mimeType);
			
			// Get the content reader and writer
			ContentReader contentReader = this.contentService.getReader(actionedUponNodeRef);
			ContentWriter contentWriter = this.contentService.getUpdatingWriter(copyNodeRef);
			
			// Try and transform the content
			this.contentService.transform(contentReader, contentWriter);
		}		
	}

}
