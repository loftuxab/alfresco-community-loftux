/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.module.vti.handler.alfresco.soap;

import java.io.Serializable;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.Pair;
import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.endpoints.EndpointUtils;
import org.alfresco.module.vti.endpoints.WebServiceErrorCodeException;
import org.alfresco.module.vti.handler.alfresco.VtiExceptionUtils;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.handler.soap.DwsServiceHandler;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.soap.dws.AssigneeBean;
import org.alfresco.module.vti.metadata.soap.dws.DocumentBean;
import org.alfresco.module.vti.metadata.soap.dws.DwsBean;
import org.alfresco.module.vti.metadata.soap.dws.DwsData;
import org.alfresco.module.vti.metadata.soap.dws.DwsMetadata;
import org.alfresco.module.vti.metadata.soap.dws.ListInfoBean;
import org.alfresco.module.vti.metadata.soap.dws.MemberBean;
import org.alfresco.module.vti.metadata.soap.dws.Permission;
import org.alfresco.module.vti.metadata.soap.dws.SchemaBean;
import org.alfresco.module.vti.metadata.soap.dws.SchemaFieldBean;
import org.alfresco.module.vti.metadata.soap.dws.UserBean;
import org.alfresco.module.vti.metadata.soap.dws.WorkspaceType;

/**
 * Alfresco implementation of DwsServiceHandler interface
 *
 * @author AndreyAk
 * @author Dmitry Lazurkin
 */
public class AlfrescoDwsServiceHandler implements DwsServiceHandler
{
    private FileFolderService fileFolderService;
    private NodeService nodeService;
    private TransactionService transactionService;
    private PermissionService permissionService;
    private AuthenticationService authenticationService;
    private PersonService personService;

