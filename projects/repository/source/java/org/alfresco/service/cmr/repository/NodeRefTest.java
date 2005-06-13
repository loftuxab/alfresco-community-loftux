package org.alfresco.service.cmr.repository;

import junit.framework.TestCase;

/**
 * @see org.alfresco.service.cmr.repository.NodeRef
 * 
 * @author Derek Hulley
 */
public class NodeRefTest extends TestCase
{

    public NodeRefTest(String name)
    {
        super(name);
    }

    public void testStoreRef() throws Exception
    {
        StoreRef storeRef = new StoreRef("ABC", "123");
        assertEquals("toString failure", "ABC://123", storeRef.toString());

        StoreRef storeRef2 = new StoreRef(storeRef.getProtocol(), storeRef
                .getIdentifier());
        assertEquals("equals failure", storeRef, storeRef2);
    }

    public void testNodeRef() throws Exception
    {
        StoreRef storeRef = new StoreRef("ABC", "123");
        NodeRef nodeRef = new NodeRef(storeRef, "456");
        assertEquals("toString failure", "ABC://123/456", nodeRef.toString());

        NodeRef nodeRef2 = new NodeRef(storeRef, "456");
        assertEquals("equals failure", nodeRef, nodeRef2);
    }
}
