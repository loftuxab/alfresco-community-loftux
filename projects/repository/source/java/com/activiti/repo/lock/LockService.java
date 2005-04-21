/*
 * Created on Mar 24, 2005
 *
 * TODO put licence header here
 */
package com.activiti.repo.lock;

import java.util.Collection;

import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.QName;


/**
 * Interface for public and internal lock operations.
 * 
 * @author Roy Wetherall
 */
public interface LockService
{
    /**
     * Lock namespace
     */
    public final static String NAMESPACE_LOCK = "http://www.activiti.com/lock/1.0";
    
    /**
     * Lock aspect QName and ClassRef
     */
    public final static QName ASPECT_QNAME_LOCK = QName.createQName(NAMESPACE_LOCK, "lock");
    public final static ClassRef ASPECT_CLASS_REF_LOCK = new ClassRef(ASPECT_QNAME_LOCK);
    
    /**
     * Lock aspect attribute names
     */
    public final static QName ATT_LOCK_OWNER = QName.createQName(NAMESPACE_LOCK, "lockOwner");
    
    /**
     * Type-safe enum used to indicate the lock status
     * TODO details of the various lock status and what they mean
     */
    public enum LockStatus {UNLOCKED, LOCKED, LOCK_OWNER};
    
   /**
    * Places a lock on a node.  
    * <p>
    * The lock prevents any other user or process from comitting updates 
    * to the node untill the lock is released.  
    * <p>
    * The user reference passed indicates who the owner of the lock is.
    * 
    * @param  nodeRef  a reference to a node 
    * @param  userRef  a reference to the user that will own the lock
    * @throws NodeAlreadyLockedException
    *                  the node is already locked by another user
    */
   public void lock(NodeRef nodeRef, String userRef)
       throws NodeAlreadyLockedException;
   
   /**
    * Places a lock on a node and optionally on all its children.  
    * <p>
    * The lock prevents any other user or process from comitting updates 
    * to the node untill the lock is released.  
    * <p>
    * The user reference passed indicates who the owner of the lock(s) is.  
    * If any one of the child locks can not be taken then an exception will 
    * be raised and all locks canceled.
    * 
    * @param  nodeRef        a reference to a node
    * @param  userRef        a reference to the user that will own the lock(s)
    * @param  lockChildren   if true indicates that all the children (and 
    *                        grandchildren, etc) of the node will also be locked, 
    *                        false otherwise
    * @throws NodeAlreadyLockedException
    *                        the node is already locked by another user
    */
   public void lock(NodeRef nodeRef, String userRef, boolean lockChildren)
       throws NodeAlreadyLockedException;
   
   /**
    * Places a lock on all the nodes referenced in the passed list.  
    * <p>
    * The lock prevents any other user or process from comitting updates 
    * to the node untill the lock is released.  
    * <p>
    * The user reference passed indicates who the owner of the lock(s) is.  
    * If any one of the child locks can not be taken then an exception will 
    * be raised and all locks canceled.
    *  
    * @param  nodeRefs a list of node references
    * @param  userRef  a reference to the user that will own the lock(s)
    * @throws NodeAlreadyLockedException
    *                  the node is already locked by another user
    */
   public void lock(Collection<NodeRef> nodeRefs, String userRef)
       throws NodeAlreadyLockedException;
   
   /**
    * Removes the lock on a node.  
    * <p>
    * The user must have sufficient permissions to remove the lock (ie: be the 
    * owner of the lock or have admin rights) otherwise an exception will be raised. 
    * 
    * @param  nodeRef  a reference to a node
    * @param  userRef  the user reference
    * @throws InsufficientPrivelegesToRealeseLockException
    *                  if the lock can not be released because the user is not the
    *                  lock owner
    */
   public void unlock(NodeRef nodeRef, String userRef)
       throws InsufficientPrivelegesToRealeseLockException;
   
   /**
    * Removes the lock on a node and optional on its children.  
    * <p>
    * The user must have sufficient permissions to remove the lock(s) (ie: be 
    * the owner of the lock(s) or have admin rights) otherwise an exception 
    * will be raised.
    * <p>
    * If one of the child nodes is not locked then it will be ignored and 
    * the process continue without error.  
    * <p>
    * If the lock on any one of the child nodes cannot be released then an 
    * exception will be raised.
    * 
    * @param  nodeRef        a node reference
    * @param  userRef        the user reference
    * @param  lockChildren   if true then all the children (and grandchildren, etc) 
    *                        of the node will also be unlocked, false otherwise
    * @throws InsufficientPrivelegesToRealeseLockException
    *                        if the lock can not be released because the user is not the
    *                        lock owner
    */
   public void unlock(NodeRef nodeRef, String userRef, boolean lockChildren)
       throws InsufficientPrivelegesToRealeseLockException;
   
   /**
    * Removes a lock on the nodes provided.
    * <p>
    * The user must have sufficient permissions to remove the locks (ie: be 
    * the owner of the locks or have admin rights) otherwise an exception 
    * will be raised.
    * <p>
    * If one of the nodes is not locked then it will be ignored and the
    * process will continue without an error.
    * <p>
    * If the lock on any one of the nodes cannot be released than an exception 
    * will be raised and the process rolled back.
    * 
    * @param  nodeRefs  the node references
    * @param  userRef   the user reference
    * @throws InsufficientPrivelegesToRealeseLockException
    *                   if the lock can not be released because the user is not the
    *                   lock owner
    */
   public void unlock(Collection<NodeRef> nodeRefs, String userRef)
       throws InsufficientPrivelegesToRealeseLockException;
   
   /**
    * Indicates the current lock status for the user against the passed node.
    * <p>
    * Possible results are LOCKED (the node is locked by antoher user), UNLOCKED 
    * (the node is not locked) , LOCK_OWNER (the node is locked by the referenced
    * user).
    * 
    * TODO do we need LOCK_ADMINISTRATOR to indicate that the user does not own the
    *      lock but can administer it??
    * 
    * @param nodeRef    the node reference
    * @param userRef    the user reference
    * @return           the status of the lock in relation to the user
    */
   public LockStatus getLockStatus(NodeRef nodeRef, String userRef);
}
