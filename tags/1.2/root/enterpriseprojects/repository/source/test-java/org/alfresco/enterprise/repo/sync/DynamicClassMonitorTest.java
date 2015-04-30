/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.test.junitrules.ApplicationContextInit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 * Integration tests sync of dynamic models {@link DynamicClassMonitor}.
 */
public class DynamicClassMonitorTest 
{
	// Rule to initialise the default Alfresco spring configuration
    public static ApplicationContextInit APP_CONTEXT_INIT = new ApplicationContextInit();
   
    private static ContentService              CONTENT_SERVICE;
    private static NodeService                 NODE_SERVICE;
    private static RetryingTransactionHelper   TRANSACTION_HELPER;
    private static DynamicClassMonitor			DYNAMIC_CLASS_MONITOR;
    private static SyncChangeMonitor			SYNC_CHANGE_MONITOR;
    private static NodeRef						COMPANY_HOME;
    private static NodeRef						MODELS;
    private static DictionaryService			DICTIONARY_SERVICE;
    
    private static Log logger = LogFactory.getLog(DynamicClassMonitorTest.class);
    
    @BeforeClass public static void initStaticData() throws Exception
    {
        CONTENT_SERVICE           = APP_CONTEXT_INIT.getApplicationContext().getBean("contentService", ContentService.class);
        NODE_SERVICE              = APP_CONTEXT_INIT.getApplicationContext().getBean("nodeService", NodeService.class);
        TRANSACTION_HELPER        = APP_CONTEXT_INIT.getApplicationContext().getBean("retryingTransactionHelper", RetryingTransactionHelper.class);
        DYNAMIC_CLASS_MONITOR     = APP_CONTEXT_INIT.getApplicationContext().getBean("syncDynamicClassMonitor", DynamicClassMonitor.class);
        SYNC_CHANGE_MONITOR       = APP_CONTEXT_INIT.getApplicationContext().getBean("syncChangeMonitor", SyncChangeMonitor.class);
        DICTIONARY_SERVICE        = APP_CONTEXT_INIT.getApplicationContext().getBean("dictionaryService", DictionaryService.class);

        Repository repositoryHelper = (Repository) APP_CONTEXT_INIT.getApplicationContext().getBean("repositoryHelper");
        COMPANY_HOME = repositoryHelper.getCompanyHome();
        
        
   }
    
    private static String TEST_MODEL_1 = "classpath:synctest/syncTestModel.xml";
    private static String MODEL_NAME_1 = "DynamicClassMonitorTest.xml";
    
    private static String TEST_MODEL_2 = "classpath:synctest/syncTestModel2.xml";
    private static String MODEL_NAME_2 = "DynamicClassMonitorTest2.xml";
    
    /**
     * testCreate  loads a dynamic model into the data dictionary and checks that the exoected properties and types are registered with 
     * the sync tracker 
     * 
     * @throws Exception
     */
    @Test 
    public void testCreate() throws Exception
    {     	
    	if(logger.isDebugEnabled())
    	{
    		logger.debug("Start testCreate");
    	}
    	
    	
    	// Upload the test model to the data dictionary
    	final RetryingTransactionHelper.RetryingTransactionCallback<NodeRef> cb = new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>()
    	{
			@Override
			public NodeRef execute() throws Throwable 
			{
		    	// Upload the test model to the data dictionary
		        Resource resource = new DefaultResourceLoader().getResource(TEST_MODEL_1);
		        assertNotNull("Unable to find " + TEST_MODEL_1, resource);
		        
		        NodeRef dd = NODE_SERVICE.getChildByName(COMPANY_HOME, ContentModel.ASSOC_CONTAINS, "Data Dictionary");
		        NodeRef models = NODE_SERVICE.getChildByName(dd, ContentModel.ASSOC_CONTAINS, "Models");
		        NodeRef model = NODE_SERVICE.getChildByName(models, ContentModel.ASSOC_CONTAINS, MODEL_NAME_1);
		        if(model == null)
		        {
		        	HashMap<QName, Serializable> properties = new HashMap<QName, Serializable> ();
		        	
		        	properties.put(ContentModel.PROP_NAME, MODEL_NAME_1);
		        	properties.put(ContentModel.PROP_TITLE, MODEL_NAME_1);
		        	properties.put(ContentModel.PROP_DESCRIPTION, "Test Model for DynamicClassMonitorTest");
		        	properties.put(ContentModel.PROP_MODEL_ACTIVE, true);
		        	
		        	ChildAssociationRef modelRef = NODE_SERVICE.createNode(models, ContentModel.ASSOC_CONTAINS, 
		        			QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, MODEL_NAME_1), 
		        			ContentModel.TYPE_DICTIONARY_MODEL, properties);
		        	model = modelRef.getChildRef();
		        	ContentWriter writer = CONTENT_SERVICE.getWriter(model, ContentModel.PROP_CONTENT, true);
		        	writer.setMimetype(MimetypeMap.MIMETYPE_XML);
		        	writer.setEncoding("UTF-8");
		        	writer.putContent(resource.getInputStream());
		        	
		        	if(logger.isDebugEnabled())
		        	{
		        		logger.debug("written test model to models folder");
		        	}	
		        }

				return model;
			}
    		
    	};
    	

