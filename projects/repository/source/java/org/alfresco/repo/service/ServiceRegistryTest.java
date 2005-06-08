package org.alfresco.repo.service;

import java.util.Collection;

import junit.framework.TestCase;

import org.alfresco.repo.ref.QName;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceRegistryTest extends TestCase
{
    
    private ApplicationContext factory = null;
    
    private static String TEST_NAMESPACE = "http://www.alfresco.org/test/serviceregistrytest";
    private static QName invalidService = QName.createQName(TEST_NAMESPACE, "invalid");
    private static QName service1 = QName.createQName(TEST_NAMESPACE, "service1");
    private static QName service2 = QName.createQName(TEST_NAMESPACE, "service2");
    private static QName service3 = QName.createQName(TEST_NAMESPACE, "service3");

    
    public void setUp()
    {
        factory = new ClassPathXmlApplicationContext("org/alfresco/repo/service/testregistry.xml");
    }
    
    public void testDescriptor()
    {
        ServiceRegistry registry = (ServiceRegistry)factory.getBean("serviceRegistry");
        
        Collection services = registry.getServices();
        assertNotNull(services);
        assertEquals(3, services.size());
        
        assertTrue(registry.isServiceProvided(service1));
        assertFalse(registry.isServiceProvided(invalidService));

        ServiceDescriptor invalid = registry.getServiceDescriptor(invalidService);
        assertNull(invalid);
        ServiceDescriptor desc1 = registry.getServiceDescriptor(service1);
        assertNotNull(desc1);
        assertEquals(service1, desc1.getName());
        assertEquals("Test Service 1", desc1.getDescription());
        assertEquals(TestServiceInterface.class, desc1.getInterface());
        ServiceDescriptor desc2 = registry.getServiceDescriptor(service2);
        assertNotNull(desc2);
        assertEquals(service2, desc2.getName());
        assertEquals("Test Service 2", desc2.getDescription());
        assertEquals(TestServiceInterface.class, desc2.getInterface());
    }

    
    public void testService()
    {
        ServiceRegistry registry = (ServiceRegistry)factory.getBean("serviceRegistry");
        
        TestServiceInterface theService1 = (TestServiceInterface)registry.getService(service1);
        assertNotNull(service1);
        assertEquals("Test1:service1", theService1.test("service1"));
        TestServiceInterface theService2 = (TestServiceInterface)registry.getService(service2);
        assertNotNull(service2);
        assertEquals("Test2:service2", theService2.test("service2"));
    }
    

    public void testStores()
    {
        ServiceRegistry registry = (ServiceRegistry)factory.getBean("serviceRegistry");
        
        ServiceDescriptor desc3 = registry.getServiceDescriptor(service3);
        assertNotNull(desc3);
        StoreRedirector theService3 = (StoreRedirector)registry.getService(service3);
        assertNotNull(service3);
        
        Collection<String> descStores = desc3.getSupportedStoreProtocols();
        assertTrue(descStores.contains("Type1"));
        assertTrue(descStores.contains("Type2"));
        assertFalse(descStores.contains("Invalid"));
        
        Collection<String> serviceStores = theService3.getSupportedStoreProtocols();
        for (String store: descStores)
        {
            assertTrue(serviceStores.contains(store));
        }
    }
    
    
    public interface TestServiceInterface
    {
        public String test(String arg);
    }

    public static abstract class Component implements TestServiceInterface
    {
        private String type;
        
        private Component(String type)
        {
            this.type = type;
        }
        
        public String test(String arg)
        {
            return type + ":" + arg;
        }
    }
    
    public static class Test1Component extends Component
    {
        private Test1Component()
        {
            super("Test1");
        }
    }

    public static class Test2Component extends Component
    {
        private Test2Component()
        {
            super("Test2");
        }
    }
    
}
