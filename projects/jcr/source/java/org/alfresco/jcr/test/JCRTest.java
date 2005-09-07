/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jcr.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jcr.Repository;

import org.alfresco.repo.importer.ImporterBootstrap;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.MutableAuthenticationDao;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;
import org.springframework.context.ApplicationContext;


/**
 * Base JCR Test
 * 
 * @author David Caruana
 */
public class JCRTest extends BaseSpringTest
{

    protected Repository repository;
    protected StoreRef storeRef;
    
    
    /**
     * Bootstrap Repository with JCR Test Data
     * 
     * @param applicationContext
     * @param workspaceName
     */
    public static void generateTestRepository(ApplicationContext applicationContext, String workspaceName)
    {
        {
            // Bootstrap Users
            MutableAuthenticationDao authDAO = (MutableAuthenticationDao)applicationContext.getBean("alfDaoImpl");
            if (authDAO.userExists("superuser") == false)
            {
                authDAO.createUser("superuser", null);
            }
            if (authDAO.userExists("readuser") == false)
            {
                authDAO.createUser("readuser", null);
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
        }
        catch(RuntimeException e)
        {
            System.out.println("Exception: " + e);
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        generateTestRepository(applicationContext, storeRef.getIdentifier());
        repository = (Repository)applicationContext.getBean("JCR.Repository");
    }

    @Override
    protected String[] getConfigLocations()
    {
        return new String[] {"classpath:alfresco/jcr-context.xml"};
    }
    
    protected String getWorkspace()
    {
        return storeRef.getIdentifier();
    }

}