    	/*
    	 * Load the test model
    	 */
    	final NodeRef model = AuthenticationUtil.runAsSystem(
    	    new RunAsWork<NodeRef>()
    	    {
    	        @Override
    	        public NodeRef doWork() throws Exception
    	        {
    	            return TRANSACTION_HELPER.doInTransaction(   
    	            			cb , false, true);
    	        }
    	    }
        );
    	
     	/*
    	 * Force the data dictionary to load the new model
    	 */
    	logger.debug("get all models model=" + model);
    	DICTIONARY_SERVICE.getAllModels();
    	logger.debug("got all models");
      	
    	/*
    	 * Validate that the SyncChangeMonitor is looking for the test model aspects and types
    	 */ 
    	List<QName> aspects = SYNC_CHANGE_MONITOR.getAspectsToTrack();
    	List<QName> properties = SYNC_CHANGE_MONITOR.getPropertiesToTrack();
    	
    	// Aspect statically loaded
    	assertTrue("Titled not being tracked", aspects.contains(ContentModel.ASPECT_TITLED));
    	assertTrue("Name not being tracked", properties.contains(ContentModel.PROP_NAME));
    	
    	// Dynamically loaded stuff
    	String TEST_FOO_URI = "http://www.alfresco.org/syncTest/foosync/1.0";
    	QName TEST_FOO_ASPECT = QName.createQName(TEST_FOO_URI, "testMarkerAspect");
    	QName TEST_FOO_PROPERTY = QName.createQName(TEST_FOO_URI, "testText");
    	QName TEST_FOO_ASPECT_PROPERTY = QName.createQName(TEST_FOO_URI, "testAspectProperty");
    	
    	if(logger.isDebugEnabled())
    	{
    		for(QName qname : aspects)
    		{
    			logger.debug("Tracked aspect " + qname);
    		}
    		for(QName qname : properties)
    		{
    			logger.debug("Tracked property " + qname);
    		}
    	}
    	
    	assertTrue("Test Aspect not being tracked", aspects.contains(TEST_FOO_ASPECT));
    	assertTrue("Test property not being tracked", properties.contains(TEST_FOO_PROPERTY));
    	assertTrue("Test aspect property not being tracked", properties.contains(TEST_FOO_ASPECT_PROPERTY));
        
    	
    	// Remove model
    	final RetryingTransactionHelper.RetryingTransactionCallback<Void> removeModel = new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
    	{
			@Override
			public Void execute() throws Throwable 
			{
				if(model != null)
				{
					NODE_SERVICE.deleteNode(model);
					logger.debug("removed test model");
				}
				return null;
			}
    		
    	};
    	
