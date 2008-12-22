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
package org.alfresco.module.vti.handler.alfresco.v3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.dic.Permission;
import org.alfresco.module.vti.metadata.model.DocumentBean;
import org.alfresco.module.vti.metadata.model.DwsBean;
import org.alfresco.module.vti.metadata.model.MemberBean;
import org.alfresco.module.vti.metadata.model.SchemaBean;
import org.alfresco.module.vti.metadata.model.SchemaFieldBean;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.site.SiteService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Alfresco implementation of DwsServiceHandler and AbstractAlfrescoDwsServiceHandler
 * 
 * @author PavelYur
 */
public class AlfrescoDwsServiceHandler extends AbstractAlfrescoDwsServiceHandler
{
    private static Log logger = LogFactory.getLog(AlfrescoDwsServiceHandler.class);

    private AuthenticationComponent authenticationComponent;
    private SiteService siteService;
    private ShareUtils shareUtils;

    /**
     * Set authentication component
     * 
     * @param authenticationComponent the authentication component to set ({@link AuthenticationComponent})
     */
    public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
    {
        this.authenticationComponent = authenticationComponent;
    }

    /**
     * Set site service
     * 
     * @param siteService the site service to set ({@link SiteService})
     */
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }

    /**
     * Set share utils
     * 
     * @param shareUtils the share utils to set ({@link ShareUtils})
     */
    public void setShareUtils(ShareUtils shareUtils)
    {
        this.shareUtils = shareUtils;
    }

    /**
     * @see org.alfresco.module.vti.handler.DwsServiceHandler#handleRedirect(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void handleRedirect(HttpServletRequest req, HttpServletResponse resp) throws HttpException, IOException
    {

        String uri = VtiPathHelper.removeSlashes(req.getRequestURI());
        String docLibPath = null;
        
        if (uri.contains("documentLibrary") && req.getAttribute("VALID_SITE_URL") != null)
        {
            int pos = uri.indexOf("documentLibrary");
            docLibPath = uri.substring(pos + "documentLibrary".length());
            uri = uri.substring(0, pos + "documentLibrary".length()) + ".vti";            
        }
        
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
                if (action.equals("documentLibrary") && docLibPath != null && docLibPath.length() != 0)
                {
                    redirectTo = redirectTo + "#path=" + docLibPath;
                }
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

        String redirectionUrl = shareUtils.getShareHostWithPort() + shareUtils.getShareContext() + redirectTo;
        if (logger.isDebugEnabled())
            logger.debug("Executing redirect to URL: '" + redirectionUrl + "'.");

        resp.setHeader("Location", redirectionUrl);
        resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doRemoveDwsUser(org.alfresco.service.cmr.model.FileInfo, java.lang.String)
     */
    public void doRemoveDwsUser(FileInfo dwsFileInfo, String authority)
    {
        siteService.removeMembership(dwsFileInfo.getName(), authority);
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetDwsContentRecursive(org.alfresco.service.cmr.model.FileInfo, java.util.List)
     */
    public void doGetDwsContentRecursive(FileInfo fileInfo, List<DocumentBean> dwsContent)
    {
        String path = pathHelper.toUrlPath(fileInfo);
        FileInfo docLibFileInfo = pathHelper.resolvePathFileInfo(path + "/documentLibrary");

        if (docLibFileInfo == null)
        {
            throw new VtiHandlerException(VtiHandlerException.BAD_URL);
        }

        addDwsContentRecursive(docLibFileInfo, dwsContent, "documentLibrary/");
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doListDwsMembers(org.alfresco.service.cmr.model.FileInfo)
     */
    public List<MemberBean> doListDwsMembers(FileInfo dwsFileInfo)
    {
        List<MemberBean> members = new ArrayList<MemberBean>();
        // gets list of site users names
        Set<String> membersName = siteService.listMembers(dwsFileInfo.getName(), null, null).keySet();
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

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetModelType()
     */
    protected QName doGetModelType()
    {
        return SiteModel.TYPE_SITE;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetUsersPermissions(org.alfresco.service.cmr.model.FileInfo)
     */
    protected List<Permission> doGetUsersPermissions(FileInfo dwsFileInfo)
    {
        List<Permission> permissions = new ArrayList<Permission>();
        String userRole = siteService.getMembersRole(dwsFileInfo.getName(), authenticationComponent.getCurrentUserName());

        if (userRole.equals(SiteModel.SITE_CONSUMER))
        {
        }
        else if (userRole.equals(SiteModel.SITE_CONTRIBUTOR))
        {
            permissions.add(Permission.DELETE_LIST_ITEMS);
            permissions.add(Permission.EDIT_LIST_ITEMS);
            permissions.add(Permission.INSERT_LIST_ITEMS);
        }
        else if (userRole.equals(SiteModel.SITE_COLLABORATOR))
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

        return permissions;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doCreateDocumentSchemaBean(org.alfresco.service.cmr.model.FileInfo, java.util.List)
     */
    protected SchemaBean doCreateDocumentSchemaBean(FileInfo dwsFileInfo, List<SchemaFieldBean> fields)
    {
        return new SchemaBean("Documents", "documentLibrary", fields);
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doDeleteDws(org.alfresco.service.cmr.model.FileInfo, java.lang.String, java.lang.String)
     */
    protected void doDeleteDws(FileInfo dwsFileInfo, String username, String password) throws HttpException, IOException
    {
        shareUtils.deleteSite(username, password, dwsFileInfo.getName());
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doCreateDws(org.alfresco.service.cmr.model.FileInfo, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    protected void doCreateDws(FileInfo parentFileInfo, String title, String username, String password) throws HttpException, IOException
    {
        shareUtils.createSite(username, password, "document-workspace", title, title, "", true);
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetDwsCreationUrl(java.lang.String, java.lang.String)
     */
    protected String doGetDwsCreationUrl(String parentUrl, String title)
    {
        // ensure that new dws will be created in Sites space
        if (!parentUrl.equals(""))
        {
            throw new VtiHandlerException(VtiHandlerException.BAD_URL);
        }

        // replace all illegal characters
        title = removeIllegalCharacters(title);

        return parentUrl + "/" + title;
    }

    /**
     * @see org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoDwsServiceHandler#doGetResultBean(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    protected DwsBean doGetResultBean(String parentUrl, String dwsUrl, String host, String context)
    {
        DwsBean dwsBean = new DwsBean();
        dwsBean.setDoclibUrl("documentLibrary");
        dwsBean.setUrl(host + context + dwsUrl);
        dwsBean.setParentWeb(parentUrl);
        return dwsBean;
    }
}