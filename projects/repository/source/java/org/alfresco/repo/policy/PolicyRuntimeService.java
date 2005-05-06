/**
 * Created on Apr 27, 2005
 */
package org.alfresco.repo.policy;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;

/**
 * Policy Runtime Service Interface
 * 
 * @author Roy Wetherall
 */
public interface PolicyRuntimeService
{

    /**
     * Registers a behaviour for a policy against a qname (a class, property or 
     * association identifier)
     * 
     * @param <T>           the policy interface type
     * @param policy        the policy interface class
     * @param policyImpl    the policy implementation
     * @param qname         the qname of the associated model entity
     */
    public <T> void registerBehaviour(Class<T> policy, T policyImpl, QName qname);
    
    /**
     * Registers a policy behaviour against a class.
     * 
     * @param <T>           the policy interface
     * @param policy        the policy interface class
     * @param policyImpl    the policy implementation
     * @param classRef      the class reference
     */
    public <T> void registerClassBehaviour(Class<T> policy, T policyImpl, ClassRef classRef);
    
    /**
     * Retrieves the behaviour for a policy against a given model eneity qname.
     * 
     * @param <T>           the policy interface type
     * @param policy        the policy interface type
     * @param qname         the qname
     * @return              the behaviour object
     */
    public <T> T getBehaviour(Class<T> policy, QName qname);
    
    /**
     * Retrieves the behaviour for a policy against a given class.  This will 
     * include any behaviours registered against the sub-types.
     * 
     * @param <T>           the policy interface type
     * @param policy        the policy interface type
     * @param classRef      the class reference
     * @return              the behaviour object
     */
    public <T> T getClassBehaviour(Class<T> policy, ClassRef classRef);
    
    /**
     * Retrieves the bahavour for a policy against a given node reference.  This
     * will include behaviours registered against the type and sub-types of the 
     * node and against all the nodes aspects.
     * 
     * @param <T>           the policy interface type
     * @param policy        the policy interface type
     * @param nodeService   the node service
     * @param nodeRef       the node reference
     * @return              the behaviour object
     */
    public <T> T getClassBehaviour(Class<T> policy, NodeService nodeService, NodeRef nodeRef);
      
}
