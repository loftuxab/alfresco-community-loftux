package org.alfresco.repo.policy;

import java.util.Collection;

import junit.framework.TestCase;

import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.impl.DictionaryDAO;
import org.alfresco.repo.dictionary.impl.M2Model;
import org.alfresco.repo.dictionary.impl.M2Type;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.QName;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PolicyComponentTest extends TestCase
{
    private static ApplicationContext ctx;
    private static String TEST_NAMESPACE = "http://www.alfresco.org/test/policycomponenttest";
    private static QName BASE_TYPE;
    private static QName FILE_TYPE;
    private static QName FOLDER_TYPE;
    private static QName INVALID_TYPE;

    static
    {
        // Initialise application context
        ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

        // Construct Test Model Types
        
        BASE_TYPE = QName.createQName(TEST_NAMESPACE, "base");
        FILE_TYPE = QName.createQName(TEST_NAMESPACE, "file");
        FOLDER_TYPE = QName.createQName(TEST_NAMESPACE, "folder");
        INVALID_TYPE = QName.createQName(TEST_NAMESPACE, "classdoesnotexist");
        DictionaryDAO dictionaryDAO = (DictionaryDAO)ctx.getBean("dictionaryDAO");
        createTestTypes(dictionaryDAO);
    }

    private NodeService nodeService = null;
    private PolicyComponent policyComponent = null;


    @Override
    protected void setUp() throws Exception
    {
        nodeService = (NodeService)ctx.getBean("indexingNodeService");
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
        
        QName policyName = QName.createQName(TEST_NAMESPACE, "test");
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
        QName policyName = QName.createQName(TEST_NAMESPACE, "test");
        Behaviour validBehaviour = new JavaBehaviour(this, "validTest");
        
        // Test null policy
        try
        {
            policyComponent.bindClassBehaviour(null, FILE_TYPE, validBehaviour);
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
            policyComponent.bindClassBehaviour(policyName, INVALID_TYPE, validBehaviour);
            fail("Failed to catch invalid class reference whilst binding behaviour");
        }
        catch(IllegalArgumentException e) {}
        
        // Test null Behaviour
        try
        {
            policyComponent.bindClassBehaviour(policyName, FILE_TYPE, null);
            fail("Failed to catch null behaviour whilst binding behaviour");
        }
        catch(IllegalArgumentException e) {}

        // Test invalid behaviour (for registered policy)
        Behaviour invalidBehaviour = new JavaBehaviour(this, "methoddoesnotexist");
        policyComponent.registerClassPolicy(TestPolicy.class);
        try
        {
            policyComponent.bindClassBehaviour(policyName, FILE_TYPE, invalidBehaviour);
            fail("Failed to catch invalid behaviour whilst binding behaviour");
        }
        catch(PolicyException e) {}
        
        // Test valid behaviour (for registered policy)
        try
        {
            BehaviourDefinition<ClassBehaviourBinding> definition = policyComponent.bindClassBehaviour(policyName, FILE_TYPE, validBehaviour);
            assertNotNull(definition);
            assertEquals(policyName, definition.getPolicy());
            assertEquals(FILE_TYPE, definition.getBinding().getClassRef());
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
        
        // Bind Class Behaviour
        QName policyName = QName.createQName(TEST_NAMESPACE, "test");
        Behaviour fileBehaviour = new JavaBehaviour(this, "fileTest");
        policyComponent.bindClassBehaviour(policyName, FILE_TYPE, fileBehaviour);

        // Test NOOP Policy delegate
        Collection<TestPolicy> basePolicies = delegate.getList(BASE_TYPE);
        assertNotNull(basePolicies);
        assertTrue(basePolicies.size() == 0);
        TestPolicy basePolicy = delegate.get(BASE_TYPE);
        assertNotNull(basePolicy);
        
        // Test single Policy delegate
        Collection<TestPolicy> filePolicies = delegate.getList(FILE_TYPE);
        assertNotNull(filePolicies);
        assertTrue(filePolicies.size() == 1);
        TestPolicy filePolicy = delegate.get(FILE_TYPE);
        assertNotNull(filePolicy);
        assertEquals(filePolicies.iterator().next(), filePolicy);

        // Bind Service Behaviour
        Behaviour serviceBehaviour = new JavaBehaviour(this, "serviceTest");
        policyComponent.bindClassBehaviour(policyName, this, serviceBehaviour);

        // Test multi Policy delegate
        Collection<TestPolicy> file2Policies = delegate.getList(FILE_TYPE);
        assertNotNull(file2Policies);
        assertTrue(file2Policies.size() == 2);
        TestPolicy filePolicy2 = delegate.get(FILE_TYPE);
        assertNotNull(filePolicy2);
    }
    
    
    public void testOverride()
    {
        // Register Policy
        ClassPolicyDelegate<TestPolicy> delegate = policyComponent.registerClassPolicy(TestPolicy.class);
        
        // Bind Behaviour
        QName policyName = QName.createQName(TEST_NAMESPACE, "test");
        Behaviour baseBehaviour = new JavaBehaviour(this, "baseTest");
        policyComponent.bindClassBehaviour(policyName, BASE_TYPE, baseBehaviour);
        Behaviour folderBehaviour = new JavaBehaviour(this, "folderTest");
        policyComponent.bindClassBehaviour(policyName, FOLDER_TYPE, folderBehaviour);

        // Invoke Policies        
        TestPolicy basePolicy = delegate.get(BASE_TYPE);
        String baseResult = basePolicy.test("base");
        assertEquals("Base: base", baseResult);
        TestPolicy filePolicy = delegate.get(FILE_TYPE);
        String fileResult = filePolicy.test("file");
        assertEquals("Base: file", fileResult);
        TestPolicy folderPolicy = delegate.get(FOLDER_TYPE);
        String folderResult = folderPolicy.test("folder");
        assertEquals("Folder: folder", folderResult);
    }
    
    
    public void testCache()
    {
        // Register Policy
        ClassPolicyDelegate<TestPolicy> delegate = policyComponent.registerClassPolicy(TestPolicy.class);
        
        // Bind Behaviour
        QName policyName = QName.createQName(TEST_NAMESPACE, "test");
        Behaviour baseBehaviour = new JavaBehaviour(this, "baseTest");
        policyComponent.bindClassBehaviour(policyName, BASE_TYPE, baseBehaviour);
        Behaviour folderBehaviour = new JavaBehaviour(this, "folderTest");
        policyComponent.bindClassBehaviour(policyName, FOLDER_TYPE, folderBehaviour);

        // Invoke Policies        
        TestPolicy basePolicy = delegate.get(BASE_TYPE);
        String baseResult = basePolicy.test("base");
        assertEquals("Base: base", baseResult);
        TestPolicy filePolicy = delegate.get(FILE_TYPE);
        String fileResult = filePolicy.test("file");
        assertEquals("Base: file", fileResult);
        TestPolicy folderPolicy = delegate.get(FOLDER_TYPE);
        String folderResult = folderPolicy.test("folder");
        assertEquals("Folder: folder", folderResult);
        
        // Retrieve delegates again        
        TestPolicy basePolicy2 = delegate.get(BASE_TYPE);
        assertTrue(basePolicy == basePolicy2);
        TestPolicy filePolicy2 = delegate.get(FILE_TYPE);
        assertTrue(filePolicy == filePolicy2);
        TestPolicy folderPolicy2 = delegate.get(FOLDER_TYPE);
        assertTrue(folderPolicy == folderPolicy2);
        
        // Bind new behaviour (forcing base & file cache resets)
        Behaviour newBaseBehaviour = new JavaBehaviour(this, "newBaseTest");
        policyComponent.bindClassBehaviour(policyName, BASE_TYPE, newBaseBehaviour);

        // Invoke Policies        
        TestPolicy basePolicy3 = delegate.get(BASE_TYPE);
        assertTrue(basePolicy3 != basePolicy2);
        String baseResult3 = basePolicy3.test("base");
        assertEquals("NewBase: base", baseResult3);
        TestPolicy filePolicy3 = delegate.get(FILE_TYPE);
        assertTrue(filePolicy3 != filePolicy2);
        String fileResult3 = filePolicy3.test("file");
        assertEquals("NewBase: file", fileResult3);
        TestPolicy folderPolicy3 = delegate.get(FOLDER_TYPE);
        assertTrue(folderPolicy3 == folderPolicy2);
        String folderResult3 = folderPolicy3.test("folder");
        assertEquals("Folder: folder", folderResult3);
        
        // Bind new behaviour (forcing file cache reset)
        Behaviour fileBehaviour = new JavaBehaviour(this, "fileTest");
        policyComponent.bindClassBehaviour(policyName, FILE_TYPE, fileBehaviour);

        // Invoke Policies        
        TestPolicy basePolicy4 = delegate.get(BASE_TYPE);
        assertTrue(basePolicy4 == basePolicy3);
        String baseResult4 = basePolicy4.test("base");
        assertEquals("NewBase: base", baseResult4);
        TestPolicy filePolicy4 = delegate.get(FILE_TYPE);
        assertTrue(filePolicy4 != filePolicy3);
        String fileResult4 = filePolicy4.test("file");
        assertEquals("File: file", fileResult4);
        TestPolicy folderPolicy4 = delegate.get(FOLDER_TYPE);
        assertTrue(folderPolicy4 == folderPolicy4);
        String folderResult4 = folderPolicy4.test("folder");
        assertEquals("Folder: folder", folderResult4);
    }


    //
    // The following interfaces represents policies
    //
    
    public interface TestPolicy extends ClassPolicy
    {
        static String NAMESPACE = TEST_NAMESPACE;
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
    
    
    //
    // The following methods represent Java Behaviours
    // 
    
    public String validTest(String argument)
    {
        return "ValidTest: " + argument;
    }
    
    public String baseTest(String argument)
    {
        return "Base: " + argument;
    }

    public String newBaseTest(String argument)
    {
        return "NewBase: " + argument;
    }
    
    public String fileTest(String argument)
    {
        return "File: " + argument;
    }
    
    public String folderTest(String argument)
    {
        return "Folder: " + argument;
    }
    
    public String serviceTest(String argument)
    {
        return "Service: " + argument;
    }

    /**
     * Helper to create Model to test with
     * 
     * @param dictionaryDAO  the meta-model DAO
     */
    private static void createTestTypes(DictionaryDAO dictionaryDAO)
    {
        // Create Model
        M2Model model = M2Model.createModel("test:policycomponent");
        model.createNamespace(TEST_NAMESPACE, "test");
        
        // Create Test Base Type
        M2Type baseType = model.createType("test:" + BASE_TYPE.getLocalName());
    
        // Create Test File Type
        M2Type fileType = model.createType("test:" + FILE_TYPE.getLocalName());
        fileType.setParentName("test:" + BASE_TYPE.getLocalName());
        
        // Create Test Folder Type
        M2Type folderType = model.createType("test:" + FOLDER_TYPE.getLocalName());
        folderType.setParentName("test:" + BASE_TYPE.getLocalName());
        
        // Import model
        dictionaryDAO.putModel(model);
    }    
    
}
