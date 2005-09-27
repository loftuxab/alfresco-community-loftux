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
    * <p>
    * A lock made with this call will never expire.
    * 
    * @param  nodeRef  a reference to a node 
    * @param  userName  a reference to the user that will own the lock
    * @param  lockType the lock type
    * @throws UnableToAquireLockException
    *                  thrown if the lock could not be obtained
    */
   public void lock(NodeRef nodeRef, String userName, LockType lockType)
       throws UnableToAquireLockException;
   
   /**
    * Places a lock on a node.  
    * <p>
    * The lock prevents any other user or process from comitting updates 
    * to the node untill the lock is released.  
    * <p>
    * The user reference passed indicates who the owner of the lock is.
    * <p>
    * If the time to expire is 0 then the lock will never expire.  Otherwise the
    * timeToExpire indicates the number of seconds before the lock expires.  When
    * a lock expires the lock is considered to have been released.
    * <p>
    * If the node is already locked and the user is the lock owner then the lock will
    * be renewed with the passed timeToExpire.
    * 
    * @param  nodeRef       a reference to a node 
    * @param  userName      a reference to the user that will own the lock
    * @param  lockType      the lock type
    * @param  timeToExpire  the number of seconds before the locks expires.
    * @throws UnableToAquireLockException
    *                       thrown if the lock could not be obtained
    */
   public void lock(NodeRef nodeRef, String userName, LockType lockType, int timeToExpire)
       throws UnableToAquireLockException;
   
   /**
    * Places a lock on a node and optionally on all its children.  
    * <p>
    * The lock prevents any other user or process from comitting updates 
    * to the node untill the lock is released.  
    * <p>
    * The user reference passed indicates who the owner of the lock(s) is.  
    * If any one of the child locks can not be taken then an exception will 
    * be raised and all locks canceled.
    * <p>
    * If the time to expire is 0 then the lock will never expire.  Otherwise the
    * timeToExpire indicates the number of seconds before the lock expires.  When
    * a lock expires the lock is considered to have been released.
    * <p>
    * If the node is already locked and the user is the lock owner then the lock will
    * be renewed with the passed timeToExpire.
    * 
    * @param nodeRef            a reference to a node
    * @param userName           a reference to the user that will own the lock(s)
    * @param lockType           the lock type 
    * @param timeToExpire       the number of seconds before the locks expires.
    * @param lockChildren       if true indicates that all the children (and 
    *                           grandchildren, etc) of the node will also be locked, 
    *                           false otherwise
    * 
    * @throws UnableToAquireLockException
    *                           thrown if the lock could not be obtained
    */
   public void lock(NodeRef nodeRef, String userName, LockType lockType, int timeToExpire, boolean lockChildren)
       throws UnableToAquireLockException;
   
   /**
    * Places a lock on all the nodes referenced in the passed list.  
    * <p>
    * The lock prevents any other user or process from comitting updates 
    * to the node untill the lock is released.  
    * <p>
    * The user reference passed indicates who the owner of the lock(s) is.  
    * If any one of the child locks can not be taken then an exception will 
    * be raised and all locks canceled.
    * <p>
    * If the time to expire is 0 then the lock will never expire.  Otherwise the
    * timeToExpire indicates the number of seconds before the lock expires.  When
    * a lock expires the lock is considered to have been released.
    * <p>
    * If the node is already locked and the user is the lock owner then the lock will
    * be renewed with the passed timeToExpire.
    * 
    * @param  nodeRefs          a list of node references
    * @param  userName          a reference to the user that will own the lock(s)
    * @param  lockType          the type of lock being created
    * @param  timeToExpire      the number of seconds before the locks expires.
    * @throws UnableToAquireLockException
    *                           thrown if the lock could not be obtained
    */
   public void lock(Collection<NodeRef> nodeRefs, String userName, LockType lockType, int timeToExpire)
       throws UnableToAquireLockException;
   
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
    */
   public void unlock(NodeRef nodeRef, String userName)
       throws UnableToReleaseLockException;
   
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
    */
   public void unlock(NodeRef nodeRef, String userName, boolean lockChildren)
       throws UnableToReleaseLockException;
   
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
    */
   public void unlock(Collection<NodeRef> nodeRefs, String userName)
       throws UnableToReleaseLockException;
   
   /**
    * Gets the lock status for the node reference relative to the current user.
    * 
    * @see LockService#getLockStatus(NodeRef, NodeRef)
    * 
    * @param nodeRef    the node reference
    * @return           the lock status
    */
   public LockStatus getLockStatus(NodeRef nodeRef);
   
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
    */
   public LockStatus getLockStatus(NodeRef nodeRef, String userName);
   
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
    */
   public LockType getLockType(NodeRef nodeRef);
   
   /**
    * Checks to see if the node is locked or not.  Gets the user reference from the current 
    * session.
    * <p>
    * Throws a NodeLockedException based on the lock status of the lock, the user ref and the
    * lock type.
    * 
    * @param nodeRef   the node reference
    */
   public void checkForLock(NodeRef nodeRef);
   
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
