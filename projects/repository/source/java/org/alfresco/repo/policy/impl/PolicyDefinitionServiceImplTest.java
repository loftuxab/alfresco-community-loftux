package org.alfresco.repo.policy.impl;

import java.util.Set;

import junit.framework.TestCase;

public class PolicyDefinitionServiceImplTest extends TestCase
{
    public void testRegisterPolicy()
    {
        Object exposingObject = new Object();
        PolicyDefinitionServiceImpl service = new PolicyDefinitionServiceImpl();
        
        service.registerPolicy(exposingObject, TestPolicy.class);        
    }

    public void testHasPolicy()
    {
        Object exposingObject = new Object();
        PolicyDefinitionServiceImpl service = new PolicyDefinitionServiceImpl();
        
        service.registerPolicy(exposingObject, TestPolicy.class); 
        
        boolean result1 = service.hasPolicy(exposingObject, TestPolicy.class);
        assertTrue(result1);
        
        boolean result2 = service.hasPolicy(exposingObject, String.class);
        assertFalse(result2);
        
        boolean result3 = service.hasPolicy(new Object(), TestPolicy.class);
        assertFalse(result3);
    }

    public void testGetRegisteredPolicies()
    {
        Object exposingObject = new Object();
        PolicyDefinitionServiceImpl service = new PolicyDefinitionServiceImpl();
        
        service.registerPolicy(exposingObject, TestPolicy.class);
        
        Set<Class> result1 = service.getRegisteredPolicies(exposingObject);
        assertNotNull(result1);
        assertEquals(1, result1.size());
        
        Set<Class> result2 = service.getRegisteredPolicies(new Object());
        assertNull(result2);
    }
    
    public interface TestPolicy
    {
        public void OnTest(String value);            
    }
}
