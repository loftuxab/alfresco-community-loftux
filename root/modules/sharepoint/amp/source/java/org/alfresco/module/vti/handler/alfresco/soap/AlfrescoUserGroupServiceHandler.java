package org.alfresco.module.vti.handler.alfresco.soap;

import java.util.LinkedList;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.endpoints.WebServiceErrorCodeException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.handler.soap.UserGroupServiceHandler;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.soap.usergroup.UserBean;

/**
 * Alfresco implementation of UserGroupServiceHandler interface
 *
 * @author Dmitry Lazurkin
 *
 */
public class AlfrescoUserGroupServiceHandler implements UserGroupServiceHandler
{
    private PermissionService permissionService;    
    private PersonService personService;
    private VtiPathHelper pathHelper;
    private NodeService nodeService;
   

    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void addUserCollectionToRole(String dwsUrl, String roleName, List<UserBean> usersList)
    {
        FileInfo dwsFileInfo = pathHelper.resolvePathFileInfo(dwsUrl);

        if (dwsFileInfo == null)
        {
            throw new WebServiceErrorCodeException(10);
        }

        if (dwsFileInfo.isFolder() == false)
        {
            throw VtiException.create(VtiError.V_BAD_URL);
        }

        for (UserBean userBean : usersList)
        {
            if (personService.personExists(userBean.getLogin().substring("ALFRESCO\\".length())))
            {
                permissionService.setPermission(dwsFileInfo.getNodeRef(), userBean.getLogin().substring("ALFRESCO\\".length()), roleName, true);
            }
            else
            {
                // The user does not have sufficient rights
                throw new WebServiceErrorCodeException(3);
            }
        }
    }

    public List<UserBean> getUserLoginFromEmail(String dwsUrl, List<String> emailList)
    {
        List<UserBean> result = new LinkedList<UserBean>();

        for (String loginOrEmail : emailList)
        {
            if (personService.personExists(loginOrEmail))
            {
                result.add(getUserBean(personService.getPerson(loginOrEmail)));
            }
            else
            {
                boolean personFounded = false;
                
                for (NodeRef personNodeRef : personService.getAllPeople())
                {
                    if (nodeService.getProperty(personNodeRef, ContentModel.PROP_EMAIL).equals(loginOrEmail))
                    {
                        result.add(getUserBean(personNodeRef));
                        personFounded = true;
                    }
                }

                if (personFounded == false)
                {
                    UserBean userBean = new UserBean();
                    userBean.setEmail(loginOrEmail);
                    result.add(userBean);
                }
            }
        }

        return result;
    }

    /**
     * Returns user bean for person node reference
     *
     * @param personNodeRef person node reference
     * @return user bean
     */
    private UserBean getUserBean(NodeRef personNodeRef)
    {
        UserBean userBean = new UserBean();

        String userName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_USERNAME));

        String firstName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_FIRSTNAME));
        String lastName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_LASTNAME));
        
        userBean.setDisplayName(firstName + " " + lastName);
        userBean.setEmail(DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_EMAIL)));
        userBean.setLogin("ALFRESCO\\" + userName);

        return userBean;
    }

}
