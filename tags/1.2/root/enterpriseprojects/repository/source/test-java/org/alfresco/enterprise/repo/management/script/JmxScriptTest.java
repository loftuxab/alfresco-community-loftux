/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.CopyActionExecuter;
import org.alfresco.repo.action.executer.MoveActionExecuter;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.jscript.ClasspathScriptLocation;
import org.alfresco.repo.management.subsystems.ChildApplicationContextFactory;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.ScriptLocation;
import org.alfresco.service.cmr.repository.ScriptService;
import org.alfresco.util.Pair;
import org.alfresco.util.test.junitrules.AlfrescoPerson;
import org.alfresco.util.test.junitrules.ApplicationContextInit;
import org.alfresco.util.test.junitrules.RunAsFullyAuthenticatedRule;
import org.alfresco.util.test.junitrules.RunAsFullyAuthenticatedRule.RunAsUser;
import org.alfresco.util.test.junitrules.TemporaryNodes;
import org.alfresco.util.test.junitrules.WellKnownNodes;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.springframework.util.ReflectionUtils;

/**
 * Integration tests for {@link JmxScriptImpl}.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class JmxScriptTest
{
    private static final String TEST_USERNAME = "TestUser";
    
    // This spring context adds a test-only MBean.
    private static final String testContext = "classpath:/" + JmxScriptTest.class.getName().replaceAll("\\.", "/") + "-context.xml";
    
    // Rule to initialise the default Alfresco spring configuration
    public static final ApplicationContextInit APP_CONTEXT_INIT = ApplicationContextInit.createStandardContextWithOverrides(testContext);
    
    // Rule to create a test user.
    public static final AlfrescoPerson TEST_USER = new AlfrescoPerson(APP_CONTEXT_INIT, TEST_USERNAME);
    
    // A rule to manage test nodes reused across all the test methods
    public static final TemporaryNodes STATIC_TEST_NODES = new TemporaryNodes(APP_CONTEXT_INIT);
    
    // A rule to get well-known nodes
    public static final WellKnownNodes WELL_KNOWN_NODES = new WellKnownNodes(APP_CONTEXT_INIT);
    
    // Tie them together in a static Rule Chain
    @ClassRule public static RuleChain STATIC_RULE_CHAIN = RuleChain.outerRule(APP_CONTEXT_INIT)
                                                            .around(WELL_KNOWN_NODES)
                                                            .around(TEST_USER)
                                                            .around(STATIC_TEST_NODES);
    
    // A rule to allow individual test methods all to be run as the admin user.
    public RunAsFullyAuthenticatedRule runAsRule = new RunAsFullyAuthenticatedRule(AuthenticationUtil.getAdminUserName());
    
    // A rule to manage temporary test nodes in each @Test method.
    public TemporaryNodes testNodes = new TemporaryNodes(APP_CONTEXT_INIT);
    
    // Tie them together in a non-static Rule Chain.
    @Rule public RuleChain nonStaticRuleChain = RuleChain.outerRule(runAsRule)
                                                         .around(testNodes);
    
    // Various services
    private static ActionService               ACTION_SERVICE;
    private static MBeanServerConnection       MBEAN_SERVER;
    private static NodeService                 NODE_SERVICE;
    private static RetryingTransactionHelper   TRANSACTION_HELPER;
    private static ScriptService               SCRIPT_SERVICE;
    
    // Various data used within @Test methods
    private NodeRef document;
    private NodeRef folder;
    
    @BeforeClass public static void initStaticData() throws Exception
    {
        ACTION_SERVICE     = APP_CONTEXT_INIT.getApplicationContext().getBean("actionService", ActionService.class);
        MBEAN_SERVER       = APP_CONTEXT_INIT.getApplicationContext().getBean("alfrescoMBeanServer", MBeanServerConnection.class);
        NODE_SERVICE       = APP_CONTEXT_INIT.getApplicationContext().getBean("nodeService", NodeService.class);
        SCRIPT_SERVICE     = APP_CONTEXT_INIT.getApplicationContext().getBean("scriptService", ScriptService.class);
        TRANSACTION_HELPER = APP_CONTEXT_INIT.getApplicationContext().getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
        
        
        // We want to ensure that MBeans which expose attributes with names containing Java/JavaScript illegal characters
        // are still usable via the JavaScript API. An example would be alpha.beta*gamma as '.' and '*' are not legal chars
        // in Java/JavaScript identifiers.
        
        // TODO Is there a way to dynamically add this attribute to the existing test MBean below? Rather than creating a second test mbean?
        
        ObjectName objectName = new ObjectName("Alfresco:Name=TestJmxOddNames");
        RequiredModelMBean mbean = new RequiredModelMBean();
        mbean.setManagedResource(new OddNameBean(), "objectReference");
        
        Descriptor oddNameDescriptor = new DescriptorSupport(new String[] { "name=alpha.beta*gamma",
                                                                            "descriptorType=attribute",
                                                                            "getMethod=getAttributeWithOddName",
                                                                            "setMethod=setAttributeWithOddName"});
        ModelMBeanAttributeInfo oddNameInfo = new ModelMBeanAttributeInfo("alpha.beta*gamma",
                                                                          "java.lang.String",
                                                                          "Odd Name",
                                                                          true, true, false,
                                                                          oddNameDescriptor);
        
        ModelMBeanOperationInfo getOddNameInfo = new ModelMBeanOperationInfo("Get odd name attribute",
                                                                             OddNameBean.class.getMethod("getAttributeWithOddName"));
        
        ModelMBeanOperationInfo setOddNameInfo = new ModelMBeanOperationInfo("Set odd name attribute",
                                                                             OddNameBean.class.getMethod("setAttributeWithOddName", String.class));
        
        ModelMBeanInfo mbeanInfo = new ModelMBeanInfoSupport(OddNameBean.class.getSimpleName(),
                                                             "Odd Name Bean - description",
                                                             new ModelMBeanAttributeInfo[] {oddNameInfo},
                                                             null,
                                                             new ModelMBeanOperationInfo[] {getOddNameInfo, setOddNameInfo},
                                                             null);
        mbean.setModelMBeanInfo(mbeanInfo);
        
        // This cast only works for local MBeanServers, which is fine for test code.
        MBeanServer server = (MBeanServer)MBEAN_SERVER;
        server.registerMBean(mbean, objectName);
    }
    
    public static class OddNameBean {
        private String attributeWithOddName = "initial value";
        
        public String getAttributeWithOddName() { return this.attributeWithOddName; }
        public void setAttributeWithOddName(String newValue) { this.attributeWithOddName = newValue; }
    }
    
    @Before public void createTestContent() throws Exception
    {
        document = testNodes.createQuickFile(MimetypeMap.MIMETYPE_TEXT_PLAIN,
                                             WELL_KNOWN_NODES.getCompanyHome(),
                                             this.getClass().getSimpleName() + "_TestNode.txt",
                                             AuthenticationUtil.getAdminUserName());
        folder = testNodes.createFolder(WELL_KNOWN_NODES.getCompanyHome(),
                                        this.getClass().getSimpleName() + "_TestFolder",
                                        AuthenticationUtil.getAdminUserName());
    }
    
    /**
     * This method, if not {@link Ignore ignored}, is a utility method for developers to get lists of Alfresco MBeans by attribute type.
     */
    @Ignore ("This is not really a test method per se. Remove Ignore annotation to use it.")
    @Test public void listTypesOfAllMBeans() throws Exception
    {
        final Map<String, MBeanPojo> allAttributesByType = new TreeMap<String, MBeanPojo>();
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // name = null for all MBeans
                // ObjectName name = null;
                
                // Pattern to select only Alfresco MBeans
                ObjectName name = new ObjectName("Alfresco:*");
                
                Set<ObjectName> mbeanNames = MBEAN_SERVER.queryNames(name , null);
                for (ObjectName mbeanName : mbeanNames)
                {
                    MBeanInfo mbeanInfo                 = MBEAN_SERVER.getMBeanInfo(mbeanName);
                    MBeanAttributeInfo[] attributeInfos = mbeanInfo.getAttributes();
                    
                    // Exclude the test MBean from this class
                    if ( !mbeanName.equals(new ObjectName("Alfresco:Name=TestJmx")))
                    {
                        for (MBeanAttributeInfo attributeInfo : attributeInfos)
                        {
                            MBeanPojo mbeanPojo = new MBeanPojo(mbeanName.getCanonicalName(), attributeInfo.getName(), attributeInfo);
                            if (!allAttributesByType.containsKey(mbeanPojo.getQName()))
                            {
                                allAttributesByType.put(attributeInfo.getType() + (attributeInfo.isWritable() ? "[w]" : "[r]")
                                        , mbeanPojo);
                            }
                        }
                    }
                }
                
                return null;
            }
        }, true);
        for (Map.Entry<String, MBeanPojo> entry : allAttributesByType.entrySet())
        {
            MBeanAttributeInfo attrInfo = entry.getValue().attributeInfo;
            System.err.println(attrInfo.getType() +
                    (attrInfo.isWritable() ? "[w]" : "[r]") +
                    "    " + entry.getValue());
        }
    }
    
    /**
     * This method, if not {@link Ignore ignored}, is a utility method for developers to get lists of Alfresco MBean Attributes by attribute type.
     */
    @Ignore ("This is not really a test method per se. Remove Ignore annotation to use it.")
    @Test public void listAllMBeansOfType() throws Exception
    {
        final Map<String, List<MBeanPojo>> allAttributesByType = new TreeMap<String, List<MBeanPojo>>();
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // name = null for all MBeans
                // ObjectName name = null;
                
                // Pattern to select only Alfresco MBeans
                ObjectName name = new ObjectName("Alfresco:*");
                
                Set<ObjectName> mbeanNames = MBEAN_SERVER.queryNames(name , null);
                for (ObjectName mbeanName : mbeanNames)
                {
                    MBeanInfo mbeanInfo                 = MBEAN_SERVER.getMBeanInfo(mbeanName);
                    MBeanAttributeInfo[] attributeInfos = mbeanInfo.getAttributes();
                    
                    // Exclude the test MBean from this class
                    if ( !mbeanName.equals(new ObjectName("Alfresco:Name=TestJmx")))
                    {
                        for (MBeanAttributeInfo attributeInfo : attributeInfos)
                        {
                            MBeanPojo mbeanPojo = new MBeanPojo(mbeanName.getCanonicalName(), attributeInfo.getName(), attributeInfo);
                            List<MBeanPojo> attributesOfThisType = allAttributesByType.get(attributeInfo.getType());
                            if (attributesOfThisType == null)
                            {
                                attributesOfThisType = new ArrayList<MBeanPojo>();
                                allAttributesByType.put(attributeInfo.getType(), attributesOfThisType);
                            }
                            attributesOfThisType.add(mbeanPojo);
                        }
                    }
                }
                
                return null;
            }
        }, true);
        
        final String type = "javax.management.openmbean.TabularData";
        
        System.err.println("Attribute Type: " + type);
        for (MBeanPojo mbeanPojo : allAttributesByType.get(type))
        {
            System.err.println(mbeanPojo.beanName + " " + mbeanPojo.attributeName);
        }
    }
    
    @Test public void listAllMBeanOperations() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // name = null for all MBeans
                // ObjectName name = null;
                
                // Pattern to select only Alfresco MBeans
                ObjectName name = new ObjectName("Alfresco:*");
                
                Set<ObjectName> mbeanNames = MBEAN_SERVER.queryNames(name , null);
                for (ObjectName mbeanName : mbeanNames)
                {
                    MBeanInfo            mbeanInfo      = MBEAN_SERVER.getMBeanInfo(mbeanName);
                    MBeanOperationInfo[] operationInfos = mbeanInfo.getOperations();
                    
                    // Exclude the test MBean from this class
                    if ( !mbeanName.equals(new ObjectName("Alfresco:Name=TestJmx")))
                    {
                        for (MBeanOperationInfo operationInfo : operationInfos)
                        {
                            System.err.println(mbeanName.toString() + ":" + toString(operationInfo));
                        }
                    }
                }
                
                return null;
            }
            
            private String toString(MBeanOperationInfo op)
            {
                StringBuilder result = new StringBuilder();
                
                result.append(op.getName())
                      .append("(");
                for (int i = 0; i < op.getSignature().length; i++)
                {
                    result.append(op.getSignature()[i].getType());
                    if (i < op.getSignature().length - 1)
                    {
                        result.append(", ");
                    }
                }
                result.append("): ")
                      .append(op.getReturnType());
                
                return result.toString();
            }
        }, true);
    }
    
    private static class MBeanPojo
    {
        public final String beanName, attributeName;
        public final MBeanAttributeInfo attributeInfo;
        
        public MBeanPojo(String beanName, String attributeName, MBeanAttributeInfo attributeInfo)
                        { this.beanName = beanName; this.attributeName = attributeName; this.attributeInfo = attributeInfo; }
        public String getQName() { return this.beanName + " - " + this.attributeName; }
        
        public String toString() { return this.getQName(); }
    }
    
    @Test public void ensureTestMBeanIsAvailable() throws Exception
    {
        assertNotNull("The test-only MBean was not found.", APP_CONTEXT_INIT.getApplicationContext().getBean("TestJmx"));
    }
    
    @Test public void adminUserCanReadFromJmxJavaScriptRootObject() throws Exception
    {
        queryForSingleMBeanImpl("Alfresco:Name=GlobalProperties");
    }
    
    // Override the default RunAs for this class and use another user instead
    @Test (expected=ScriptException.class)
    @RunAsUser(userName="TEST_USERNAME")
    public void nonAdminUserCannotReadFromJmxJavaScriptRootObject() throws Exception
    {
        queryForSingleMBeanImpl("Alfresco:Name=GlobalProperties");
    }
    
    private void queryForSingleMBeanImpl(final String objectName)
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // A trivially simple test script accessing the 'jmx' root object should be enough here.
                final String javaScript = "jmx.queryMBeans('" + objectName + "');";
                
                SCRIPT_SERVICE.executeScriptString(javaScript, new HashMap<String, Object>());
                
                return null;
            }
        }, true);
    }
    
    
    
    @Test public void performReadTestsOfJavaScriptApi() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/management/script/test_JmxReadAccessViaJavaScript.js");
                SCRIPT_SERVICE.executeScript(location, new HashMap<String, Object>());
                
                return null;
            }
        }, true);
    }
    
    // The read tests are separated from the write tests so that if we ever have different allowable roles for read & write, a simple
    // @RunAsUser annotation should be the only change that is needed here.
    @Test public void performWriteTestsOfJavaScriptApi() throws Exception
    {
        try
        {
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/management/script/test_JmxWriteAccessViaJavaScript.js");
                    SCRIPT_SERVICE.executeScript(location, new HashMap<String, Object>());
                    
                    // Ensure that the transaction does not commit and instead rolls back. We only want to ensure that the script
                    // runs to completion without exception. Transaction commit it tested elsewhere in this class.
                    throw new TestExecutionRolledBackNormally();
                }
            });
        }
        catch (TestExecutionRolledBackNormally ignored)
        {
            // Intentionally empty.
        }
    }
    
    /**
     * Arrays/Collections are a bit of a special case, so as well as the coverage provided in {@link #performWriteTestsOfJavaScriptApi()},
     * which do not commit their updates, we'll add one test case that actually commits its changes.
     */
    @Ignore("Temporarily disabled as this method leaves StringArray attribute as 'Goodbye', 'World' which causes other test in this class to fail")
    @Test public void performOneSpecificArrayAttributeWriteWithCommitAndValidate() throws Exception
    {
        final String[] originalArrayValue = new TestJmx().stringArray;
        final String beanName = "Alfresco:Name=TestJmx";
        final String attrName = "StringArray";
        
        try
        {
            // Read the current JMX attribute value and make sure all is well.
            final String[] attrValue = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<String[]>()
            {
                public String[] execute() throws Throwable
                {
                    return (String[]) MBEAN_SERVER.getAttribute(new ObjectName(beanName), attrName);
                }
            }, true);
            
            assertArrayEquals(originalArrayValue, attrValue);
            
            String[] updatedAttributeValue = new String[] { "Goodbye", "World" };
            final String newAttrElemValue = updatedAttributeValue[0];
            
            // Write the new value to JMX.
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("beanName",        beanName);
                    model.put("attributeName",   attrName);
                    model.put("newElementValue", newAttrElemValue);
                    
                    ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/management/script/test_JmxWriteAnArrayElementViaJavaScript.js");
                    SCRIPT_SERVICE.executeScript(location, model);
                    
                    return null;
                }
            });
            
            // Read the updated value from JMX and assert it's updated.
            final String[] reReadValue = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<String[]>()
            {
                public String[] execute() throws Throwable
                {
                    return (String[]) MBEAN_SERVER.getAttribute(new ObjectName(beanName), attrName);
                }
            }, true);
            
            assertArrayEquals(updatedAttributeValue, reReadValue);
        }
        finally
        {
            // Write the original value to JMX to ensure nothing is changed by this test case.
            TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                public Void execute() throws Throwable
                {
                    Attribute attr = new Attribute(attrName, originalArrayValue);
                    MBEAN_SERVER.setAttribute(new ObjectName(beanName), attr );
                    
                    return null;
                }
            });
        }
    }
    
    private static class TestExecutionRolledBackNormally extends AlfrescoRuntimeException
    {
        private static final long serialVersionUID = 1L;
        public TestExecutionRolledBackNormally() { super("TestExecutionRolledBackNormally"); }
    }
    
    /**
     * This test ensures that the basic [update, save] flow works in jmx in JS.
     */
    @Test public void performOneSpecificAttributeWriteWithCommitAndValidate() throws Exception
    {
        final String beanName = "Alfresco:Type=Configuration,Category=OOoJodconverter,id1=default";
        final String attrName = "jodconverter.taskQueueTimeout";
        
        // Read the current JMX attribute value and make sure all is well.
        final Object attrValue = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws Throwable
            {
                return MBEAN_SERVER.getAttribute(new ObjectName(beanName), attrName);
            }
        }, true);
        
        // Create a new value based on the old value - we'll switch back and forward between two values.
        final Object newAttrValue;
        if ("30000".equals(attrValue))
        {
            newAttrValue = "40000";
        }
        else
        {
            newAttrValue = "30000";
        }
        
        // Write the new value to JMX.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("beanName",      beanName);
                model.put("attributeName", attrName);
                model.put("newValue",      newAttrValue);
                
                ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/management/script/test_setJmxAttribute.js");
                SCRIPT_SERVICE.executeScript(location, model);
                
                return null;
            }
        });
        
        // Read the updated value from JMX and assert it's updated.
        final Object reReadValue = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Object>()
        {
            public Object execute() throws Throwable
            {
                return MBEAN_SERVER.getAttribute(new ObjectName(beanName), attrName);
            }
        }, true);
        
        assertEquals(newAttrValue, reReadValue);
    }
    
    /**
     * This test ensures that it is possible to update a number of JMX attributes on an MBean in a subsystem and have the subsystem restarted.
     */
    @Test public void updatingAttributesOnMBeanInSubsystemMustEnsureSubsystemStartsAfterUpdates() throws Exception
    {
        final String beanName = "Alfresco:Type=Configuration,Category=OOoJodconverter,id1=default";
        final String attrName1 = "jodconverter.taskQueueTimeout";
        final String attrName2 = "jodconverter.maxTasksPerProcess";
        
        // Read the current JMX attribute values and make sure all is well.
        final Pair<Attribute, Attribute> attrValues = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Pair<Attribute, Attribute>>()
        {
            public Pair<Attribute, Attribute> execute() throws Throwable
            {
                AttributeList attrs = MBEAN_SERVER.getAttributes(new ObjectName(beanName), new String[] { attrName1, attrName2 });
                List<Attribute> attrsList = attrs.asList();
                return new Pair<Attribute, Attribute>(attrsList.get(0), attrsList.get(1));
            }
        }, true);
        
        // Create new values based on the old values - we'll switch back and forward between two values.
        final Pair<String, String> newAttrValues = new Pair<String, String>(attrValues.getFirst().getValue().equals("30000") ? "40000" : "30000",
                                                                            attrValues.getSecond().getValue().equals("200") ? "300" : "200");
        
        // Write the new values to JMX.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("beanName",       beanName);
                model.put("attributeNames",  new String[] { attrName1, attrName2 });
                model.put("attributeValues", new String[] { newAttrValues.getFirst(), newAttrValues.getSecond() });
                
                ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/management/script/test_setJmxAttributes.js");
                SCRIPT_SERVICE.executeScript(location, model);
                
                return null;
            }
        });
        
        // Read the updated values from JMX and assert they're updated.
        final List<Attribute> reReadValues = TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<List<Attribute>>()
        {
            public List<Attribute> execute() throws Throwable
            {
                return MBEAN_SERVER.getAttributes(new ObjectName(beanName), new String[] { attrName1, attrName2 }).asList();
            }
        }, true);
        
        assertEquals(newAttrValues.getFirst(), reReadValues.get(0).getValue());
        assertEquals(newAttrValues.getSecond(), reReadValues.get(1).getValue());
        
        
        // We expect the subsystem to restart when we set multiple parameter values.
        assertSubsystemIsInState("OOoJodconverter", "STARTED", "PENDING_BROADCAST_START");
    }
    
    /** Asserts that the specified subsystem is in one of the given states.
     * 
     * @param subsystemName the name of the subsystem to check.
     * @param subsystemStates the list of acceptable states for the given subsystem.
     */
    private void assertSubsystemIsInState(String subsystemName, String... subsystemStates)
    {
        // We need to examine the subsystem's "runtimeState", which is private data.
        // So we'll break in using some reflection trickery.
        ChildApplicationContextFactory jodSubsystem = APP_CONTEXT_INIT.getApplicationContext().getBean(subsystemName, ChildApplicationContextFactory.class);
        Field runtimeStateField = ReflectionUtils.findField(ChildApplicationContextFactory.class, "runtimeState");
        
        // Even though it's private, make it accessible anyway.
        ReflectionUtils.makeAccessible(runtimeStateField);
        
        // Now get the value of that field on our subsystem.
        Object field = ReflectionUtils.getField(runtimeStateField, jodSubsystem);
        
        // And assert that it is as expected. (Note that the field is an instance of the RuntimeState enum.
        // That enum is private, hence the toString() - we can't compare actual enum values due to visibility restrictions.)
        assertTrue(Arrays.asList(subsystemStates).contains(String.valueOf(field)));
    }
    
    @Test public void readCompositeDataBeanAttribute() throws Exception
    {
        final String beanName = "Alfresco:Name=RunningActions";
        final String attrName = "ActionStatistics";
        
        
        // The RunningActions bean provides data on currently and previously executed actions.
        // In order to ensure that an action has run in the system, we'll kick off a few here...
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            /** Execute multiple instances of two action types in order to generate data on their execution. This test will examine those data. */
            public Void execute() throws Throwable
            {
                // Copy a node 10 times - the copy-node action. We don't actually care what the action type is.
                for (int i = 0; i < 10; i++)
                {
                    final Action action = ACTION_SERVICE.createAction(CopyActionExecuter.NAME);
                    Map<String, Serializable> actionParams = new HashMap<String, Serializable>();
                    actionParams.put(CopyActionExecuter.PARAM_DESTINATION_FOLDER, folder);
                    actionParams.put(CopyActionExecuter.PARAM_OVERWRITE_COPY, true);
                    action.addParameterValues(actionParams);
                    ACTION_SERVICE.executeAction(action, document);
                }
                
                // Rename and move the source node - this will ensure we have 2 types of Actions for which to retrieve ActionStatistics.
                // copy-action and move-action. This is important as we want our CompositeData array to have more than one element in it
                // in order to allow realistic testing.
                final String nodeName = (String) NODE_SERVICE.getProperty(document, ContentModel.PROP_NAME);
                NODE_SERVICE.setProperty(document, ContentModel.PROP_NAME, "x" + nodeName);
                
                final Action mvAction = ACTION_SERVICE.createAction(MoveActionExecuter.NAME);
                Map<String, Serializable> mvActionParams = new HashMap<String, Serializable>();
                mvActionParams.put(MoveActionExecuter.PARAM_DESTINATION_FOLDER, folder);
                mvAction.addParameterValues(mvActionParams);
                ACTION_SERVICE.executeAction(mvAction, document);
                
                return null;
            }
        });
        
        // Now we'll run the JavaScript to read the JMX CompositeData
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("beanName",      beanName);
                model.put("attributeName", attrName);
                
                ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/management/script/test_readCompositeData.js");
                SCRIPT_SERVICE.executeScript(location, model);
                
                return null;
            }
        });
    }
    
    @Test public void readTabularDataBeanAttribute() throws Exception
    {
        // "Alfresco:Name=Cluster" is the only Alfresco attribute of type TabularData.
        // But to get real data in there would require a clustered Alfresco with failing nodes, so we'll simulate some data.
        final String beanName = "Alfresco:Name=TestJmx";
        final String attrName = "TabularData";
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("beanName",      beanName);
                model.put("attributeName", attrName);
                
                ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/management/script/test_readTabularData.js");
                SCRIPT_SERVICE.executeScript(location, model);
                
                return null;
            }
        });
    }
    
    @Test (expected=ScriptException.class)
    public void writingWronglyTypedValueToJmxAttributeShouldThrowException() throws Exception
    {
        final String beanName = "Alfresco:Name=TestJmx";
        final String attrName = "BooleanPrim";
        
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("beanName",      beanName);
                model.put("attributeName", attrName);
                // A value of type String should not be writable to an attribute of type boolean.
                model.put("newValue",      "Illegal value");
                
                ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/management/script/test_setJmxAttribute.js");
                SCRIPT_SERVICE.executeScript(location, model);
                
                return null;
            }
        });
    }
    
    @Test public void basicJmxOperations() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                Map<String, Object> model = new HashMap<String, Object>();
                
                ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/management/script/test_jmxOperations.js");
                SCRIPT_SERVICE.executeScript(location, model);
                
                return null;
            }
        });
    }
    
    /**
     * JavaScript and Java have quite different handling for overloaded methods and we need to ensure that
     * the JMX JS API does the right thing with JavaScript calls to overloaded JMX operations.
     */
    @Test public void jmxOperationsWithMethodOverloading() throws Exception
    {
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                Map<String, Object> model = new HashMap<String, Object>();
                
                ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/management/script/test_overloadedJmxOperations.js");
                SCRIPT_SERVICE.executeScript(location, model);
                
                return null;
            }
        });
    }
    
    @Test (expected=ScriptException.class) public void invokingJmxOperationWithWrongParametersShouldProduceUsefulException() throws Exception
    {
        // Without specific handling, this could lead to an ArrayIndexOutOfBoundsException, which is not very friendly.
        TRANSACTION_HELPER.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                Map<String, Object> model = new HashMap<String, Object>();
                
                ScriptLocation location = new ClasspathScriptLocation("org/alfresco/enterprise/repo/management/script/test_jmxOperationsBadParams.js");
                SCRIPT_SERVICE.executeScript(location, model);
                
                return null;
            }
        });
    }
    
    // Override the default RunAs for this class and use another user instead
    @Test (expected=ScriptException.class)
    @RunAsUser(userName="TEST_USERNAME")
    public void nonAdminUserCannotInvokeOperationsOnJmxJavaScriptRootObject() throws Exception
    {
        basicJmxOperations();
    }
    
    public static interface TestJmxMBean
    {
        // Java primitives
        
        boolean getBooleanPrim();
        void setBooleanPrim(boolean bool);
        
        char getCharPrim();
        void setCharPrim(char char_);
        
        byte getBytePrim();
        void setBytePrim(byte byte_);
        
        int getIntPrim();
        void setIntPrim(int int_);
        
        long getLongPrim();
        void setLongPrim(long long_);
        
        float getFloatPrim();
        void setFloatPrim(float float_);
        
        double getDoublePrim();
        void setDoublePrim(double double_);
        
        
        // Java objects
        
        Boolean getBoolean();
        void setBoolean(Boolean bool);
        
        void      setChar(Character char_);
        Character getChar();
        
        Byte getByte();
        void setByte(Byte byte_);
        
        Integer getInt();
        void    setInt(Integer int_);
        
        Long getLong();
        void setLong(Long long_);
        
        Float getFloat();
        void setFloat(Float float_);
        
        Double getDouble();
        void setDouble(Double double_);
        
        String getString();
        void setString(String string);
        
        String[] getStringArray();
        void setStringArray(String[] stringArray);
        
        List<String> getStringList();
        void setStringList(List<String> stringList);
        
        Date getDate();
        void setDate(Date newDate);
        
        TabularData getTabularData() throws OpenDataException;
        // All Tabular Data in Alfresco is read-only, so no support for writes. yet.
        
        // Operations
        void noParams();
        void pushString(String arg);
        String[] fetchStringArray();
        String reverseString(String arg);
        void multipleParams(String s, int i);
        void throwException();
    }
    
    public static class TestJmx implements TestJmxMBean
    {
        private boolean      booleanPrim = true;
        private char         charPrim    = 'n';
        private byte         bytePrim    = 127;
        private int          intPrim     = 65535;
        private long         longPrim    = 65535L;
        private float        floatPrim   = 2.5f;
        private double       doublePrim  = 3.5;
        
        private Boolean      boolean_    = true;
        private Character    char_       = 'n';
        private Byte         byte_       = 127;
        private Integer      int_        = 65535;
        private Long         long_       = 65535L;
        private Float        float_      = 2.5f;
        private Double       double_     = 3.5;
        private String       string      = "Hello";
        private String[]     stringArray = new String[] {"Hello", "World"};
        private List<String> stringList  = Arrays.asList(stringArray);
        @SuppressWarnings("deprecation")
        private Date         date        = new Date(2010, 6, 15);
        
        @Override public boolean getBooleanPrim()          { return this.booleanPrim; }
        @Override public void setBooleanPrim(boolean bool) { this.booleanPrim = bool; }
        
        @Override public char getCharPrim()           { return this.charPrim; }
        @Override public void setCharPrim(char char_) { this.charPrim = char_; }
        
        @Override public byte getBytePrim()           { return this.bytePrim; }
        @Override public void setBytePrim(byte byte_) { this.bytePrim = byte_; }
        
        @Override public int getIntPrim()          { return this.intPrim; }
        @Override public void setIntPrim(int int_) { this.intPrim = int_; }
        
        @Override public long getLongPrim()           { return this.longPrim; }
        @Override public void setLongPrim(long long_) { this.longPrim = long_; }
        
        @Override public float getFloatPrim()            { return this.floatPrim; }
        @Override public void setFloatPrim(float float_) { this.floatPrim = float_; }
        
        @Override public double getDoublePrim()             { return this.doublePrim; }
        @Override public void setDoublePrim(double double_) { this.doublePrim = double_; }
        
        
        
        @Override public void setBoolean(Boolean bool) { this.boolean_ = bool; }
        @Override public Boolean getBoolean()          { return boolean_; }
        
        @Override public void setChar(Character char_) { this.char_ = char_; }
        @Override public Character getChar()           { return char_; }
        
        @Override public Byte getByte()           { return byte_; }
        @Override public void setByte(Byte byte_) { this.byte_ = byte_; }
        
        @Override public Integer getInt()          { return int_; }
        @Override public void setInt(Integer int_) { this.int_ = int_; }
        
        @Override public Long getLong()           { return long_; }
        @Override public void setLong(Long long_) { this.long_ = long_; }
        
        @Override public Float getFloat()            { return float_; }
        @Override public void setFloat(Float float_) { this.float_ = float_; }
        
        @Override public Double getDouble()             { return double_; }
        @Override public void setDouble(Double double_) { this.double_ = double_; }
        
        @Override public String getString()            { return string; }
        @Override public void setString(String string) { this.string = string; }
        
        @Override public String[] getStringArray()                 { return stringArray; }
        @Override public void setStringArray(String[] stringArray) { this.stringArray = stringArray; }
        
        @Override public List<String> getStringList()                { return stringList; }
        @Override public void setStringList(List<String> stringList) { this.stringList = stringList; }
        
        @Override public Date getDate()             { return date; }
        @Override public void setDate(Date newDate) { this.date = newDate; }
        
        @Override public TabularData getTabularData() throws OpenDataException
        {
            // This code is loosely based on product code taken from org.alfresco.enterprise.repo.management.ClusterInfo
            CompositeType rowType = new CompositeType("Row typename",                    // type name
                                                      "Row description",                 // description
                                                      new String[] {"item1", "item2"},           // item names
                                                      new String[] {"item1 desc", "item2 desc"}, // item descriptions
                                                      new OpenType<?>[] {SimpleType.STRING, SimpleType.STRING}); // item types
            
            TabularType tType = new TabularType("Test Tabular type",              // type name
                                                "Test Tabular type description",  // description
                                                rowType,                          // row type
                                                new String[] {"item1", "item2"}); // index names, which must be the same as the item names above
            
            final TabularDataSupport table = new TabularDataSupport(tType);
            
            Map<String, String> itemNamesToValues = new HashMap<String, String>();
            itemNamesToValues.put("item1", "Alpha");
            itemNamesToValues.put("item2", "Beta");
            CompositeDataSupport value1 = new CompositeDataSupport(rowType, itemNamesToValues);
            table.put(value1);
            
            Map<String, String> itemNamesToValues2 = new HashMap<String, String>();
            itemNamesToValues2.put("item1", "Gamma");
            itemNamesToValues2.put("item2", "Delta");
            CompositeDataSupport value2 = new CompositeDataSupport(rowType, itemNamesToValues2);
            table.put(value2);
            
            return table;
        }
        
        
        // Test JMX operations
        @Override public void noParams()                      { System.err.println("Called 'noParams' operation."); }
        @Override public void pushString(String arg)          { System.err.println("String " + arg + " received"); }
        @Override public String[] fetchStringArray()          { return new String[] { "fetched", "array" }; }
        @Override public String reverseString(String arg)     { return new StringBuilder(arg).reverse().toString(); }
        @Override public void multipleParams(String s, int i) { System.err.println("Received args: " + s + ", " + i); }
        @Override public void throwException()                { throw new AlfrescoRuntimeException("Intentionally disallowed."); }
    }
    
    
    public static interface TestOverloadedJmxMBean
    {
        String overloadedMethodVariesByParamCount();
        String overloadedMethodVariesByParamCount(String s1);
        String overloadedMethodVariesByParamCount(String s1, String s2);
        
        String overloadedMethodVariesByParamType(String s);
        String overloadedMethodVariesByParamType(int i);
    }
    
    public static class TestOverloadedJmx implements TestOverloadedJmxMBean
    {
        @Override public String overloadedMethodVariesByParamCount()
        {
            System.err.println("Called 'overloadedMethodVariesByParamCount()' operation.");
            return "void";
        }
        @Override public String overloadedMethodVariesByParamCount(String s1)
        {
            System.err.println("Called 'overloadedMethodVariesByParamCount(s1)' operation.");
            return s1;
        }
        @Override public String overloadedMethodVariesByParamCount(String s1, String s2)
        {
            System.err.println("Called 'overloadedMethodVariesByParamCount(s1, s2)' operation.");
            return s1 + ',' + s2;
        }
        
        @Override public String overloadedMethodVariesByParamType(String s) { return s; }
        @Override public String overloadedMethodVariesByParamType(int i) { return "int: " + i; }
    }
}
