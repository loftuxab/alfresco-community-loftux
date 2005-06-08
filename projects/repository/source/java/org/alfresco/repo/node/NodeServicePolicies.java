/**
 * Created on May 17, 2005
 */
package org.alfresco.repo.node;

import org.alfresco.repo.policy.AssociationPolicy;
import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;

/**
 * Node service policies
 * 
 * @author Roy Wetherall
 */
public interface NodeServicePolicies 
{
    public interface BeforeCreateStorePolicy extends ClassPolicy
    {
        /**
         * Called before a new node store is created.
         * 
         * @param nodeTypeQName the type of the node that will be used for the root
         * @param storeRef the reference to the store about to be created
         */
        public void beforeCreateStore(QName nodeTypeQName, StoreRef storeRef);
    }
    
    public interface OnCreateStorePolicy extends ClassPolicy
    {
        /**
         * Called when a new node store has been created.
         * 
         * @param rootNodeRef the reference to the newly created root node
         */
        public void onCreateStore(NodeRef rootNodeRef);
    }

    public interface BeforeCreateNodePolicy extends ClassPolicy
    {
        /**
         * Called before a new node is created.
         * 
         * @param parentRef         the parent node reference
         * @param assocTypeQName    the association type qualified name
         * @param assocQName        the association qualified name
         * @param nodeTypeQName     the node type qualified name
         */
        public void beforeCreateNode(
                        NodeRef parentRef,
                        QName assocTypeQName,
                        QName assocQName,
                        QName nodeTypeQName);
    }
    
    public interface OnCreateNodePolicy extends ClassPolicy
    {
        /**
         * Called when a new node has been created.
         * 
         * @param childAssocRef  the created child association reference
         */
        public void onCreateNode(ChildAssocRef childAssocRef);
    }

    public interface BeforeUpdateNodePolicy extends ClassPolicy
    {
        /**
         * Called before a node is updated.  This includes the modification of properties, child and target 
         * associations and the addition of aspects.
         * 
         * @param nodeRef  reference to the node being updated
         */
        public void beforeUpdateNode(NodeRef nodeRef);
    }
    
	public interface OnUpdateNodePolicy extends ClassPolicy
	{
		/**
		 * Called after a new node has been created.  This includes the modification of properties, child and target
		 * associations and the addition of aspects.
		 * 
		 * @param nodeRef  reference to the updated node
		 */
		public void onUpdateNode(NodeRef nodeRef);
	}
	
	public interface BeforeDeleteNodePolicy extends ClassPolicy
	{
		/**
		 * Called before a node is deleted.
		 * 
		 * @param nodeRef   the node reference
		 */
		public void beforeDeleteNodePolicy(NodeRef nodeRef);
	}
	
	public interface OnDeleteNodePolicy extends ClassPolicy
	{
		/**
		 * Called after a node is deleted.  The reference given is for an association
         * which has been deleted and cannot be used to retrieve node or associaton
         * information from any of the services.
		 * 
         * @param childAssocRef the primary parent-child association of the deleted node
		 */
		public void onDeleteNode(ChildAssocRef childAssocRef);
	}
	
	// TODO
	// onAddAspect
	// onRemoveAspect
    
    public interface OnCreateChildAssociationPolicy extends AssociationPolicy
    {
        /**
         * Called after a node child association has been created.
         * 
         * @param childAssocRef     the created child association reference
         */
        public void onCreateChildAssociation(ChildAssocRef childAssocRef);
    }
    
    public interface OnDeleteChildAssociationPolicy extends AssociationPolicy
    {
        /**
         * Called after a node child association has been deleted.
         * 
         * @param childAssocRef the child association that has been deleted
         */
        public void onDeleteChildAssociation(ChildAssocRef childAssocRef);
    }
}
