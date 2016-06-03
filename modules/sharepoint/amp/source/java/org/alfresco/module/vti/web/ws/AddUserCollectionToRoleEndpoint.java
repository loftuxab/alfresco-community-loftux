package org.alfresco.module.vti.web.ws;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.vti.handler.UserGroupServiceHandler;
import org.alfresco.module.vti.metadata.model.UserBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling AddUserCollectionToRole soap method
 * 
 * @author AndreyAk
 *
 */
public class AddUserCollectionToRoleEndpoint extends AbstractEndpoint
{

    // handler that provides methods for operating with documents and folders
    private UserGroupServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "usergroup";
    
    private static Log logger = LogFactory.getLog(AddUserCollectionToRoleEndpoint.class);

    public AddUserCollectionToRoleEndpoint(UserGroupServiceHandler handler)
    {
        super();
        this.handler = handler;
    }

    /**
     * Adds new user to the site with given role
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    
    @SuppressWarnings("unchecked")
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("Soap Method with name " + getName() + " is started.");
        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);
        
        Element requestElement = soapRequest.getDocument().getRootElement();

        // getting document parameter from request
        if (logger.isDebugEnabled()) 
            logger.debug("Getting role from request.");
        XPath roleNamePath = new Dom4jXPath(buildXPath(prefix, "/AddUserCollectionToRole/roleName"));
        roleNamePath.setNamespaceContext(nc);
        Element roleName = (Element) roleNamePath.selectSingleNode(requestElement);
        
        // getting document parameter from request
        if (logger.isDebugEnabled()) 
            logger.debug("Getting users from request.");        
        XPath usersPath = new Dom4jXPath(buildXPath(prefix, "/AddUserCollectionToRole/usersInfoXml/Users/User"));
        usersPath.setNamespaceContext(nc);
        List<Element> usersElementList = (List<Element>) usersPath.selectNodes(requestElement);

        List<UserBean> usersList = new ArrayList<UserBean>();
        for (Element userElement : usersElementList)
        {
            usersList.add(new UserBean(userElement.attributeValue("Name"), userElement.attributeValue("LoginName"), userElement.attributeValue("Email"), userElement.attributeValue("Notes")));
        }
        
        String role = roleName.getTextTrim();
        if (role != null)
        {
            String dws = getDwsFromUri(soapRequest);
            if (logger.isDebugEnabled()) { 
                String users = "";
                for (UserBean userBean : usersList) {
                    users += userBean.getDisplayName() + ", ";
                }
                logger.debug("Adding users [ " + users + "] with role '" + role + "' to the site '" + dws + "'");
            }            
            handler.addUserCollectionToRole(dws, role, usersList);
            // creating soap response
            soapResponse.getDocument().addElement("AddUserCollectionToRoleResponse", namespace);            
        }

        if (logger.isDebugEnabled())
            logger.debug("Soap Method with name " + getName() + " is finished.");  
        
    }

}