    	AuthenticationUtil.runAsSystem(
           new RunAsWork<Void>()
           {
               @Override
        	   public Void doWork() throws Exception
        	   {
        	            return TRANSACTION_HELPER.doInTransaction(   
        	            			removeModel, false, true);
        	   }
        	}
        );
    }
    
    /**
     * Similar test as above but two step process similar to loading via share.  
     * First transaction upload then second transaction sets the model active
     */
    @Test
    public void testCreate2Step()
    {
    	if(logger.isDebugEnabled())
    	{
    		logger.debug("Start testCreate");
    	}
    	
    	// Upload the test model to the data dictionary
    	final RetryingTransactionHelper.RetryingTransactionCallback<NodeRef> loadModel = new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>()
    	{
			@Override
			public NodeRef execute() throws Throwable 
			{
		    	// Upload the test model to the data dictionary
		        Resource resource = new DefaultResourceLoader().getResource(TEST_MODEL_2);
		        assertNotNull("Unable to find " + TEST_MODEL_2, resource);
		        
		        NodeRef dd = NODE_SERVICE.getChildByName(COMPANY_HOME, ContentModel.ASSOC_CONTAINS, "Data Dictionary");
		        NodeRef models = NODE_SERVICE.getChildByName(dd, ContentModel.ASSOC_CONTAINS, "Models");
		        NodeRef model = NODE_SERVICE.getChildByName(models, ContentModel.ASSOC_CONTAINS, MODEL_NAME_2);
		        if(model == null)
		        {
		        	HashMap<QName, Serializable> properties = new HashMap<QName, Serializable> ();
		        	
		        	properties.put(ContentModel.PROP_NAME, MODEL_NAME_2);
		        	properties.put(ContentModel.PROP_TITLE, MODEL_NAME_2);
		        	properties.put(ContentModel.PROP_DESCRIPTION, "Test Model for DynamicClassMonitorTest");
		        	
		        	ChildAssociationRef modelRef = NODE_SERVICE.createNode(models, ContentModel.ASSOC_CONTAINS, 
		        			QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, MODEL_NAME_2), 
		        			ContentModel.TYPE_DICTIONARY_MODEL, properties);
		        	model = modelRef.getChildRef();
		        	ContentWriter writer = CONTENT_SERVICE.getWriter(model, ContentModel.PROP_CONTENT, true);
		        	writer.setMimetype(MimetypeMap.MIMETYPE_XML);
		        	writer.setEncoding("UTF-8");
		        	writer.putContent(resource.getInputStream());
		        	
		        	if(logger.isDebugEnabled())
		        	{
		        		logger.debug("written test model to models folder");
		        	}
		        	
		        }

				return model;
			}
    	};
    	

    	/*
    	 * Load the test model
    	 */
    	final NodeRef model = AuthenticationUtil.runAsSystem(
    	    new RunAsWork<NodeRef>()
    	    {
    	        @Override
    	        public NodeRef doWork() throws Exception
    	        {
    	            return TRANSACTION_HELPER.doInTransaction(   
    	            			loadModel , false, true);
    	        }
    	    }
        );
    	
    	// Upload the test model to the data dictionary
    	final RetryingTransactionHelper.RetryingTransactionCallback<Void> activateModel = new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
    	{
			@Override
			public Void execute() throws Throwable 
			{
				NODE_SERVICE.setProperty(model, ContentModel.PROP_MODEL_ACTIVE, true);
		        return null;
			}
    		
    	};
    	
    	AuthenticationUtil.runAsSystem(
            new RunAsWork<Void>()
        	{
        	   @Override
        	   public Void doWork() throws Exception
        	   {
        	            TRANSACTION_HELPER.doInTransaction(   
        	            activateModel, false, true);
        	            return null;
        	    }
        	}
        );
    	
     	/*
    	 * Force the data dictionary to load the new model
    	 */
    	logger.debug("get all models model=" + model);
    	DICTIONARY_SERVICE.getAllModels();
    	logger.debug("got all models");
      	
    	/*
    	 * Validate that the SyncChangeMonitor is looking for the test model aspects and types
    	 */ 
    	List<QName> aspects = SYNC_CHANGE_MONITOR.getAspectsToTrack();
    	List<QName> properties = SYNC_CHANGE_MONITOR.getPropertiesToTrack();
    	
    	// Aspect statically loaded
    	assertTrue("Titled not being tracked", aspects.contains(ContentModel.ASPECT_TITLED));
    	assertTrue("Name not being tracked", properties.contains(ContentModel.PROP_NAME));
    	
    	// Dynamically loaded stuff
    	String TEST_BAR_URI = "http://www.alfresco.org/syncTest/barsync/1.0";
    	QName TEST_BAR_ASPECT = QName.createQName(TEST_BAR_URI, "testPass");
    
    	
    	if(logger.isDebugEnabled())
    	{
    		for(QName qname : aspects)
    		{
    			logger.debug("Tracked aspect " + qname);
    		}
    		for(QName qname : properties)
    		{
    			logger.debug("Tracked property " + qname);
    		}
    	}
    	
    	assertTrue("Test property not being tracked", aspects.contains(TEST_BAR_ASPECT));        
    	
    	// Remove model
    	final RetryingTransactionHelper.RetryingTransactionCallback<Void> removeModel = new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
    	{
			@Override
			public Void execute() throws Throwable 
			{
				if(model != null)
				{
					NODE_SERVICE.deleteNode(model);
					logger.debug("removed test model");
				}
				return null;
			}
    		
    	};

    	
    	AuthenticationUtil.runAsSystem(
          new RunAsWork<Void>()
        	    {
        	        @Override
        	        public Void doWork() throws Exception
        	        {
        	            return TRANSACTION_HELPER.doInTransaction(   
        	            			removeModel, false, true);
        	        }
        	    }
            );
    
    	
    }
   
 

}
