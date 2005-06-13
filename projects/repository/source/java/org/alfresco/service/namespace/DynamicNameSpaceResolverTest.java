/*
 * Created on 23-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.service.namespace;

import junit.framework.TestCase;

public class DynamicNameSpaceResolverTest extends TestCase
{

    public DynamicNameSpaceResolverTest()
    {
        super();
    }
    
    public void testOne()
    {
        DynamicNamespacePrefixResolver dnpr = new DynamicNamespacePrefixResolver(null);
        dnpr.addDynamicNamespace("one", "http:/namespace/one");
        dnpr.addDynamicNamespace("two", "http:/namespace/two");
        dnpr.addDynamicNamespace("three", "http:/namespace/three");
        dnpr.addDynamicNamespace("oneagain", "http:/namespace/one");
        dnpr.addDynamicNamespace("four", "http:/namespace/one");
        dnpr.addDynamicNamespace("four", "http:/namespace/four");
        
        assertEquals("http:/namespace/one", dnpr.getNamespaceURI("one"));
        assertEquals("http:/namespace/two", dnpr.getNamespaceURI("two"));
        assertEquals("http:/namespace/three", dnpr.getNamespaceURI("three"));
        assertEquals("http:/namespace/one", dnpr.getNamespaceURI("oneagain"));
        assertEquals("http:/namespace/four", dnpr.getNamespaceURI("four"));
        assertEquals(null, dnpr.getNamespaceURI("five"));
        
        dnpr.removeDynamicNamespace("four");
        assertEquals(null, dnpr.getNamespaceURI("four"));
        
        assertEquals(0, dnpr.getPrefixes("http:/namespace/four").size());
        assertEquals(1, dnpr.getPrefixes("http:/namespace/two").size());
        assertEquals(2, dnpr.getPrefixes("http:/namespace/one").size());
        
        
    }
    
    
    public void testTwo()
    {
        DynamicNamespacePrefixResolver dnpr1 = new DynamicNamespacePrefixResolver(null);
        dnpr1.addDynamicNamespace("one", "http:/namespace/one");
        dnpr1.addDynamicNamespace("two", "http:/namespace/two");
        dnpr1.addDynamicNamespace("three", "http:/namespace/three");
        dnpr1.addDynamicNamespace("oneagain", "http:/namespace/one");
        dnpr1.addDynamicNamespace("four", "http:/namespace/one");
        dnpr1.addDynamicNamespace("four", "http:/namespace/four");
        dnpr1.addDynamicNamespace("five", "http:/namespace/five");
        dnpr1.addDynamicNamespace("six", "http:/namespace/six");
        
        DynamicNamespacePrefixResolver dnpr2 = new DynamicNamespacePrefixResolver(dnpr1);
        dnpr2.addDynamicNamespace("a", "http:/namespace/one");
        dnpr2.addDynamicNamespace("b", "http:/namespace/two");
        dnpr2.addDynamicNamespace("c", "http:/namespace/three");
        dnpr2.addDynamicNamespace("d", "http:/namespace/one");
        dnpr2.addDynamicNamespace("e", "http:/namespace/one");
        dnpr2.addDynamicNamespace("f", "http:/namespace/four");
        dnpr2.addDynamicNamespace("five", "http:/namespace/one");
        dnpr2.addDynamicNamespace("six", "http:/namespace/seven");
        
        assertEquals("http:/namespace/one", dnpr2.getNamespaceURI("one"));
        assertEquals("http:/namespace/two", dnpr2.getNamespaceURI("two"));
        assertEquals("http:/namespace/three", dnpr2.getNamespaceURI("three"));
        assertEquals("http:/namespace/one", dnpr2.getNamespaceURI("oneagain"));
        assertEquals("http:/namespace/four", dnpr2.getNamespaceURI("four"));
        assertEquals("http:/namespace/one", dnpr2.getNamespaceURI("five"));
        dnpr2.removeDynamicNamespace("five");
        
        assertEquals("http:/namespace/five", dnpr2.getNamespaceURI("five"));
        assertEquals("http:/namespace/one", dnpr2.getNamespaceURI("a"));
        assertEquals("http:/namespace/two", dnpr2.getNamespaceURI("b"));
        assertEquals("http:/namespace/three", dnpr2.getNamespaceURI("c"));
        assertEquals("http:/namespace/one", dnpr2.getNamespaceURI("d"));
        assertEquals("http:/namespace/one", dnpr2.getNamespaceURI("e"));
        assertEquals("http:/namespace/four", dnpr2.getNamespaceURI("f"));
        
        assertEquals(5, dnpr2.getPrefixes("http:/namespace/one").size());
        assertEquals(2, dnpr2.getPrefixes("http:/namespace/two").size());
        assertEquals(2, dnpr2.getPrefixes("http:/namespace/three").size());
        assertEquals(2, dnpr2.getPrefixes("http:/namespace/four").size());
        assertEquals(1, dnpr2.getPrefixes("http:/namespace/five").size());
        assertEquals(0, dnpr2.getPrefixes("http:/namespace/six").size());
        assertEquals(1, dnpr2.getPrefixes("http:/namespace/seven").size());
    }

}
