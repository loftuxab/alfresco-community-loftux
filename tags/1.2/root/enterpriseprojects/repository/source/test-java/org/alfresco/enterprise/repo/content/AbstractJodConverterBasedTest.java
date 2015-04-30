/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformerTest;
import org.alfresco.repo.content.transform.ContentTransformer;
import org.alfresco.repo.management.subsystems.ChildApplicationContextFactory;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.alfresco.service.cmr.thumbnail.ThumbnailService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author Neil McErlean
 * @since 3.3
 */
@Ignore("This is an abstract class so don't instaniate it or run it in Junit")
public abstract class AbstractJodConverterBasedTest
{
    private static Log log = LogFactory.getLog(AbstractJodConverterBasedTest.class);

    protected static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();

    protected static ServiceRegistry serviceRegistry;
    protected static TransactionService transactionService;
    protected static NodeService nodeService;
    protected static ContentService contentService;
    protected static ThumbnailService thumbnailService;
    private static Repository repositoryHelper;
    
    private static ChildApplicationContextFactory oooDirectSubsystem;
    private static ChildApplicationContextFactory oooJodcSubsystem;
    
    protected NodeRef contentNodeRef;
    protected LinkedList<NodeRef> nodesToDeleteAfterTest = new LinkedList<NodeRef>();


	/**
	 * This test relies upon customised OpenOffice.org subsystems being available.
	 * The OOoDirect subsystem (usually enabled by default) is disabled and the
	 * OOoJodconverter subsystem (usually disabled by default) is enabled.
	 * @throws Exception
	 */
    @BeforeClass
    public static void initServicesAndRestartOOoSubsystems() throws Exception
    {
    	if (log.isDebugEnabled())
    	{
    		log.debug("initServicesAndRestartOOoSubsystems");
    	}
    	
    	repositoryHelper = (Repository) ctx.getBean("repositoryHelper");
        serviceRegistry = (ServiceRegistry) ctx.getBean(ServiceRegistry.SERVICE_REGISTRY);
        contentService = serviceRegistry.getContentService();
        nodeService = serviceRegistry.getNodeService();
        thumbnailService = serviceRegistry.getThumbnailService();
        transactionService = serviceRegistry.getTransactionService();
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        oooDirectSubsystem = (ChildApplicationContextFactory) ctx.getBean("OOoDirect");
        oooJodcSubsystem = (ChildApplicationContextFactory) ctx.getBean("OOoJodconverter");

    	// Stop the OOoDirect subsystem and restart it with test settings i.e. disabled.
        if (log.isDebugEnabled())
        {
        	log.debug("Disabling OOoDirect");
        }
    	oooDirectSubsystem.stop();
    	oooDirectSubsystem.setProperty("ooo.enabled", "false");
    	oooDirectSubsystem.start();

    	// Stop the OOoJodconverter subsystem and restart it with test settings i.e. enabled.
    	// Also a pool of 3 JodConverter instances, just for fun.
        if (log.isDebugEnabled())
        {
        	log.debug("Enabling OOoJodconverter");
        }
		oooJodcSubsystem.stop();
		oooJodcSubsystem.setProperty("jodconverter.enabled", "true");
		oooJodcSubsystem.setProperty("jodconverter.portNumbers", "2022, 2023, 2024");
		oooJodcSubsystem.start();
    }

    /**
     * Returns <code>true</code> if OpenOffice-based transformations are currently known to
     * be available, else <code>false</code>.
     */
    protected boolean isOpenOfficeAvailable()
    {
    	ContentTransformer transformer = contentService.getTransformer(null, MimetypeMap.MIMETYPE_WORD, -1, MimetypeMap.MIMETYPE_PDF, new TransformationOptions());

    	// A transformer may not be returned here if it is unavailable.
    	if (transformer == null)
    	{
    		return false;
    	}
    	
    	// Maybe it's non-null, but not available.
    	boolean isTransformable = transformer.isTransformable(MimetypeMap.MIMETYPE_WORD, -1, MimetypeMap.MIMETYPE_PDF, null);
    	return isTransformable;
    }

    @Before
    public void createTemporaryNodeRefs() throws Exception
    {
        // Create a content node which will serve as test data for our transformations.
        RetryingTransactionCallback<NodeRef> makeNodeCallback = new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
            	if (log.isDebugEnabled())
            	{
            		log.debug("Creating temporary NodeRefs for testing.");
            	}

            	final NodeRef companyHome = repositoryHelper.getCompanyHome();
                // Create a folder
            	Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>();
            	folderProps.put(ContentModel.PROP_NAME, this.getClass().getSimpleName() + System.currentTimeMillis());
            	
                NodeRef folderNodeRef = nodeService.createNode(companyHome, ContentModel.ASSOC_CONTAINS,
                        ContentModel.ASSOC_CONTAINS, ContentModel.TYPE_FOLDER, folderProps).getChildRef();
                nodesToDeleteAfterTest.add(folderNodeRef);
                
                // Add some content.
                File origFile = AbstractContentTransformerTest.loadQuickTestFile("doc");
                
                Map<QName, Serializable> props = new HashMap<QName, Serializable>();
                props.put(ContentModel.PROP_NAME, "original." + "doc");        
                NodeRef node = nodeService.createNode(
                        folderNodeRef, 
                        ContentModel.ASSOC_CONTAINS, 
                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "original.doc"),
                        ContentModel.TYPE_CONTENT, 
                        props).getChildRef();    
                
                ContentWriter writer = contentService.getWriter(node, ContentModel.PROP_CONTENT, true);
                writer.setMimetype(MimetypeMap.MIMETYPE_WORD);
                writer.setEncoding("UTF-8");
                writer.putContent(origFile);
                
                return node;
            }
        };
        contentNodeRef = transactionService.getRetryingTransactionHelper().doInTransaction(makeNodeCallback);
        this.nodesToDeleteAfterTest.add(contentNodeRef);
    }

    @After
    public void deleteTemporaryNodeRefs()
    {
		// Tidy up the test nodes we created
        RetryingTransactionCallback<Void> deleteNodeCallback = new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
            	// Delete them in reverse order.
            	for (Iterator<NodeRef> iter = nodesToDeleteAfterTest.descendingIterator(); iter.hasNext(); )
            	{
            		NodeRef nextNodeToDelete = iter.next();
            		
            		if (nodeService.exists(nextNodeToDelete))
            		{
            			if (log.isDebugEnabled())
            			{
            				log.debug("Deleting temporary node " + nextNodeToDelete);
            			}
            			nodeService.deleteNode(nextNodeToDelete);
            		}
            	}
                
                return null;
            }
        };
        transactionService.getRetryingTransactionHelper().doInTransaction(deleteNodeCallback);
    }

    @AfterClass
    public static void stopOOoSubsystems() throws Exception
    {
        // Put the OOoDirect and OOoJodconverter subsystems back to their install settings.
        // i.e. OOoDirect enabled, OOoJodconverter disabled.
        if (log.isDebugEnabled())
        {
        	log.debug("Re-enabling OOoDirect");
        }
        oooDirectSubsystem.stop();
        oooDirectSubsystem.setProperty("ooo.enabled", "true");
        oooDirectSubsystem.start();
        
        if (log.isDebugEnabled())
        {
        	log.debug("Disabling OOoJodconverter");
        }
        oooJodcSubsystem.stop();
        oooJodcSubsystem.setProperty("jodconverter.enabled", "false");
        oooJodcSubsystem.start();
        
        oooDirectSubsystem.stop();
        oooJodcSubsystem.stop();
    }
}
