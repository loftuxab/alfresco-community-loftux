/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package client.axis.test;

import org.alfresco.util.BaseTest;

/**
 * Tests the NodeService web service
 * 
 * @author gavinc
 */
public class NodeServiceTest extends BaseTest
{
    public void test()
    {
        // placeholder so that test doesn't fail during automated build, need to remove this
        // when we have a way of testing "system" tests i.e. those that need the server up.
    }
   
    public void xtestNodeServiceWSDL() throws Exception 
    {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new client.axis.NodeServiceLocator().getNodeServiceAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new client.axis.NodeServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    /**
     * Test whether the SpacesStore exists
     */
    public void xtestStoreExists() throws Exception 
    {
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
    public void xtestGetRootNode() throws Exception 
    {
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
    public void xtestGetChildren() throws Exception 
    {
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
