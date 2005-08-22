package org.alfresco.repo.action;

import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Class containing behaviour for the auditable aspect
 * 
 * @author Roy Wetherall
 */
public class ActionableAspect
{
	public static final QName ASSOC_NAME_SAVEDACTIONFOLDER = QName.createQName(ContentModel.ACTION_MODEL_URI, "savedActionFolder");
	public static final QName ASSOC_NAME_SAVEDRULESFOLDER = QName.createQName(ContentModel.ACTION_MODEL_URI, "savedRuleFolder");
	
	private Behaviour onAddAspectBehaviour;
	
	private PolicyComponent policyComponent;
	
	private NodeService nodeService;
	
	public void setPolicyComponent(PolicyComponent policyComponent)
	{
		this.policyComponent = policyComponent;
	}
	
	public void setNodeService(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}
	
	public void init()
	{
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCopyNode"),
				ContentModel.ASPECT_ACTIONABLE,
				new JavaBehaviour(this, "onCopyNode"));
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCopyComplete"),
				ContentModel.ASPECT_ACTIONABLE,
				new JavaBehaviour(this, "onCopyComplete"));
		
		this.onAddAspectBehaviour = new JavaBehaviour(this, "onAddAspect");
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"), 
				this, 
				onAddAspectBehaviour);
	}
	
	/**
	 * On add aspect policy behaviour
	 * @param nodeRef
	 * @param aspectTypeQName
	 */
	public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
	{
		if (ContentModel.ASPECT_ACTIONABLE.equals(aspectTypeQName) == true)
		{
			List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(nodeRef, ASSOC_NAME_SAVEDACTIONFOLDER);
			if (assocs.size() == 0)
			{
				// Create the saved action folder used by this service
				this.nodeService.createNode(nodeRef,
						ContentModel.ASSOC_SAVED_ACTION_FOLDERS,
					 	ASSOC_NAME_SAVEDACTIONFOLDER,
					 	ContentModel.TYPE_SAVED_ACTION_FOLDER);
			}
			
			List<ChildAssociationRef> assocs2 = this.nodeService.getChildAssocs(nodeRef, ASSOC_NAME_SAVEDRULESFOLDER);
			if (assocs2.size() == 0)
			{
				// Create the saved action folder used by this service
				this.nodeService.createNode(nodeRef,
						ContentModel.ASSOC_SAVED_ACTION_FOLDERS,
						ASSOC_NAME_SAVEDRULESFOLDER,
					 	ContentModel.TYPE_SAVED_ACTION_FOLDER);
			}
		}
	}
	
	public void onCopyNode(
			QName classRef,
			NodeRef sourceNodeRef,
            StoreRef destinationStoreRef,
            boolean copyToNewNode,
			PolicyScope copyDetails)
	{
		copyDetails.addAspect(ContentModel.ASPECT_ACTIONABLE);
		
		List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(sourceNodeRef);
		for (ChildAssociationRef assoc : assocs)
		{
			if (assoc.getTypeQName().equals(ContentModel.ASSOC_SAVED_ACTION_FOLDERS) == true)
			{
				copyDetails.addChildAssociation(classRef, assoc, true);
			}
		}
		
		this.onAddAspectBehaviour.disable();
	}
	
	public void onCopyComplete(
			QName classRef,
			NodeRef sourceNodeRef,
			NodeRef destinationRef,
			Map<NodeRef, NodeRef> copyMap)
	{
		this.onAddAspectBehaviour.enable();
	}
}
