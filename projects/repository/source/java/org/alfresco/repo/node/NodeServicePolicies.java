/**
 * Created on May 17, 2005
 */
package org.alfresco.repo.node;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.policy.AssociationPolicy;
import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;

/**
 * Node service policies
 * 
 * @author Roy Wetherall
 */
public interface NodeServicePolicies 
{
	/**
	 * BeforeCreate policy interface
	 */
	public interface BeforeCreatePolicy extends ClassPolicy
	{
		/**
		 * Policy meta data
		 */
		static final String NAMESPACE = NamespaceService.ALFRESCO_URI;
				
		/**
		 * Called before a new node is created.
		 * 
		 * @param parentRef			the parent node reference
		 * @param assocTypeQName	the association type qualified name
		 * @param assocQName		the association qualified name
		 * @param nodeTypeQName		the node type qualified name
		 */
		public void beforeCreate(
						NodeRef parentRef,
						QName assocTypeQName,
						QName assocQName,
						QName nodeTypeQName);
	}
	
	/**
	 * OnCreate policy interface
	 */
	public interface OnCreatePolicy extends ClassPolicy
	{
		/**
		 * Policy meta data
		 */
		static final String NAMESPACE = NamespaceService.ALFRESCO_URI;
				
		/**
		 * Called when a new node has been created.
		 * 
		 * @param childAssocRef  the created child association reference
		 */
		public void onCreate(ChildAssocRef childAssocRef);
	}
	
	/**
	 * OnUpdate policy interface
	 */
	public interface OnUpdatePolicy extends ClassPolicy
	{
		/**
		 * Policy meta data
		 */
		static final String NAMESPACE = NamespaceService.ALFRESCO_URI;

		/**
		 * Called after a new node has been created.  This includes the modification of properties, child and target
		 * associations and the addition of aspects.
		 * 
		 * @param nodeRef  reference to the updated node
		 */
		public void onUpdate(NodeRef nodeRef);
	}
	
	/**
	 * BeforeDelete policy interface
	 */
	public interface BeforeDeletePolicy extends ClassPolicy
	{
		/**
		 * Policy meta data
		 */
		static final String NAMESPACE = NamespaceService.ALFRESCO_URI;

		/**
		 * Called before a node is deleted.
		 * 
		 * @param nodeRef   the node reference
		 */
		public void beforeDeletePolicy(NodeRef nodeRef);
	}
	
	/**
	 * OnDelete policy interface
	 */
	public interface OnDeletePolicy extends ClassPolicy
	{
		/**
		 * Policy meta data
		 */
		static final String NAMESPACE = NamespaceService.ALFRESCO_URI;
		
		/**
		 * Called after a node is deleted.  Note that the passed node reference
		 * relates to the deleted node and can not be used by the node service.
		 * 
		 * @param classRef	the class ref of the delete node
		 * @param nodeRef	the reference to the deleted node
		 */
		public void onDelete(QName classRef, NodeRef nodeRef);
	}
	
	// TODO
	// onAddAspect
	// onRemoveAspect
    
    /**
     * OnCreateChildAssociationPolicy interface
     */
    public interface OnCreateChildAssociationPolicy extends AssociationPolicy
    {
        /**
         * Policy meta data
         */
        static final String NAMESPACE = NamespaceService.ALFRESCO_URI;
        
        /**
         * Called after a node child association has been created.
         * 
         * @param childAssocRef     the created child association reference
         */
        public void onCreateChildAssociation(ChildAssocRef childAssocRef);
    }
}
