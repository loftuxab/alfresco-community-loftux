/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using WcfCmisWSTests.CmisServices;
using System.ServiceModel;


namespace WcfCmisWSTests
{
    ///
    /// author: Stas Sokolovsky
    ///
    class ACLServiceClientTest : BaseServiceClientTest
    {
        private string documentId;

        public void initialize(string testname)
        {
            documentId = createAndAssertObject(false, getAndAssertRootFolder(), getAndAssertDocumentTypeId()).ObjectId;
        }

        public void release(string testname)
        {
            deleteAndAssertObject(documentId);
        }

        public void testReadPermission()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            cmisAccessControlListType acList = createSimpleACL(aclPrincipalId, PERMISSION_READ);
            logger.log("[ACLService->applyACL]");
            aclServiceClient.applyACL(getAndAssertRepositoryId(), documentId, acList, null, getACLPropagation(), null);

            getPropertiesUsingCredentials(documentId, aclUsername, aclPassword);
                        
        }

        public void testWritePermission()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            cmisAccessControlListType acList = createSimpleACL(aclPrincipalId, PERMISSION_WRITE);
            logger.log("[ACLService->applyACL]");
            aclServiceClient.applyACL(getAndAssertRepositoryId(), documentId, acList, null, getACLPropagation(), null);

            updatePropertiesUsingCredentials(documentId, aclUsername, aclPassword);                       
        }

        public void testPermissionPropagation()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }
            if (!enumACLPropagation.propagate.Equals(getACLPropagation()))
            {
                Assert.Skip("ACL Propagation is not supported.");
            }

            string folderId = createAndAssertFolder(getAndAssertRootFolder()).ObjectId;
            string documentId = createAndAssertObject(folderId, null).ObjectId;

            cmisAccessControlListType acList = createSimpleACL(aclPrincipalId, PERMISSION_READ);
            logger.log("[ACLService->applyACL]");
            aclServiceClient.applyACL(getAndAssertRepositoryId(), folderId, acList, null, getACLPropagation(), null);

            getPropertiesUsingCredentials(documentId, aclUsername, aclPassword);

            deleteAndAssertObject(documentId);
            deleteAndAssertObject(folderId);
        }

        public void testGetACEs()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            cmisAccessControlListType acList = createSimpleACL(aclPrincipalId, PERMISSION_READ);
            logger.log("[ACLService->applyACL]");
            aclServiceClient.applyACL(getAndAssertRepositoryId(), documentId, acList, null, getACLPropagation(), null);

            logger.log("[ACLService->getACL]");
            cmisACLType aclType = aclServiceClient.getACL(getAndAssertRepositoryId(), documentId, true, null);
            Assert.IsTrue(aclType != null && aclType.acl != null && aclType.acl.permission != null, "No ACE were returned");
            bool contains = false;
            foreach (cmisAccessControlEntryType receivedAce in aclType.acl.permission)
            {
                Assert.IsTrue(receivedAce != null && receivedAce.permission != null && receivedAce.principal != null, "Incorrect ACE was returned");
                if (receivedAce.principal.principalId != null && receivedAce.principal.principalId.Equals(aclPrincipalId))
                {                    
                    foreach (String permission in receivedAce.permission)
                    {
                        Assert.IsNotNull(permission, "Incorrect permission was returned");
                        if (permission.Equals(PERMISSION_READ))
                        {
                            contains = true;
                        }
                    }                    
                }
            }
            Assert.IsTrue(contains, "Response doesn't contain expected permission");                       
        }

        public void testAddAndRemovePermissionConstraints()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            cmisAccessControlListType acList = createSimpleACL("Invalid principal", "Invalid permission");

            try
            {
                logger.log("[ACLService->applyACL]");
                aclServiceClient.applyACL(INVALID_REPOSITORY_ID, documentId, acList, null, getACLPropagation(), null);
                Assert.Skip("Exception expected");
            }
            catch (FaultException<cmisFaultType> e)
            {
            }

            try
            {
                logger.log("[ACLService->applyACL]");
                aclServiceClient.applyACL(getAndAssertRepositoryId(), INVALID_OBJECT_ID, acList, null, getACLPropagation(), null);
                Assert.Skip("Exception expected");
            }
            catch (FaultException<cmisFaultType> e)
            {
            }

            try
            {
                logger.log("[ACLService->applyACL]");
                aclServiceClient.applyACL(getAndAssertRepositoryId(), documentId, acList, null, getACLPropagation(), null);
                Assert.Skip("Exception expected");
            }
            catch (FaultException<cmisFaultType> e)
            {
            }
            
        }

        public void testGetACEsConstraints()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            try
            {
                logger.log("[ACLService->applyACL]");
                aclServiceClient.getACL(INVALID_REPOSITORY_ID, documentId, true, null);
                Assert.Skip("Exception expected");
            }
            catch (FaultException<cmisFaultType> e)
            {
            }

            try
            {
                logger.log("[ACLService->applyACL]");
                aclServiceClient.getACL(getAndAssertRepositoryId(), INVALID_OBJECT_ID, true, null);
                Assert.Skip("Exception expected");
            }
            catch (FaultException<cmisFaultType> e)
            {
            }
        }

    }
}
