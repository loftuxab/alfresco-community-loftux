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
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.QName;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PolicyComponentTest extends TestCase
{
    private static ApplicationContext ctx;
    private static QName BASE_TYPE;
    private static QName FILE_TYPE;
    private static QName FOLDER_TYPE;
    private static QName INVALID_TYPE;

    static
    {
        // Initialise application context
        ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

        // Construct Test Model Types
        BASE_TYPE = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "base");
        FILE_TYPE = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "file");
        FOLDER_TYPE = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "folder");
        INVALID_TYPE = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "classdoesnotexist");
        MetaModelDAO metaModelDAO = (MetaModelDAO)ctx.getBean("metaModelDAO");
        createTestTypes(metaModelDAO);
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
            BehaviourDefinition<ClassBehaviourBinding> definition = policyComponent.bindClassBehaviour(policyName, new ClassRef(FILE_TYPE), validBehaviour);
            assertNotNull(definition);
            assertEquals(policyName, definition.getPolicy());
            assertEquals(new ClassRef(FILE_TYPE), definition.getBinding().getClassRef());
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
        QName policyName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "test");
        Behaviour fileBehaviour = new JavaBehaviour(this, "fileTest");
        policyComponent.bindClassBehaviour(policyName, new ClassRef(FILE_TYPE), fileBehaviour);

        // Test NOOP Policy delegate
        Collection<TestPolicy> basePolicies = delegate.getList(new ClassRef(BASE_TYPE));
        assertNotNull(basePolicies);
        assertTrue(basePolicies.size() == 0);
        TestPolicy basePolicy = delegate.get(new ClassRef(BASE_TYPE));
        assertNotNull(basePolicy);
        
        // Test single Policy delegate
        Collection<TestPolicy> filePolicies = delegate.getList(new ClassRef(FILE_TYPE));
        assertNotNull(filePolicies);
        assertTrue(filePolicies.size() == 1);
        TestPolicy filePolicy = delegate.get(new ClassRef(FILE_TYPE));
        assertNotNull(filePolicy);
        assertEquals(filePolicies.iterator().next(), filePolicy);

        // Bind Service Behaviour
        Behaviour serviceBehaviour = new JavaBehaviour(this, "serviceTest");
        policyComponent.bindClassBehaviour(policyName, this, serviceBehaviour);

        // Test multi Policy delegate
        Collection<TestPolicy> file2Policies = delegate.getList(new ClassRef(FILE_TYPE));
        assertNotNull(file2Policies);
        assertTrue(file2Policies.size() == 2);
        TestPolicy filePolicy2 = delegate.get(new ClassRef(FILE_TYPE));
        assertNotNull(filePolicy2);
    }
    
    
    public void testOverride()
    {
        // Register Policy
        ClassPolicyDelegate<TestPolicy> delegate = policyComponent.registerClassPolicy(TestPolicy.class);
        
        // Bind Behaviour
        QName policyName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "test");
        Behaviour baseBehaviour = new JavaBehaviour(this, "baseTest");
        policyComponent.bindClassBehaviour(policyName, new ClassRef(BASE_TYPE), baseBehaviour);
        Behaviour folderBehaviour = new JavaBehaviour(this, "folderTest");
        policyComponent.bindClassBehaviour(policyName, new ClassRef(FOLDER_TYPE), folderBehaviour);

        // Invoke Policies        
        TestPolicy basePolicy = delegate.get(new ClassRef(BASE_TYPE));
        String baseResult = basePolicy.test("base");
        assertEquals("Base: base", baseResult);
        TestPolicy filePolicy = delegate.get(new ClassRef(FILE_TYPE));
        String fileResult = filePolicy.test("file");
        assertEquals("Base: file", fileResult);
        TestPolicy folderPolicy = delegate.get(new ClassRef(FOLDER_TYPE));
        String folderResult = folderPolicy.test("folder");
        assertEquals("Folder: folder", folderResult);
    }
    
    
    public void testCache()
    {
        // Register Policy
        ClassPolicyDelegate<TestPolicy> delegate = policyComponent.registerClassPolicy(TestPolicy.class);
        
        // Bind Behaviour
        QName policyName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "test");
        Behaviour baseBehaviour = new JavaBehaviour(this, "baseTest");
        policyComponent.bindClassBehaviour(policyName, new ClassRef(BASE_TYPE), baseBehaviour);
        Behaviour folderBehaviour = new JavaBehaviour(this, "folderTest");
        policyComponent.bindClassBehaviour(policyName, new ClassRef(FOLDER_TYPE), folderBehaviour);

        // Invoke Policies        
        TestPolicy basePolicy = delegate.get(new ClassRef(BASE_TYPE));
        String baseResult = basePolicy.test("base");
        assertEquals("Base: base", baseResult);
        TestPolicy filePolicy = delegate.get(new ClassRef(FILE_TYPE));
        String fileResult = filePolicy.test("file");
        assertEquals("Base: file", fileResult);
        TestPolicy folderPolicy = delegate.get(new ClassRef(FOLDER_TYPE));
        String folderResult = folderPolicy.test("folder");
        assertEquals("Folder: folder", folderResult);
        
        // Retrieve delegates again        
        TestPolicy basePolicy2 = delegate.get(new ClassRef(BASE_TYPE));
        assertTrue(basePolicy == basePolicy2);
        TestPolicy filePolicy2 = delegate.get(new ClassRef(FILE_TYPE));
        assertTrue(filePolicy == filePolicy2);
        TestPolicy folderPolicy2 = delegate.get(new ClassRef(FOLDER_TYPE));
        assertTrue(folderPolicy == folderPolicy2);
        
        // Bind new behaviour (forcing base & file cache resets)
        Behaviour newBaseBehaviour = new JavaBehaviour(this, "newBaseTest");
        policyComponent.bindClassBehaviour(policyName, new ClassRef(BASE_TYPE), newBaseBehaviour);

        // Invoke Policies        
        TestPolicy basePolicy3 = delegate.get(new ClassRef(BASE_TYPE));
        assertTrue(basePolicy3 != basePolicy2);
        String baseResult3 = basePolicy3.test("base");
        assertEquals("NewBase: base", baseResult3);
        TestPolicy filePolicy3 = delegate.get(new ClassRef(FILE_TYPE));
        assertTrue(filePolicy3 != filePolicy2);
        String fileResult3 = filePolicy3.test("file");
        assertEquals("NewBase: file", fileResult3);
        TestPolicy folderPolicy3 = delegate.get(new ClassRef(FOLDER_TYPE));
        assertTrue(folderPolicy3 == folderPolicy2);
        String folderResult3 = folderPolicy3.test("folder");
        assertEquals("Folder: folder", folderResult3);
        
        // Bind new behaviour (forcing file cache reset)
        Behaviour fileBehaviour = new JavaBehaviour(this, "fileTest");
        policyComponent.bindClassBehaviour(policyName, new ClassRef(FILE_TYPE), fileBehaviour);

        // Invoke Policies        
        TestPolicy basePolicy4 = delegate.get(new ClassRef(BASE_TYPE));
        assertTrue(basePolicy4 == basePolicy3);
        String baseResult4 = basePolicy4.test("base");
        assertEquals("NewBase: base", baseResult4);
        TestPolicy filePolicy4 = delegate.get(new ClassRef(FILE_TYPE));
        assertTrue(filePolicy4 != filePolicy3);
        String fileResult4 = filePolicy4.test("file");
        assertEquals("File: file", fileResult4);
        TestPolicy folderPolicy4 = delegate.get(new ClassRef(FOLDER_TYPE));
        assertTrue(folderPolicy4 == folderPolicy4);
        String folderResult4 = folderPolicy4.test("folder");
        assertEquals("Folder: folder", folderResult4);
    }


    //
    // The following interfaces represents policies
    //
    
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
     * @param metaModelDAO  the meta-model DAO
     */
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
