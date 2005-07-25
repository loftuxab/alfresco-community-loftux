/**
 * NodeServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package client.axis.test;


public class NodeServiceTest extends junit.framework.TestCase {
    public NodeServiceTest(java.lang.String name) {
        super(name);
    }

    public void testNodeServiceWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new client.axis.NodeServiceLocator().getNodeServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new client.axis.NodeServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    /**
     * Test whether the SpacesStore exists
     */
    public void testStoreExists() throws Exception {
        client.axis.NodeServiceSoapBindingStub binding;
        try {
            binding = (client.axis.NodeServiceSoapBindingStub)
                          new client.axis.NodeServiceLocator().getNodeService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        boolean value = false;
        value = binding.storeExists(new client.axis.StoreRef("workspace://SpacesStore","SpacesStore"));
        assertTrue("SpacesStore should exist", value);
        System.out.println("SpacesStore exists = " + value);
    }
    
    /**
     * Tests whether the root node can be retrieved
     */
    public void testGetRootNode() throws Exception {
        client.axis.NodeServiceSoapBindingStub binding;
        try {
            binding = (client.axis.NodeServiceSoapBindingStub)
                          new client.axis.NodeServiceLocator().getNodeService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        client.axis.NodeRef value = null;
        value = binding.getRootNode(new client.axis.StoreRef("workspace://SpacesStore", "SpacesStore"));
        assertNotNull("root node should not be null", value);
        System.out.println("root node id = " + value.getId());
    }
    
    /**
     * Tests whether the root node can be retrieved
     */
    public void testGetChildren() throws Exception {
        client.axis.NodeServiceSoapBindingStub binding;
        try {
            binding = (client.axis.NodeServiceSoapBindingStub)
                          new client.axis.NodeServiceLocator().getNodeService();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        client.axis.QueryResult value = null;
        value = binding.getChildren(new client.axis.StoreRef("workspace://SpacesStore", "SpacesStore"), "0");
        assertNotNull("there should be a query result", value);
        System.out.println("number of children = " + value.getHits());
    }
}
