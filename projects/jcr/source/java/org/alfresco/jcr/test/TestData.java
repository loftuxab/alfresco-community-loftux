package org.alfresco.jcr.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.alfresco.jcr.repository.RepositoryFactory;
import org.alfresco.repo.importer.ImporterBootstrap;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.MutableAuthenticationDao;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.debug.NodeStoreInspector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestData
{
    public static final String TEST_WORKSPACE = "test";

    
    /**
     * Generate Test Workspace within Repository
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        ApplicationContext context = new ClassPathXmlApplicationContext("org/alfresco/jcr/test/test-context.xml");
        generateTestData(context, TEST_WORKSPACE);
    }
   
    
    /**
     * Bootstrap Repository with JCR Test Data
     * 
     * @param applicationContext
     * @param workspaceName
     */
    public static void generateTestData(ApplicationContext applicationContext, String workspaceName)
    {
        {
            // Bootstrap Users
            MutableAuthenticationDao authDAO = (MutableAuthenticationDao)applicationContext.getBean("alfDaoImpl");
            if (authDAO.userExists("superuser") == false)
            {
                authDAO.createUser("superuser", null);
            }
            if (authDAO.userExists("user") == false)
            {
                authDAO.createUser("user", null);
            }
            if (authDAO.userExists("anonymous") == false)
            {
                authDAO.createUser("anonymous", null);
            }
        }

        try
        {
            // Bootstrap Workspace Test Data
            StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, workspaceName);
            
            ImporterBootstrap bootstrap = new ImporterBootstrap();
            bootstrap.setAuthenticationComponent((AuthenticationComponent)applicationContext.getBean("authenticationComponent"));
            bootstrap.setImporterService((ImporterService)applicationContext.getBean(ServiceRegistry.IMPORTER_SERVICE.getLocalName()));
            bootstrap.setNodeService((NodeService)applicationContext.getBean(ServiceRegistry.NODE_SERVICE.getLocalName()));
            bootstrap.setNamespaceService((NamespaceService)applicationContext.getBean(ServiceRegistry.NAMESPACE_SERVICE.getLocalName()));
            bootstrap.setTransactionService((TransactionService)applicationContext.getBean(ServiceRegistry.TRANSACTION_SERVICE.getLocalName()));
            bootstrap.setStoreUrl(storeRef.toString());
            
            List<Properties> views = new ArrayList<Properties>();
            Properties testView = new Properties();
            testView.setProperty("path", "/");
            testView.setProperty("location", "org/alfresco/jcr/test/testData.xml");
            views.add(testView);
            bootstrap.setBootstrapViews(views);
            bootstrap.bootstrap();
            
            System.out.println(NodeStoreInspector.dumpNodeStore((NodeService)applicationContext.getBean(ServiceRegistry.NODE_SERVICE.getLocalName()), storeRef));
        }
        catch(RuntimeException e)
        {
            System.out.println("Exception: " + e);
            e.printStackTrace();
            throw e;
        }
    }

}
