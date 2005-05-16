package org.alfresco.repo.policy;

import java.util.Collection;

import junit.framework.TestCase;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.metamodel.M2ChildAssociation;
import org.alfresco.repo.dictionary.metamodel.M2Property;
import org.alfresco.repo.dictionary.metamodel.M2Type;
import org.alfresco.repo.dictionary.metamodel.MetaModelDAO;
import org.alfresco.repo.policy.PolicyComponentImpl.PolicyKey;
import org.alfresco.repo.ref.QName;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PolicyComponentTest extends TestCase
{

    private static ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    
    private static QName BASE_TYPE = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "base");
    private static QName FILE_TYPE = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "file");
    private static QName FOLDER_TYPE = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "folder");
    private static QName INVALID_TYPE = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "classdoesnotexist");
    
    
    static
    {
        MetaModelDAO metaModelDAO = (MetaModelDAO)ctx.getBean("metaModelDAO");
        createTestTypes(metaModelDAO);
    }

    private PolicyComponent policyComponent = null;
    

    @Override
    protected void setUp() throws Exception
    {
        DictionaryService dictionary = (DictionaryService)ctx.getBean("dictionaryService");
        policyComponent = new PolicyComponentImpl(dictionary); 
    }


    public void testJavaBehaviour()
    {
        Behaviour validBehaviour = new JavaBehaviour(this, "validTest");
        TestPolicy policy = validBehaviour.getInterface(TestPolicy.class);
        assertNotNull(policy);
        String result = policy.test("argument");
        assertEquals("ValidTest: argument", result);
    }
    
    
    public void testPolicyKey()
    {
        PolicyKey key1 = new PolicyKey(PolicyType.Class, BASE_TYPE);
        PolicyKey key2 = new PolicyKey(PolicyType.Class, BASE_TYPE);
        PolicyKey key3 = new PolicyKey(PolicyType.Property, BASE_TYPE);
        assertEquals(key1, key2);
        assertEquals(key1.equals(key3), false);
    }
    
    
    public void testRegisterDefinitions()
    {
        try
        {
            ClassPolicyDelegate<InvalidMetaDataPolicy> delegate = policyComponent.registerClassPolicy(InvalidMetaDataPolicy.class);
            fail("Failed to catch hidden metadata");
        }
        catch(PolicyException e)
        {
        }
    
        try
        {
            ClassPolicyDelegate<NoMethodPolicy> delegate = policyComponent.registerClassPolicy(NoMethodPolicy.class);
            fail("Failed to catch no methods defined in policy");
        }
        catch(PolicyException e)
        {
        }

        try
        {
            ClassPolicyDelegate<MultiMethodPolicy> delegate = policyComponent.registerClassPolicy(MultiMethodPolicy.class);
            fail("Failed to catch multiple methods defined in policy");
        }
        catch(PolicyException e)
        {
        }
        
        QName policyName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "test");
        boolean isRegistered = policyComponent.isRegisteredPolicy(PolicyType.Class, policyName);
        assertFalse(isRegistered);
        ClassPolicyDelegate<TestPolicy> delegate = policyComponent.registerClassPolicy(TestPolicy.class);
        assertNotNull(delegate);
        isRegistered = policyComponent.isRegisteredPolicy(PolicyType.Class, policyName);
        assertTrue(isRegistered);
        PolicyDefinition definition = policyComponent.getRegisteredPolicy(PolicyType.Class, policyName);
        assertNotNull(definition);
        assertEquals(policyName, definition.getName());
        assertEquals(PolicyType.Class, definition.getType());
        assertEquals(TestPolicy.class, definition.getPolicyInterface());
    }
    
    
    public void testBindBehaviour()
    {
        QName policyName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "test");
        Behaviour validBehaviour = new JavaBehaviour(this, "validTest");
        
        // Test null policy
        try
        {
            policyComponent.bindClassBehaviour(null, new ClassRef(FILE_TYPE), validBehaviour);
            fail("Failed to catch null policy whilst binding behaviour");
        }
        catch(IllegalArgumentException e) {}

        // Test null Class Reference
        try
        {
            policyComponent.bindClassBehaviour(policyName, null, validBehaviour);
            fail("Failed to catch null class reference whilst binding behaviour");
        }
        catch(IllegalArgumentException e) {}

        // Test invalid Class Reference
        try
        {
            policyComponent.bindClassBehaviour(policyName, new ClassRef(INVALID_TYPE), validBehaviour);
            fail("Failed to catch invalid class reference whilst binding behaviour");
        }
        catch(IllegalArgumentException e) {}
        
        // Test null Behaviour
        try
        {
            policyComponent.bindClassBehaviour(policyName, new ClassRef(FILE_TYPE), null);
            fail("Failed to catch null behaviour whilst binding behaviour");
        }
        catch(IllegalArgumentException e) {}

        // Test invalid behaviour (for registered policy)
        Behaviour invalidBehaviour = new JavaBehaviour(this, "methoddoesnotexist");
        policyComponent.registerClassPolicy(TestPolicy.class);
        try
        {
            policyComponent.bindClassBehaviour(policyName, new ClassRef(FILE_TYPE), invalidBehaviour);
            fail("Failed to catch invalid behaviour whilst binding behaviour");
        }
        catch(PolicyException e) {}
        
        // Test valid behaviour (for registered policy)
        try
        {
            BehaviourDefinition definition = policyComponent.bindClassBehaviour(policyName, new ClassRef(FILE_TYPE), validBehaviour);
            assertNotNull(definition);
            assertEquals(policyName, definition.getPolicy());
            assertEquals(new ClassRef(FILE_TYPE), definition.getBinding());
        }
        catch(PolicyException e)
        {
            fail("Policy exception thrown for valid behaviour");
        }
    }


    public void testDelegate()
    {
        // Register Policy
        ClassPolicyDelegate<TestPolicy> delegate = policyComponent.registerClassPolicy(TestPolicy.class);
        
        // Register Behaviour
        QName policyName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "test");
        Behaviour validBehaviour = new JavaBehaviour(this, "validTest");
        policyComponent.bindClassBehaviour(policyName, new ClassRef(FILE_TYPE), validBehaviour);
        
        // Test delegates
        Collection<TestPolicy> folderPolicies = delegate.getList(new ClassRef(FOLDER_TYPE));
        assertNotNull(folderPolicies);
        assertEquals(0, folderPolicies.size());
        Collection<TestPolicy> filePolicies = delegate.getList(new ClassRef(FILE_TYPE));
        assertNotNull(filePolicies);
        assertEquals(1, filePolicies.size());
        for (TestPolicy policy : filePolicies)
        {
            String result = policy.test("argument");
            assertEquals("ValidTest: argument", result);
        }
        TestPolicy filePolicy = delegate.get(new ClassRef(FILE_TYPE));
        
        
    }
    
    
    public String validTest(String argument)
    {
        return "ValidTest: " + argument;
    }
    
    
    public interface TestPolicy extends ClassPolicy
    {
        static String NAMESPACE = NamespaceService.ALFRESCO_TEST_URI;
        public String test(String argument);
    }

    public interface InvalidMetaDataPolicy extends ClassPolicy
    {
        static int NAMESPACE = 0;
        public String test(String nodeRef);
    }

    public interface NoMethodPolicy extends ClassPolicy
    {
    }
    
    public interface MultiMethodPolicy extends ClassPolicy
    {
        public void a();
        public void b();
    }
    

    private static void createTestTypes(MetaModelDAO metaModelDAO)
    {
        // Create Test Base Type
        M2Type baseType = metaModelDAO.createType(BASE_TYPE);
        M2Property primaryTypeProp = baseType.createProperty("primaryType");
        primaryTypeProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.QNAME));
        primaryTypeProp.setMandatory(true);
        primaryTypeProp.setProtected(true);
        primaryTypeProp.setMultiValued(false);
        M2Property aspectsProp = baseType.createProperty("aspects");
        aspectsProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.QNAME));
        aspectsProp.setMandatory(false);
        aspectsProp.setProtected(true);
        aspectsProp.setMultiValued(true);
    
        // Create Test File Type
        M2Type fileType = metaModelDAO.createType(FILE_TYPE);
        fileType.setSuperClass(baseType);
        M2Property encodingProp = fileType.createProperty("encoding");
        encodingProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.TEXT));
        encodingProp.setMandatory(true);
        encodingProp.setMultiValued(false);
        M2Property mimetypeProp = fileType.createProperty("mimetype");
        mimetypeProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.TEXT));
        mimetypeProp.setMandatory(true);
        mimetypeProp.setMultiValued(false);
        
        // Create Test Folder Type
        M2Type folderType = metaModelDAO.createType(FOLDER_TYPE);
        folderType.setSuperClass(baseType);
        M2ChildAssociation contentsAssoc = folderType.createChildAssociation("contents");
        contentsAssoc.getRequiredToClasses().add(fileType);
        contentsAssoc.setMandatory(false);
        contentsAssoc.setMultiValued(true);
    }    
    
}
