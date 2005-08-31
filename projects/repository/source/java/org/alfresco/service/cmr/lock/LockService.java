/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.service.cmr.lock;

import java.util.Collection;

import org.alfresco.service.cmr.repository.AspectMissingException;
import org.alfresco.service.cmr.repository.NodeRef;


/**
 * Interface for public and internal lock operations.
 * 
 * @author Roy Wetherall
 */
public interface LockService
{
   /**
    * Places a lock on a node.  
    * <p>
    * The lock prevents any other user or process from comitting updates 
    * to the node untill the lock is released.  
    * <p>
    * The user reference passed indicates who the owner of the lock is.
    * @param  nodeRef  a reference to a node 
    * @param  userName  a reference to the user that will own the lock
    * @param  lockType the lock type
    * @throws UnableToAquireLockException
    *                  thrown if the lock could not be obtained
    * @throws LockAspectMissing
    *                   thrown if the lock aspect is missing
    */
   public void lock(NodeRef nodeRef, String userName, LockType lockType)
       throws UnableToAquireLockException, AspectMissingException;
   
   /**
    * Places a lock on a node and optionally on all its children.  
    * <p>
    * The lock prevents any other user or process from comitting updates 
    * to the node untill the lock is released.  
    * <p>
    * The user reference passed indicates who the owner of the lock(s) is.  
    * If any one of the child locks can not be taken then an exception will 
    * be raised and all locks canceled.
    * @param nodeRef        a reference to a node
    * @param userName        a reference to the user that will own the lock(s)
    * @param lockType       the lock type 
    * @param lockChildren   if true indicates that all the children (and 
    *                       grandchildren, etc) of the node will also be locked, 
    *                       false otherwise
    * 
    * @throws UnableToAquireLockException
    *                        thrown if the lock could not be obtained
    * @throws LockAspectMissing
    *                        thrown if the lock aspect is missing
    */
   public void lock(NodeRef nodeRef, String userName, LockType lockType, boolean lockChildren)
       throws UnableToAquireLockException, AspectMissingException;
   
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
    * @param  userName  a reference to the user that will own the lock(s)
    * @param  lockType the type of lock being created
    * @throws UnableToAquireLockException
    *                  thrown if the lock could not be obtained
    * @throws LockAspectMissing
    *                   thrown if the lock aspect is missing
    */
   public void lock(Collection<NodeRef> nodeRefs, String userName, LockType lockType)
       throws UnableToAquireLockException, AspectMissingException;
   
   /**
    * Removes the lock on a node.  
    * <p>
    * The user must have sufficient permissions to remove the lock (ie: be the 
    * owner of the lock or have admin rights) otherwise an exception will be raised. 
    * 
    * @param  nodeRef  a reference to a node
    * @param  userName  the user reference
    * @throws UnableToReleaseLockException
    *                  thrown if the lock could not be released
    * @thrown AspectMissingException
    *                   thrown if the lock aspect is missing                 
    */
   public void unlock(NodeRef nodeRef, String userName)
       throws UnableToReleaseLockException, AspectMissingException;
   
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
    * @param  userName        the user reference
    * @param  lockChildren   if true then all the children (and grandchildren, etc) 
    *                        of the node will also be unlocked, false otherwise
    * @throws UnableToReleaseLockException
    *                  thrown if the lock could not be released
    * @thrown AspectMissingException
    *                   thrown if the lock aspect is missing
    */
   public void unlock(NodeRef nodeRef, String userName, boolean lockChildren)
       throws UnableToReleaseLockException, AspectMissingException;
   
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
    * @param  userName   the user reference
    * @throws UnableToReleaseLockException
    *                  thrown if the lock could not be released
    * @thrown AspectMissingException
    *                   thrown if the lock aspect is missing
    */
   public void unlock(Collection<NodeRef> nodeRefs, String userName)
       throws UnableToReleaseLockException, AspectMissingException;
   
   /**
    * Gets the lock status for the node reference relative to the current user.
    * 
    * @see LockService#getLockStatus(NodeRef, NodeRef)
    * 
    * @param nodeRef    the node reference
    * @return           the lock status
    * @throws AspectMissingException
    *                   thrown if the lock aspect is missing
    */
   public LockStatus getLockStatus(NodeRef nodeRef)
       throws AspectMissingException;
   
   /**
    * Indicates the current lock status for the user against the passed node.
    * <p>
    * Possible results are LOCKED (the node is locked by antoher user), NO_LOCK 
    * (the node is not locked) , LOCK_OWNER (the node is locked by the referenced
    * user).
    * 
    * TODO do we need LOCK_ADMINISTRATOR to indicate that the user does not own the
    *      lock but can administer it??
    * 
    * @param nodeRef    the node reference
    * @param userName    the user reference
    * @return           the status of the lock in relation to the user
    * @thrown AspectMissingException
    *                   thrown if the lock aspect is missing
    */
   public LockStatus getLockStatus(NodeRef nodeRef, String userName)
       throws AspectMissingException;
   
   /**
    * Gets the lock type for the node indicated.  
    * <p>
    * Returns null if the node is not locked.
    * <p>
    * Throws an exception if the node does not have the lock aspect.
    * 
    * @param  nodeRef  the node reference
    * @return          the lock type, null is returned if the object in question has no
    *                  lock
    * @throws AspectMissingException
    *                  thrown if the lock aspect is missing
    */
   public LockType getLockType(NodeRef nodeRef)
       throws AspectMissingException;
   
   /**
    * Checks to see if the node is locked or not.  Gets the user reference from the current 
    * session.
    * <p>
    * Throws a NodeLockedException based on the lock status of the lock, the user ref and the
    * lock type.
    * 
    * @param nodeRef   the node reference
    * @throws NodeLockedException
    *                  thrown if the node is determined to be locked based on the user ref and lock 
    *                  type
    */
   public void checkForLock(NodeRef nodeRef)
   	  throws NodeLockedException;
   
   /**
    * Checks to see if the node is locked or not.
    * <p>
    * Throws a NodeLockedException based on the lock status of the lock, the user ref and the
    * lock type.
    * 
    * @param nodeRef   the node reference
    * @param userName   the user reference
    * @throws NodeLockedException
    *                  thrown if the node is determined to be locked based on the user ref and lock 
    *                  type
    */
   public void checkForLockWithUser(NodeRef nodeRef, String userName)
       throws NodeLockedException;
}
