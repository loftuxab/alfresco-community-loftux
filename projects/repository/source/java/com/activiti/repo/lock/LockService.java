/*
 * Created on Mar 24, 2005
 *
 * TODO put licence header here
 */
package com.activiti.repo.lock;

import java.util.Collection;
import com.activiti.repo.ref.NodeRef;


/**
 * Interface for public and internal lock operations.
 * 
 * @author Roy Wetherall
 */
public interface LockService
{
   /**
    * Places a lock on a node.  
    * 
    * The lock prevents any other user or process from comitting updates 
    * to the node untill the lock is released.  
    * 
    * The user reference passed indicates who the owner of the lock is.
    * 
    * @param nodeRef a reference to a node 
    * @param userRef a reference to the user that will own the lock
    * 
    * TODO: how do we pass a user reference??
    */
   public void lock(NodeRef nodeRef, String userRef);
   
   /**
    * Places a lock on a node and optionally on all its children.  
    * 
    * The lock prevents any other user or process from comitting updates 
    * to the node untill the lock is released.  
    * 
    * The user reference passed indicates who the owner of the lock(s) is.  
    * If any one of the child locks can not be taken then an exception will 
    * be raised and all locks canceled.
    * 
    * @param nodeRef a reference to a node
    * @param userRef a reference to the user that will own the lock(s)
    * @param lockChildren if true indicates that all the children (and 
    * grandchildren, etc) of the node will also be locked, false otherwise
    * 
    * TODO: how do we pass a user reference ??
    */
   public void lock(NodeRef nodeRef, String userRef, boolean lockChildren);
   
   /**
    * Places a lock on all the nodes referenced in the passed list.  
    * 
    * The lock prevents any other user or process from comitting updates 
    * to the node untill the lock is released.  
    * 
    * The user reference passed indicates who the owner of the lock(s) is.  
    * If any one of the child locks can not be taken then an exception will 
    * be raised and all locks canceled.
    *  
    * @param nodeRefs a list of node references
    * @param userRef a reference to the user that will own the lock(s)
    */
   public void lock(Collection<NodeRef> nodeRefs, String userRef);
   
   /**
    * Removes the lock on a node.  
    * 
    * You must have sufficient permissions to remove the lock (ie: be the 
    * owner of the lock or have admin rights) otherwise an exception will be raised. 
    * 
    * @param nodeRef a reference to a node
    */
   public void unlock(NodeRef nodeRef);
   
   /**
    * Removes the lock on a node and optional on its children.  
    * 
    * You must have sufficient permissions to remove the lock(s) (ie: be 
    * the owner of the lock(s) or have admin rights) otherwise an exception 
    * will be raised.
    * 
    * If one of the child nodes is not locked then it will be ignored and 
    * the process continue without error.  
    * 
    * If the lock on any one of the child nodes cannot be released then an 
    * exception will be raised.
    * 
    * @param nodeRef a node reference
    * @param lockChildren if true then all the children (and grandchildren, etc) 
    * of the node will also be unlocked, false otherwise
    */
   public void unlock(NodeRef nodeRef, boolean lockChildren);
   
   /**
    * Indicates whether a node is currently locked.
    * 
    * @param nodeRef a node reference
    * @return true is the node is locked, false otherwise
    */
   public boolean isLocked(NodeRef nodeRef);
   
   // TODO do we need a getLockData method to get the data about a lock ??
}
