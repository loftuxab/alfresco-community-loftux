/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.original.test.ws;

import javax.xml.rpc.ServiceException;

import org.alfresco.cmis.test.ws.AbstractService;
import org.alfresco.cmis.test.ws.AbstractServiceClient;
import org.alfresco.repo.webservice.action.Action;
import org.alfresco.repo.webservice.action.ActionItemDefinitionType;
import org.alfresco.repo.webservice.action.ActionServiceLocator;
import org.alfresco.repo.webservice.action.ActionServiceSoapBindingStub;
import org.alfresco.repo.webservice.action.Rule;
import org.alfresco.repo.webservice.action.RuleType;
import org.alfresco.repo.webservice.repository.RepositoryServiceLocator;
import org.alfresco.repo.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.repo.webservice.types.NamedValue;
import org.alfresco.repo.webservice.types.Node;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Reference;
import org.alfresco.repo.webservice.types.Store;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Action Service
 */
public class OriginalActionServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(OriginalActionServiceClient.class);

    private static final String WORKSPACE_STORE = "workspace";
    private static final String SPACES_STORE = "SpacesStore";

    private static final String RULE_INCOMMING = "incomming";

    private static final String ACTION_NAME = "add-features";
    private static final String ASPECT_NAME = "aspect-name";

    private static final String ASPECT_VERSIONABLE = "{http://www.alfresco.org/model/content/1.0}versionable";

    private static final String ACTION_TITLE = "Add the versionable aspect to the node.";
    private static final String ACTION_DESCRIPTION = "This will add the verisonable aspect to the node and thus create a version history.";
    private static final String RULE_TITLE = "This rule adds the classificable aspect";

    private Reference rootReference;

    private Predicate predicate;

    private AbstractService repositoryService;

    public OriginalActionServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public void setRepositoryService(AbstractService repositoryService)
    {
        this.repositoryService = repositoryService;
    }

    /**
     * Starts session and initializes Action Service client
     */
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        startSession();

        RepositoryServiceSoapBindingStub repositoryService = getRepositoryService(getServerUrl() + this.repositoryService.getPath());

        Store store = null;
        for (Store cStore : repositoryService.getStores())
        {
            if (WORKSPACE_STORE.equals(cStore.getScheme()) && SPACES_STORE.equals(cStore.getAddress()))
            {
                store = cStore;
                break;
            }
        }
        predicate = new Predicate(null, store, null);
        Node[] nodes = repositoryService.get(predicate);
        rootReference = nodes[0].getReference();
    }

    /**
     * Invokes all methods in Action Service
     */
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        ActionServiceSoapBindingStub actionService = getActionService(getProxyUrl() + getService().getPath());

        actionService.getConditionDefinitions();

        // TODO title nillable? do not work in v3.0 ("blog-post" Action Definition)
        actionService.getActionDefinitions();

        actionService.getActionItemDefinition(ACTION_NAME, ActionItemDefinitionType.action);

        RuleType[] ruletypes = actionService.getRuleTypes();

        actionService.getRuleType(ruletypes[0].getName());

        NamedValue[] parameters = new NamedValue[] { new NamedValue(ASPECT_NAME, false, ASPECT_VERSIONABLE, null) };
        Action action = new Action();
        action.setActionName(ACTION_NAME);
        action.setTitle(ACTION_TITLE);
        action.setDescription(ACTION_DESCRIPTION);
        action.setParameters(parameters);
        Action[] actions = actionService.saveActions(rootReference, new Action[] { action });

        actionService.getActions(rootReference, null);

        actionService.executeActions(predicate, actions);

        actionService.removeActions(rootReference, actions);

        Rule rule = new Rule();
        rule.setRuleTypes(new String[] { RULE_INCOMMING });
        rule.setTitle(RULE_TITLE);
        rule.setAction(action);
        Rule[] rules = actionService.saveRules(rootReference, new Rule[] { rule });

        actionService.getRules(rootReference, null);

        actionService.removeRules(rootReference, rules);
    }

    /**
     * Ends session for Action Service client
     */
    @Override
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
        endSession();
    }

    /**
     * Main method to start client
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-tools-client-context.xml");
        AbstractServiceClient client = (OriginalActionServiceClient) applicationContext.getBean("originalActionServiceClient");
        try
        {
            client.initialize();
            client.invoke();
            client.release();
        }
        catch (Exception e)
        {
            LOGGER.error("Some error occured during client running. Exception message: " + e.getMessage());
        }
    }

    /**
     * Gets stub for Action Service
     * 
     * @param address address where service resides
     * @return ActionServiceSoapBindingStub
     * @throws ServiceException
     */
    private ActionServiceSoapBindingStub getActionService(String address) throws ServiceException
    {
        ActionServiceSoapBindingStub actionService = null;
        ActionServiceLocator locator = new ActionServiceLocator(getEngineConfiguration());
        locator.setActionServiceEndpointAddress(address);
        actionService = (ActionServiceSoapBindingStub) locator.getActionService();
        actionService.setMaintainSession(true);
        actionService.setTimeout(TIMEOUT);
        return actionService;
    }

    /**
     * Gets stub for Repository Service
     * 
     * @param address address where service resides
     * @return RepositoryServiceSoapBindingStub
     * @throws ServiceException
     */
    private RepositoryServiceSoapBindingStub getRepositoryService(String address) throws ServiceException
    {
        RepositoryServiceSoapBindingStub repositoryService = null;
        RepositoryServiceLocator locator = new RepositoryServiceLocator(getEngineConfiguration());
        locator.setRepositoryServiceEndpointAddress(address);
        repositoryService = (RepositoryServiceSoapBindingStub) locator.getRepositoryService();
        repositoryService.setMaintainSession(true);
        repositoryService.setTimeout(TIMEOUT);
        return repositoryService;
    }
}