    private VtiPathHelper pathHelper;

    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }
    
    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }    
    
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public DwsMetadata getDWSMetaData(String document, String id, boolean minimal) throws Exception
    {
        String host = EndpointUtils.getHost();
        String context = EndpointUtils.getContext();
        String dws = URLDecoder.decode(document.substring(0, document.lastIndexOf('/')).replaceAll("http://"+host+context+"/", ""), "UTF-8");
        // get the nodeRef for current dws
        FileInfo dwsNode = pathHelper.resolvePathFileInfo(dws);
        
        if (dwsNode == null)
        {
            throw VtiException.create(VtiError.V_URL_NOT_FOUND);
        }               
        
        DwsMetadata dwsMetadata = new DwsMetadata();
        //TODO set usefull urls 
        dwsMetadata.setSubscribeUrl("http://" + host + context);
        dwsMetadata.setMtgInstance("");
        dwsMetadata.setSettingsUrl("http://" + host + context);
        dwsMetadata.setPermsUrl("http://" + host + context);
        dwsMetadata.setUserInfoUrl("http://" + host + context);

        // adding the list of roles
        dwsMetadata.setRoles(permissionService.getSettablePermissions(ContentModel.TYPE_FOLDER));
        
        // setting the permissions for current user such as add/edit/delete items or users
        List<Permission> permissions = new ArrayList<Permission>();
        
        if (permissionService.hasPermission(dwsNode.getNodeRef(), PermissionService.DELETE_CHILDREN) == AccessStatus.ALLOWED)
        {
            permissions.add(Permission.DELETE_LIST_ITEMS);
        }
        
        if (permissionService.hasPermission(dwsNode.getNodeRef(), PermissionService.WRITE) == AccessStatus.ALLOWED)
        {
            permissions.add(Permission.EDIT_LIST_ITEMS);
        }
        
        if(permissionService.hasPermission(dwsNode.getNodeRef(), PermissionService.ADD_CHILDREN) == AccessStatus.ALLOWED)
        {
            permissions.add(Permission.INSERT_LIST_ITEMS);
        }
        
        if (permissionService.hasPermission(dwsNode.getNodeRef(), PermissionService.CHANGE_PERMISSIONS) == AccessStatus.ALLOWED)
        {
            permissions.add(Permission.MANAGE_ROLES);
        }     
        
        if ((permissionService.hasPermission(dwsNode.getNodeRef(), PermissionService.WRITE_PROPERTIES) == AccessStatus.ALLOWED)
                && (permissionService.hasPermission(dwsNode.getNodeRef(), PermissionService.DELETE) == AccessStatus.ALLOWED))
        {
            permissions.add(Permission.MANAGE_WEB);
        }

        // includes information about the schemas, lists, documents, links,
        // and tasks lists of a Document Workspace site
        if (!minimal)
        {
            //set Documents schema
            List<SchemaBean> schemaItems = new ArrayList<SchemaBean>();

            List<String> choices = new ArrayList<String>();

            List<SchemaFieldBean> fields = new ArrayList<SchemaFieldBean>();
            fields.add(new SchemaFieldBean("FileLeafRef", "Invalid", true, choices));
            fields.add(new SchemaFieldBean("_SourceUrl", "Text", false, choices));
            fields.add(new SchemaFieldBean("_SharedFileIndex", "Text", false, choices));
            fields.add(new SchemaFieldBean("Order", "Number", false, choices));
            fields.add(new SchemaFieldBean("Title", "Text", false, choices));
            schemaItems.add(new SchemaBean("Documents", dws, fields));

            dwsMetadata.setSchemaItems(schemaItems);

            //set Documents listInfo for documents list
            List<ListInfoBean> listInfoItems = new ArrayList<ListInfoBean>();
            listInfoItems.add(new ListInfoBean("Documents", false, permissions));
            dwsMetadata.setListInfoItems(listInfoItems);
        }

        //set permissions
        dwsMetadata.setPermissions(permissions);
        dwsMetadata.setHasUniquePerm(true);
        // set the type of Document Workspace site
        dwsMetadata.setWorkspaceType(WorkspaceType.DWS);
        dwsMetadata.setADMode(false);
        // set url to currently opened document
        dwsMetadata.setDocUrl(document);
        dwsMetadata.setMinimal(minimal);
        // gets dwsData information
        DwsData dwsData = getDwsData(document, "");
        dwsData.setMinimal(minimal);
        dwsMetadata.setDwsData(dwsData);
        
        return dwsMetadata;
    }

    public DwsData getDwsData(String document, String lastUpdate) throws Exception
    {
        DwsData dwsData = new DwsData();        
        String host = EndpointUtils.getHost();
        String context = EndpointUtils.getContext();
        String dws = URLDecoder.decode(document.substring(0, document.lastIndexOf('/')).replaceAll("http://"+host+context+"/", ""), "UTF-8");
        
        FileInfo dwsInfo = pathHelper.resolvePathFileInfo(dws); 
        
        if (dwsInfo == null)
        {
            throw VtiException.create(VtiError.V_URL_NOT_FOUND);
        }
        
        // set the title of currently opened Document Workspace site
        Serializable title = nodeService.getProperty(dwsInfo.getNodeRef(), ContentModel.PROP_TITLE);   
        
        if (title == null)
        {
            dwsData.setTitle(nodeService.getProperty(dwsInfo.getNodeRef(), ContentModel.PROP_NAME).toString());
        }
        else
        {
            dwsData.setTitle(title.toString());
        }      
        
        // setting the Documents list for current Document Workspace site
        List<DocumentBean> dws_content = new ArrayList<DocumentBean>();        
        
        addDwsContentRecursive(dwsInfo, dws_content, dws + "/");               
        
        dwsData.setDocumentsList(dws_content);        

        dwsData.setLastUpdate(lastUpdate);
        
        // setting currently authenticated user
        UserBean user = getCurrentUser();
        dwsData.setUser(user);    
        
        // if current user has permission to view dws users then collect information about dws users
        if (permissionService.hasPermission(dwsInfo.getNodeRef(), PermissionService.READ_PERMISSIONS) == AccessStatus.ALLOWED)
        {
            List<MemberBean> members = getDwsMembers(dwsInfo);
            dwsData.setMembers(members);
            
            List<AssigneeBean> assignees = new ArrayList<AssigneeBean>();
            for (MemberBean member : members)
            {
                assignees.add(new AssigneeBean(member.getId(), member.getName(), member.getLoginName()));
            }
            dwsData.setAssignees(assignees);
        }

        return dwsData;
    }

    public DwsBean createDws(String parentDwsUrl, String name, List users, String title, List documents)
    {
        String dwsUrl = title;
        if (parentDwsUrl.length() != 0)
        {
            dwsUrl = parentDwsUrl + "/" + dwsUrl;
        }

        FileInfo dwsFileInfo = pathHelper.resolvePathFileInfo(dwsUrl);

        if (dwsFileInfo != null)
        {
            throw new WebServiceErrorCodeException(13);
        }

        Pair<String, String> parentChildPaths = VtiPathHelper.splitPathParentChild(dwsUrl);

        String parentPath = parentChildPaths.getFirst();
        FileInfo parentFileInfo = pathHelper.resolvePathFileInfo(parentPath);
        if (parentFileInfo == null)
        {
            throw VtiException.create(VtiError.V_BAD_URL);
        }

        String dwsName = parentChildPaths.getSecond();
        if (dwsName.length() == 0)
        {
            throw VtiException.create(VtiError.V_BAD_URL);            
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            dwsFileInfo = fileFolderService.create(parentFileInfo.getNodeRef(), dwsName, ContentModel.TYPE_FOLDER);           

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}

            throw VtiExceptionUtils.createRuntimeException(e);
        }

        DwsBean dwsBean = new DwsBean();
        dwsBean.setDoclibUrl(dwsUrl);
        dwsBean.setUrl("http://" + EndpointUtils.getHost() + EndpointUtils.getContext() + "/");
        dwsBean.setParentWeb(parentDwsUrl);

        return dwsBean;
    }

    public void createFolder(String url)
    {        
        
        FileInfo folderFileInfo = pathHelper.resolvePathFileInfo(url);

        if (folderFileInfo != null)
        {
            throw new WebServiceErrorCodeException(13);
        }

        Pair<String, String> parentChildPaths = VtiPathHelper.splitPathParentChild(url);

        String parentPath = parentChildPaths.getFirst();
        FileInfo parentFileInfo = pathHelper.resolvePathFileInfo(parentPath);
        if (parentFileInfo == null)
        {
            throw new WebServiceErrorCodeException(10);
        }

        String dwsName = parentChildPaths.getSecond();
        if (dwsName.length() == 0)
        {
            throw new WebServiceErrorCodeException(10);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            folderFileInfo = fileFolderService.create(parentFileInfo.getNodeRef(), dwsName, ContentModel.TYPE_FOLDER);            

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}

            throw VtiExceptionUtils.createRuntimeException(e);
        }
    }

    public void deleteFolder(String url)
    {        
        
        FileInfo folderFileInfo = pathHelper.resolvePathFileInfo(url);

        if (folderFileInfo == null)
        {
            throw new WebServiceErrorCodeException(10);
        }

        if (folderFileInfo.isFolder() == false)
        {
            throw new WebServiceErrorCodeException(10);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            fileFolderService.delete(folderFileInfo.getNodeRef());

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}

            throw VtiExceptionUtils.createRuntimeException(e);
        }
    }

    public void deleteDws(String dwsUrl)
    {
        FileInfo dwsFileInfo = pathHelper.resolvePathFileInfo(dwsUrl);

        if (dwsFileInfo == null)
        {
            throw new WebServiceErrorCodeException(10);
        }

        if (dwsFileInfo.isFolder() == false)
        {
            throw new WebServiceErrorCodeException(10);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            fileFolderService.delete(dwsFileInfo.getNodeRef());

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}

            throw VtiExceptionUtils.createRuntimeException(e);
        }
    }

    public void renameDws(String oldDwsUrl, String title)
    {
        FileInfo dwsFileInfo = pathHelper.resolvePathFileInfo(oldDwsUrl);

        if (dwsFileInfo == null)
        {
            throw new WebServiceErrorCodeException(10);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            nodeService.setProperty(dwsFileInfo.getNodeRef(), ContentModel.PROP_TITLE, title);

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}

            throw VtiExceptionUtils.createRuntimeException(e);
        }
    }

    public void removeDwsUser(String dwsUrl, String id)
    {
        FileInfo dwsInfo = pathHelper.resolvePathFileInfo(dwsUrl);
        
        if ( dwsInfo == null)
        {
            throw new WebServiceErrorCodeException(10);
        }
        
        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();
            
            permissionService.clearPermission(dwsInfo.getNodeRef(), id);

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}

            throw VtiExceptionUtils.createRuntimeException(e);
        }
        
        
    }
    
    
    /*
     * Collect information about files and folders in current dws     
     */
    private void addDwsContentRecursive(FileInfo dwsInfo, List<DocumentBean> result, String rootName)
    {        
        
        List<FileInfo> fileInfoList = fileFolderService.list(dwsInfo.getNodeRef());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        
        for (FileInfo fileInfo:fileInfoList)
        {
            // do not show working copies
            if (!fileInfo.isFolder() && nodeService.hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
            {                
                continue;
            }
            String id = "";
            String progID =  "";
            String fileRef =  rootName + fileInfo.getName();
            String objType = (fileInfo.isFolder()) ? "1":"0";
            String created = format.format(fileInfo.getCreatedDate());
            String author = (String)nodeService.getProperty(fileInfo.getNodeRef(), ContentModel.PROP_AUTHOR);
            String modified = format.format(fileInfo.getModifiedDate());
            String editor = (String)nodeService.getProperty(fileInfo.getNodeRef(), ContentModel.PROP_MODIFIER);
            
            result.add(new DocumentBean(id, progID, fileRef, objType, created, author, modified, editor));          
            
            // enter in other folders recursively
            if (fileInfo.isFolder())
            {
                addDwsContentRecursive(fileInfo, result, rootName + fileInfo.getName() + "/");
            }
        }        
        
    }
    
    /* 
     * return new UserBean object that represent current user
     */
    private UserBean getCurrentUser()
    {
        NodeRef person = personService.getPerson(authenticationService.getCurrentUserName());
        
        String loginName = (String)nodeService.getProperty(person, ContentModel.PROP_USERNAME);
        String email = (String)nodeService.getProperty(person, ContentModel.PROP_EMAIL);
        String firstName = (String)nodeService.getProperty(person, ContentModel.PROP_FIRSTNAME);
        String lastName = (String)nodeService.getProperty(person, ContentModel.PROP_LASTNAME);
        boolean isSiteAdmin = (loginName.equals("admin") ? true : false);
        
        return new UserBean(loginName, firstName + ' ' + lastName, loginName, email, false, isSiteAdmin);
    }   
    
    /*      
     * return list of dws members
     */
    private List<MemberBean> getDwsMembers(FileInfo dwsInfo)
    {        
        List<MemberBean> members = new ArrayList<MemberBean>();
        // gets all permissions for current dws
        Set<AccessPermission> permissions = permissionService.getAllSetPermissions(dwsInfo.getNodeRef());
        
        Set<String> users = new HashSet<String>();
        for (AccessPermission permission : permissions)
        {
            if (permission.getAccessStatus() == AccessStatus.ALLOWED &&
                    (permission.getAuthorityType() == AuthorityType.USER
                     || permission.getAuthorityType() == AuthorityType.GUEST
                     || permission.getAuthorityType() == AuthorityType.GROUP))
            {
                String authority = permission.getAuthority();
                
                if (users.contains(authority))
                {
                    continue;
                }
                else
                {
                    users.add(authority);
                }
                
                NodeRef person = personService.getPerson(authority);
                
                String loginName = (String)nodeService.getProperty(person, ContentModel.PROP_USERNAME);                
                String email = (String)nodeService.getProperty(person, ContentModel.PROP_EMAIL);
                String firstName = (String)nodeService.getProperty(person, ContentModel.PROP_FIRSTNAME);
                String lastName = (String)nodeService.getProperty(person, ContentModel.PROP_LASTNAME);                
                boolean isDomainGroup = (permission.getAuthorityType() == AuthorityType.GROUP ? true : false);
                
                members.add(new MemberBean(loginName, firstName + ' ' + lastName, loginName, email, isDomainGroup));
            }
        }
        
        String currentUser = authenticationService.getCurrentUserName(); 
        if (!users.contains(currentUser))
        {
            NodeRef person = personService.getPerson(currentUser);
            
            String loginName = (String)nodeService.getProperty(person, ContentModel.PROP_USERNAME);                
            String email = (String)nodeService.getProperty(person, ContentModel.PROP_EMAIL);
            String firstName = (String)nodeService.getProperty(person, ContentModel.PROP_FIRSTNAME);
            String lastName = (String)nodeService.getProperty(person, ContentModel.PROP_LASTNAME);                
            
            members.add(new MemberBean(loginName, firstName + ' ' + lastName, loginName, email, false));
        }
        
        return members;     
    }
}