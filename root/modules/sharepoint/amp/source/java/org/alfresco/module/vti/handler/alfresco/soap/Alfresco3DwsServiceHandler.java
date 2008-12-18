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

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.endpoints.EndpointUtils;
import org.alfresco.module.vti.endpoints.WebServiceErrorCodeException;
import org.alfresco.module.vti.handler.alfresco.ShareUtils;
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
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.site.SiteService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.Pair;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Alfresco3DwsServiceHandler implements DwsServiceHandler
{    
    private FileFolderService fileFolderService;
    private NodeService nodeService;
    private TransactionService transactionService;
    private PermissionService permissionService;
    private AuthenticationService authenticationService;
    private AuthenticationComponent authenticationComponent;
    private PersonService personService;
    private SiteService siteService;
    private ShareUtils shareUtils;

    private VtiPathHelper pathHelper;
    
    private Map<String, String> pagesMap;
    
    private static Log logger = LogFactory.getLog(Alfresco3DwsServiceHandler.class); 
    
	public Map<String, String> getPagesMap() {
		return pagesMap;
	}

	public void setPagesMap(Map<String, String> pagesMap) {
		this.pagesMap = pagesMap;
	}

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
    
    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }
    
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
    
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;        
    }

    public void setShareUtils(ShareUtils shareUtils)
    {
        this.shareUtils = shareUtils;
    }
    
    public DwsBean createDws(String parentDwsUrl, String name, List users, String title, List documents)
    {
        // ensure that new dws will be created in Sites space
        if (!parentDwsUrl.equals(""))
        {
            throw VtiException.create(VtiError.V_BAD_URL); 
        }
        
        // replace all illegal characters
        title = removeIllegalCharacters(title);
        
        String dwsUrl = parentDwsUrl + "/" + title;        

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

            Pair<String, String> credentials = EndpointUtils.getCredentials();
            if (credentials == null)
            {
                throw new RuntimeException("Invalid credentilas was provided.");
            }            
            shareUtils.createSite(credentials.getFirst(), credentials.getSecond(), "document-workspace", title, title, "", true);  
            dwsFileInfo = pathHelper.resolvePathFileInfo(dwsUrl);

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

        if (logger.isDebugEnabled()) {
        	logger.debug("Document workspace with name '" + title + "' was successfully created.");
        }

        DwsBean dwsBean = new DwsBean();
        dwsBean.setDoclibUrl("documentLibrary");
        dwsBean.setUrl("http://" + EndpointUtils.getHost() + EndpointUtils.getContext() + dwsUrl);
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
        
        if (logger.isDebugEnabled()) {
        	logger.debug("Folder with url '" + url.substring(url.indexOf('/', 1)) + "' was created in site: " + VtiPathHelper.removeSlashes(EndpointUtils.getDwsFromUri()) + ".");
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

            Pair<String, String> credentials = EndpointUtils.getCredentials();
            if (credentials == null)
            {
                throw new RuntimeException("Invalid credentilas was provided.");
            }
            shareUtils.deleteSite(credentials.getFirst(), credentials.getSecond(), dwsFileInfo.getName());

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
        
        if (logger.isDebugEnabled()) {
        	logger.debug("Document workspace with name '" + dwsFileInfo.getName() + "' was successfully deleted.");
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
        
        if (logger.isDebugEnabled()) {
        	logger.debug("Folder with url '" + url.substring(url.indexOf('/', 1)) +
        			"' was deleted from site: " + VtiPathHelper.removeSlashes(EndpointUtils.getDwsFromUri()) + ".");
        }
    }

    public DwsMetadata getDWSMetaData(String document, String id, boolean minimal) throws Exception
    {        
        String dws = EndpointUtils.getDwsFromUri();
        String host = EndpointUtils.getHost();
        String context = EndpointUtils.getContext();
        
        // get the nodeRef for current dws
        FileInfo dwsNode = pathHelper.resolvePathFileInfo(dws);
        
        if (dwsNode == null)
        {
            throw VtiException.create(VtiError.V_URL_NOT_FOUND);
        }               
        
        DwsMetadata dwsMetadata = new DwsMetadata();
        //TODO set usefull urls
        dwsMetadata.setSubscribeUrl("http://" + host + context + dws + "/subscribe.vti");
        dwsMetadata.setMtgInstance("");
        dwsMetadata.setSettingsUrl("http://" + host + context + dws + "/siteSettings.vti");
        dwsMetadata.setPermsUrl("http://" + host + context + dws + "/siteGroupMembership.vti");
        dwsMetadata.setUserInfoUrl("http://" + host + context + dws + "/userInformation.vti");

        // adding the list of roles
        dwsMetadata.setRoles(permissionService.getSettablePermissions(SiteModel.TYPE_SITE));
        
        // setting the permissions for current user such as add/edit/delete items or users
        List<Permission> permissions = new ArrayList<Permission>();
        String userRole = siteService.getMembersRole(dwsNode.getName(), authenticationComponent.getCurrentUserName());
        
        if (userRole.equals(SiteModel.SITE_CONSUMER))
        {                             
        }       
        else if (userRole.equals(SiteModel.SITE_CONTRIBUTOR))
        {
            permissions.add(Permission.DELETE_LIST_ITEMS);
            permissions.add(Permission.EDIT_LIST_ITEMS);
            permissions.add(Permission.INSERT_LIST_ITEMS);                        
        }
        else if(userRole.equals(SiteModel.SITE_COLLABORATOR))
        {
            permissions.add(Permission.EDIT_LIST_ITEMS);
            permissions.add(Permission.DELETE_LIST_ITEMS);
            permissions.add(Permission.INSERT_LIST_ITEMS);
            permissions.add(Permission.MANAGE_LISTS);                        
        }        
        else if (userRole.equals(SiteModel.SITE_MANAGER))
        {
            permissions.add(Permission.EDIT_LIST_ITEMS);
            permissions.add(Permission.DELETE_LIST_ITEMS);
            permissions.add(Permission.INSERT_LIST_ITEMS);
            permissions.add(Permission.MANAGE_LISTS);
            permissions.add(Permission.MANAGE_ROLES);
            permissions.add(Permission.MANAGE_SUBWEBS);
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
            schemaItems.add(new SchemaBean("Documents", "documentLibrary", fields));

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
        
        if (logger.isDebugEnabled()) {
        	logger.debug("Document workspace meta-data was retrieved for '" + dwsNode.getName() + "' site.");
        }
        
        return dwsMetadata;
    }

    public DwsData getDwsData(String document, String lastUpdate) throws Exception
    {
        DwsData dwsData = new DwsData();       
        
        String dws = EndpointUtils.getDwsFromUri();
        String host = EndpointUtils.getHost();
        String context = EndpointUtils.getContext();
        
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
        
        FileInfo docLibInfo = pathHelper.resolvePathFileInfo(dws + "/documentLibrary");
        
        if (docLibInfo == null)
        {
            throw VtiException.create(VtiError.V_BAD_URL);
        }
        
        addDwsContentRecursive(docLibInfo, dws_content, "documentLibrary/");               
        
        dwsData.setDocumentsList(dws_content); 
        
        dwsData.setDocLibUrl("http://" + host + context + dws + "/documentLibrary.vti");
        
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

        if (logger.isDebugEnabled()) {
        	logger.debug("Document workspace data was retrieved for '" + dwsInfo.getName() + "' site.");
        }

        return dwsData;
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
            
            siteService.removeMembership(dwsInfo.getName(), id);

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}

            throw new WebServiceErrorCodeException(3);
        }
        
        if (logger.isDebugEnabled()) {
        	logger.debug("User with name '" + id + "' was successfully removed from site: " + dwsInfo.getName() + ".");
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
        
        if (logger.isDebugEnabled()) {
        	logger.debug("Rename DWS title from '" + dwsFileInfo.getName() + "' to '" + title + "'.");
        }
        
    }
    
    public void handleRedirect(HttpServletRequest req, HttpServletResponse resp) throws HttpException, IOException
    {
        
        String uri = VtiPathHelper.removeSlashes(req.getRequestURI());
        String redirectTo;
        
        if (!uri.endsWith(".vti"))
        {
            if (logger.isDebugEnabled())
                logger.debug("Redirection to site in browser");
            redirectTo = pagesMap.get("siteInBrowser");
            
            String siteName = uri.substring(uri.lastIndexOf('/') + 1);
                
            redirectTo = redirectTo.replace("...", siteName);
            if (logger.isDebugEnabled())
                logger.debug("Redirection URI: " + redirectTo);
        }
        else
        {            
            // gets the action is performed
            String action = uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf(".vti"));
            if (logger.isDebugEnabled())
                logger.debug("Redirection to specific action: " + action);
            // gets the uri for redirection from configuration
            redirectTo = pagesMap.get(action);
            if (action.equals("userInformation"))
            {
                // redirect to user profile
                String userName = req.getParameter("ID");
                redirectTo = redirectTo.replace("...", userName);                
            }
            else
            {
                // redirect to site information (dashboard, site members ...) 
                String[] parts = uri.split("/");
                String siteName = parts[parts.length - 2];
                redirectTo = redirectTo.replace("...", siteName);
            }
            final String doc = req.getParameter("doc");
            if (doc != null)
            {
                
                NodeRef nodeRef = AuthenticationUtil.runAs(new RunAsWork<NodeRef>()
                        {
                            public NodeRef doWork() throws Exception
                {
                                return pathHelper.resolvePathFileInfo(doc).getNodeRef();
                }
                
                        }, authenticationComponent.getSystemUserName());
                
                redirectTo = redirectTo + "?nodeRef=" + nodeRef;
            }
            if (logger.isDebugEnabled())
                logger.debug("Redirection URI: " + redirectTo);
            
        }       
        
        String redirectionUrl = "http://" + shareUtils.getShareHostWithPort() + redirectTo; 
        if (logger.isDebugEnabled())
            logger.debug("Executing redirect to URL: '" + redirectionUrl + "'.");     
        
        resp.sendRedirect(redirectionUrl);                
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
            
            // word dont show list of documents longer then 99 itmes 
            if (result.size() > 99)
            {
                return;
            }
            
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
     * return list of site members
     */
    private List<MemberBean> getDwsMembers(FileInfo dwsInfo)
    {        
        List<MemberBean> members = new ArrayList<MemberBean>();
        // gets list of site users names
        Set<String> membersName = siteService.listMembers(dwsInfo.getName(), null, null).keySet();
        Iterator<String> userIterator = membersName.iterator();
        
        while (userIterator.hasNext())
        {
            String username = userIterator.next();
            NodeRef personNodeRef = personService.getPerson(username);
            String firstName = nodeService.getProperty(personNodeRef, ContentModel.PROP_FIRSTNAME).toString();
            String lastName = nodeService.getProperty(personNodeRef, ContentModel.PROP_LASTNAME).toString();
            String email = nodeService.getProperty(personNodeRef, ContentModel.PROP_EMAIL).toString();
            members.add(new MemberBean(username, firstName + " " + lastName, username, email, false));
        }  

        return members;     
    } 
    
    
    private String removeIllegalCharacters(String value)
    {        
        return value.replaceAll("[!@#$%\\^&*\\(\\)\\-+=~`:;/\\\\\\[\\]\\{\\}|.,\"'\\s\\?<>]+", "_");
    }
}
