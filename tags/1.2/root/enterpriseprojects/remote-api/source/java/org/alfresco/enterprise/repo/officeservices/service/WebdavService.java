/*
 * Copyright 2005-2015 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.officeservices.vfs.AlfrescoVirtualFileSystem;
import org.alfresco.enterprise.repo.officeservices.vfs.DocumentNode;
import org.alfresco.enterprise.repo.officeservices.vfs.FolderNode;
import org.alfresco.enterprise.repo.officeservices.vfs.VersionNumber;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.transaction.TransactionService;
import org.apache.chemistry.opencmis.server.shared.ThresholdOutputStreamFactory;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xaldon.officeservices.StandardWebdavService;
import com.xaldon.officeservices.URLEncoder;
import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.datamodel.Guid;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.lists.CheckinType;
import com.xaldon.officeservices.protocol.HtmlEncoder;
import com.xaldon.officeservices.vfs.VFSDocumentNode;
import com.xaldon.officeservices.vfs.VFSNode;
import com.xaldon.officeservices.vfs.VirtualFileSystem;

public class WebdavService extends StandardWebdavService
{

    private static final long serialVersionUID = 5535102938681050458L;
    
    protected VirtualFileSystem vfs;
    
    protected AuthenticationService authenticationService;
    
    protected MimetypeService mimetypeService;

    protected TransactionService transactionService;

    protected ThresholdOutputStreamFactory streamFactory;
    
    protected Logger logger = Logger.getLogger(this.getClass());

    // initialization

    @Override
    public void init() throws ServletException
    {
        super.init();
        
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if(wac == null)
        {
            throw new ServletException("Error initializing Servlet. No WebApplicationContext available.");
        }
        
        vfs = (VirtualFileSystem) wac.getBean("AosVirtualFileSystem");
        if(vfs == null)
        {
            throw new ServletException("Cannot find bean AosVirtualFileSystem in WebApplicationContext.");
        }
        ((AlfrescoVirtualFileSystem)vfs).prepare();
        authenticationService = (AuthenticationService) wac.getBean("AuthenticationService");
        if(authenticationService == null)
        {
            throw new ServletException("Cannot find bean AuthenticationService in WebApplicationContext.");
        }
        mimetypeService = (MimetypeService) wac.getBean("MimetypeService");
        if(mimetypeService == null)
        {
            throw new ServletException("Cannot find bean MimetypeService in WebApplicationContext.");
        }
        transactionService = ((AlfrescoVirtualFileSystem)vfs).getTransactionService();
        streamFactory = ((AlfrescoVirtualFileSystem)vfs).createStreamFactory();
    }
    
    // transaction
    
    @Override
    protected void handleUnexpectedException(String methodName, Exception e, HttpServletRequest req, HttpServletResponse resp)
    {
    	AlfrescoVirtualFileSystem.checkForRetryingException(e);
    	super.handleUnexpectedException(methodName, e, req, resp);
    }
    
    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
    	final String methodname = req.getMethod();
    	final long debugId = System.currentTimeMillis();
    	if(logger.isDebugEnabled())
    	{
            logger.debug("------------------------------< WebDAV: "+req.getMethod()+" >------------------------------");    		
    	}
        final BufferedHttpServletRequest bufferedRequest = new BufferedHttpServletRequest((HttpServletRequest)req, streamFactory);
        try
        {
            transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                	if(logger.isDebugEnabled())
                	{
                        logger.debug("In Retrying transaction: "+methodname+" "+debugId);
                	}
                	WebdavService.super.service(bufferedRequest, resp);
                    return null;
                }
            });
        }
        catch(Throwable t)
        {
        	try
        	{
        		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	}
        	catch(Exception e)
        	{
        		;
        	}
        }
        finally
        {
        	bufferedRequest.close();
        }
        if(logger.isDebugEnabled())
        {
            logger.debug("Finished WebDAV: "+req.getMethod());
        }
    }
    
    // file system

    @Override
    protected String preProcessRequestedPath(String requestedPath)
    {
        if(requestedPath.equals(Const.SERVICE_MAPPING_IN_CONTEXT))
        {
            return "";
        }
        if(requestedPath.startsWith(Const.SERVICE_MAPPING_IN_CONTEXT+"/"))
        {
            return requestedPath.substring(Const.SERVICE_MAPPING_IN_CONTEXT.length());
        }
        logger.warn("The requestedPath sent to the webdav servlet does not start with the expected webdav mapping '"+Const.SERVICE_MAPPING_IN_CONTEXT+"'. Please make sure that the mapping in web.xml corresponds to the WEBDAV_MAPPING_IN_CONTEXT constant.");
        return requestedPath;
    }

    @Override
    public VirtualFileSystem getVirtualFileSystem(UserData userData) throws AuthenticationRequiredException
    {
        return vfs;
    }
    
    @Override
    protected String getSitePrefix(HttpServletRequest request)
    {
        return ((AlfrescoVirtualFileSystem)vfs).getSitePrefix(request);
    }

    protected String getContextRootUrl(HttpServletRequest request)
    {
        return this.getSitePrefix(request);
    }

    @Override
    protected String getEtagForNode(VFSNode node, String sitePrefix, String path)
    {
        try
        {
            if(node instanceof DocumentNode)
            {
                Guid guid = Guid.parse(((DocumentNode)node).getFileInfo().getNodeRef().getId());
                //return "\"" + guid.toString() + "," + ((DocumentNode)node).getVersionNumber().toString() + "\"";
                return "\"" + guid.toString() + "," + Integer.toString(((DocumentNode)node).getDateLastModified(VFSDocumentNode.CALLCONTEXT_HTTPGET).hashCode()) + "\"";
            }
            if(node instanceof FolderNode)
            {
                Guid guid = Guid.parse(((FolderNode)node).getFileInfo().getNodeRef().getId());
                return "\"" + guid.toString() + ",0\"";
            }
        }
        catch(Exception e)
        {
            logger.debug("Error generating Etag for node",e);
        }
        // fall-back if we do not have a unique ID
        return "\"{" + getGuidForNode(node, sitePrefix, path) + "},0\"";
    }
    
    @Override
    protected String getResourceTagForNode(VFSNode node, String sitePrefix, String path)
    {
        try
        {
            if(node instanceof DocumentNode)
            {
                Guid guid = Guid.parse(((DocumentNode)node).getFileInfo().getNodeRef().getId());
                VersionNumber ver = ((DocumentNode)node).getVersionNumber();
                String versionMarker = Integer.toString(ver.getMinor());
                while(versionMarker.length() < 6)
                {
                    versionMarker = "0" + versionMarker;
                }
                versionMarker = Integer.toString(ver.getMajor()) + versionMarker;
                while(versionMarker.length() < 11)
                {
                    versionMarker = "0" + versionMarker;
                }
                return "rt:" + guid.toString() + "@" + versionMarker;
            }
            if(node instanceof FolderNode)
            {
                Guid guid = Guid.parse(((FolderNode)node).getFileInfo().getNodeRef().getId());
                return "rt:" + guid.toString() + "@00000000000";
            }
        }
        catch(Exception e)
        {
            logger.debug("Error generating Etag for node",e);
        }
        // fall-back if we do not have a unique ID
        return "rt:" + getGuidForNode(node, sitePrefix, path) + "@00000000000";
    }
    
    // directory listing

    protected static URLEncoder urlSegmentEncoder = new URLEncoder();

    @Override
    protected boolean handleNonDocumentVFSNodes(UserData userData, VFSNode node, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        boolean debugMode = detectDebugMode(req, resp);
        if( debugMode && performAction(userData, node, req, resp))
        {
            return true;
        }
        String pathInfo = req.getPathInfo();
        String path = req.getServletPath();
        if(pathInfo != null)
        {
            path = path + pathInfo;
        }
        if(!path.endsWith("/"))
        {
            path = path + "/";
        }
        String relativePath = (pathInfo==null) ? "/" : pathInfo;
        if(!relativePath.endsWith("/"))
        {
            relativePath = relativePath + "/";
        }
        String userAgent = req.getHeader("User-Agent");
        if( (userAgent != null) && (userAgent.indexOf("MSIE") >= 0) && ( (userAgent.indexOf("ms-office") >= 0) || (userAgent.indexOf("MSOffice") >= 0) ) )
        {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            logger.error("Blocked a directory listing request from MS-Office. This indicates a broken MS-Office deployment. Please check that the ROOT and the _vti_bin webapps are deployed properly and reachable from the outside!");
        }
        String baseUrl = getSitePrefix(req) + urlEncoder.encode(relativePath);
        List<?> subnodes = node.getEnumerableContainees(userData, VFSNode.CALLCONTEXT_HTTPGET);
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.print("<!DOCTYPE html>\n");
        out.print("<html>\n");
        out.print("<head>\n");
        out.print("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=8\" />\n");
        out.print("<title>Alfresco Directory Listing for ");
        HtmlEncoder.writeEncoded(out,new StringBuffer(relativePath));
        out.print("</title>\n");
        out.print("<style>");
        out.print("<!-- BODY {font-family:Tahoma,Arial,sans-serif; color:black; background-color:white;} H2 {margin-bottom:5px;} A {text-decoration:none;color:blue;} A:visited {text-decoration:none;color:blue;} A:hover {text-decoration:none;color:red;} .listTable { border-spacing: 1px 3px; font-size: 10pt; margin-bottom: 15px; } .listHeader { background-color: #333377; color: #FFFFFF; font-size: 10pt; } .listEven { background-color: #eeeeee; font-size: 10pt; } .listOdd { background-color: #dddddd; font-size: 10pt; } -->");
        out.print("</style>\n");
        if(debugMode)
        {
            writeJavaScript(out);
        }
        out.print("</head>\n");
        out.print("<body>\n");
        out.print("<h2>");
        StringBuffer buildUrl = new StringBuffer(getSitePrefix(req));
        out.print("<a href=\"");
        HtmlEncoder.writeEncoded(out,buildUrl);
        out.print("\">");
        HtmlEncoder.writeEncoded(out,buildUrl);
        out.print("</a>");
        String x = relativePath.substring(1);
        if(x.length() > 0)
        {
            String[] pathSegments = x.split("/");
            for(String segment : pathSegments)
            {
                buildUrl.append('/');
                buildUrl.append(segment);
                out.print("/");
                out.print("<a href=\"");
                HtmlEncoder.writeEncoded(out,buildUrl);
                out.print("\">");
                HtmlEncoder.writeEncoded(out,new StringBuffer(segment));
                out.print("</a>");
            }
        }
        out.print("/");
        out.print("</h2>\n");
        out.print("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\" class=\"listTable\">\n");
        out.print("<tr class=\"listHeader\" >\n");
        out.print("<th align=\"left\" width=\"99%\" style=\"min-width: 100px;\">Name</th>\n");
        out.print("<th align=\"right\" style=\"min-width: 75px;\">Size</th>\n");
        if(!debugMode)
        {
            out.print("<th align=\"left\" style=\"min-width: 250px;\">Type</th>\n");
        }
        out.print("<th align=\"left\" style=\"min-width: 200px;\">Modified</th>\n");
        if(debugMode)
        {
            out.print("<th align=\"left\" style=\"min-width: 250px;\">Locked</th>\n");
            out.print("<th align=\"left\" style=\"min-width: 250px;\">Checked Out</th>\n");
            out.print("<th align=\"left\" style=\"min-width: 175px;\">Actions</th>\n");
        }
        out.print("</tr>\n");
        boolean  oddEvenFlag = false;
        for(int i = 0; i < subnodes.size(); i++)
        {
            out.print("<tr class=\"");
            out.print(oddEvenFlag ? "listOdd" : "listEven");
            oddEvenFlag = !oddEvenFlag;
            out.print("\">\n");
            // Name
            out.print("<td align=\"left\">");
            VFSNode n = (VFSNode)subnodes.get(i);
            out.print("<a href=\"");
            String nodeName = n.getName(VFSNode.CALLCONTEXT_HTTPGET);
            HtmlEncoder.writeEncoded(out,new StringBuffer(baseUrl + urlEncoder.encode(nodeName)));
            out.print("\">");
            HtmlEncoder.writeEncoded(out,new StringBuffer(nodeName));
            out.print("</a>");
            out.print("</td>\n");
            if(n instanceof VFSDocumentNode)
            {
                DocumentNode doc = (DocumentNode)n;
                // Size
                out.print("<td align=\"right\">");
                out.print(doc.getSize(VFSNode.CALLCONTEXT_HTTPPROPFIND));
                out.print("</td>\n");
                if(!debugMode)
                {
                    // Type
                    String displayType = mimetypeService.getDisplaysByMimetype().get(doc.getMimeString(VFSNode.CALLCONTEXT_HTTPPROPFIND));
                    if(displayType != null)
                    {
                        out.print("<td align=\"left\">");
                        out.print(displayType);
                        out.print("</td>\n");
                    }
                    else
                    {
                        out.print("<td align=\"left\">&nbsp;</td>\n");
                    }
                }
                // Modified
                out.print("<td align=\"left\">");
                out.print(doc.getDateLastModified(VFSNode.CALLCONTEXT_HTTPPROPFIND));
                out.print("</td>\n");
                if(debugMode)
                {
                    // Locked
                    out.print("<td align=\"left\">");
                    if(doc.isLocked())
                    {
                        out.print("until ");
                        Date expiry = doc.getLockExpiry();
                        out.print(expiry == null ? "unknown" : expiry);
                        out.print(" by ");
                        String lockOwner = doc.getLockOwner();
                        HtmlEncoder.writeEncoded(out,new StringBuffer(lockOwner == null ? "unknown" : lockOwner));
                    }
                    out.print("</td>\n");
                    // Checked Out
                    out.print("<td align=\"left\">");
                    if(doc.isCheckedOut())
                    {
                        Date checkoutDate = doc.getCheckoutDate();
                        out.print(checkoutDate == null ? "unknown" : checkoutDate);
                        if(doc.isCheckedOutToLocal())
                        {
                            out.print(" to local");
                        }
                        out.print(" by ");
                        String checkoutOwner = doc.getCheckoutOwner();
                        HtmlEncoder.writeEncoded(out,new StringBuffer(checkoutOwner == null ? "unknown" : checkoutOwner));
                    }
                    out.print("</td>\n");
                    // Actions
                    out.print("<td align=\"left\">");
                    if(doc.isLocked())
                    {
                        out.print("<a href=\"");
                        StringBuffer unlockCommand = new StringBuffer("?cmd=unlock&item=");
                        unlockCommand.append(urlSegmentEncoder.encode(n.getName(VFSNode.CALLCONTEXT_HTTPGET)));
                        HtmlEncoder.writeEncoded(out,unlockCommand);
                        out.print("\">Unlock</a>");
                    }
                    else
                    {
                        out.print("<a href=\"");
                        StringBuffer lockCommand = new StringBuffer("?cmd=lock&item=");
                        lockCommand.append(urlSegmentEncoder.encode(n.getName(VFSNode.CALLCONTEXT_HTTPGET)));
                        HtmlEncoder.writeEncoded(out,lockCommand);
                        out.print("\">Lock</a>");
                    }
                    out.print(" ");
                    if(doc.isCheckedOut())
                    {
                        out.print("<a href=\"");
                        StringBuffer checkinCommand = new StringBuffer("?cmd=checkin&item=");
                        checkinCommand.append(urlSegmentEncoder.encode(n.getName(VFSNode.CALLCONTEXT_HTTPGET)));
                        HtmlEncoder.writeEncoded(out,checkinCommand);
                        out.print("\">Checkin</a>");
                        out.print(" ");
                        out.print("<a href=\"");
                        StringBuffer uncheckoutCommand = new StringBuffer("?cmd=uncheckout&item=");
                        uncheckoutCommand.append(urlSegmentEncoder.encode(n.getName(VFSNode.CALLCONTEXT_HTTPGET)));
                        HtmlEncoder.writeEncoded(out,uncheckoutCommand);
                        out.print("\">Uncheckout</a>");
                    }
                    else
                    {
                        out.print("<a href=\"");
                        StringBuffer checkoutCommand = new StringBuffer("?cmd=checkout&item=");
                        checkoutCommand.append(urlSegmentEncoder.encode(n.getName(VFSNode.CALLCONTEXT_HTTPGET)));
                        HtmlEncoder.writeEncoded(out,checkoutCommand);
                        out.print("\">Checkout</a>");
                    }
                    out.print("</td>\n");
                }
            }
            else
            {
                // Size
                out.print("<td align=\"right\">&lt;SUB-DIR&gt;</td>\n");
                if(!debugMode)
                {
                    // Type
                    out.print("<td align=\"left\">&nbsp;</td>\n");
                }
                // Modified
                out.print("<td align=\"left\">&nbsp;</td>\n");
                if(debugMode)
                {
                    // Locked
                    out.print("<td align=\"left\">&nbsp;</td>\n");
                    // Checked Out
                    out.print("<td align=\"left\">&nbsp;</td>\n");
                    // Actions
                    out.print("<td align=\"left\">&nbsp;</td>\n");
                }
            }
            out.print("</tr>\n");
        }
        out.print("</table>\n");
        if(debugMode)
        {
            out.print("Open this folder in ");
            out.print(" <a href=\"");
            out.print("javascript:openInWindowsExplorer('"+baseUrl+"')");
            out.print("\">Windows Explorer</a>");
            out.print("\n<br/>\n");
        }
        out.print("</body>\n");
        out.print("</html>\n");
        return true;
    }
    
    protected void writeJavaScript(PrintWriter out)
    {
        out.print("<script type=\"text/javascript\">\n");
        try
        {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("alfresco/enterprise/aos/WebdavPageJavascript.template");
            if(is != null)
            {
                try
                {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while((line = reader.readLine()) != null)
                    {
                        out.println(line);
                    }
                }
                finally
                {
                    is.close();
                }
            }
        }
        catch (IOException e)
        {
            logger.fatal("Error loading ContentTypes.properties file!", e);
        }
        out.print("</script>\n");
    }
    
    protected boolean detectDebugMode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        boolean debugMode = false;
        Cookie[] cookies = req.getCookies();
        if(cookies != null)
        {
            for(Cookie cookie : cookies)
            {
                if(cookie.getName().equals("aosDebugMode"))
                {
                    debugMode = (cookie.getValue() != null) && cookie.getValue().equals("1");
                }
            }
        }
        if(debugMode)
        {
            if(req.getParameter("nodebug") != null)
            {
                debugMode = false;
                Cookie debugModeCookie = new Cookie("aosDebugMode", "0");
                debugModeCookie.setPath(req.getContextPath());
                resp.addCookie(debugModeCookie);
            }
        }
        else
        {
            if(req.getParameter("debug") != null)
            {
                debugMode = true;
                Cookie debugModeCookie = new Cookie("aosDebugMode", "1");
                debugModeCookie.setPath(req.getContextPath());
                resp.addCookie(debugModeCookie);
            }
        }
        return debugMode;
    }

    protected boolean performAction(UserData userData, VFSNode node, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String cmd = req.getParameter("cmd");
        String item = req.getParameter("item");
        if((cmd==null) || (cmd.length()==0))
        {
            // nothing to do
            return false;
        }
        if("iqy".equalsIgnoreCase(cmd))
        {
            String list = req.getParameter("list");
            if((list==null) || (list.length()==0) )
            {
                // nothing to do
                return false;
            }
            resp.setContentType("text/x-ms-iqy");
            PrintWriter out = resp.getWriter();
            out.print("WEB\n");
            out.print("1\n");
            out.print(getSitePrefix(req)+"/_vti_bin/owssvr.dll?XMLDATA=1&List="+urlSegmentEncoder.encode(list)+"&View={00000000-0000-0000-0000-000000000000}&RowLimit=0\n");
            out.print("\n");
            out.print("Selection={00000000-0000-0000-0000-000000000000}-{11111111-1111-1111-1111-111111111111}\n");
            out.print("EditWebPage=\n");
            out.print("Formatting=None\n");
            out.print("PreFormattedTextToColumns=True\n");
            out.print("ConsecutiveDelimitersAsOne=True\n");
            out.print("SingleBlockTextImport=False\n");
            out.print("DisableDateRecognition=False\n");
            out.print("DisableRedirections=False\n");
            out.print("SharePointApplication="+getSitePrefix(req)+"/_vti_bin\n");
            out.print("SharePointListView={00000000-0000-0000-0000-000000000000}\n");
            out.print("SharePointListName="+list+"\n");
            return true;
        }
        if((item==null) || (item.length()==0) )
        {
            // nothing to do
            return false;
        }
        VFSNode itemNode = node.getContainmentByName(userData, item, VFSNode.CALLCONTEXT_HTTPPROPFIND);
        if( (itemNode == null) || (!(itemNode instanceof VFSDocumentNode)))
        {
            return false;
        }
        VFSDocumentNode itemDocument = (VFSDocumentNode)itemNode;
        if(cmd.equalsIgnoreCase("checkout"))
        {
            itemDocument.checkout(0, null, VFSNode.CALLCONTEXT_UNDEFINED);
        }
        if(cmd.equalsIgnoreCase("uncheckout"))
        {
            itemDocument.uncheckout(false, VFSNode.CALLCONTEXT_UNDEFINED);
        }
        if(cmd.equalsIgnoreCase("checkin"))
        {
            itemDocument.checkin("", false, VFSNode.CALLCONTEXT_UNDEFINED, CheckinType.MAJOR);
        }
        if(cmd.equalsIgnoreCase("lock"))
        {
            itemDocument.checkout(10, null, VFSNode.CALLCONTEXT_UNDEFINED);
        }
        if(cmd.equalsIgnoreCase("unlock"))
        {
            itemDocument.uncheckout(true, VFSNode.CALLCONTEXT_UNDEFINED);
        }
        return false;
    }
    
    /**
     * Returns the stssync URL
     * 
     * @param type one of these: contacts, calendar, tasks
     * @param weburl URL into root of website without trailing slash,  e.g. http://example.com/alfresco/aos
     * @param listurl site relative path to the list with leading and trailing slash, e.g. /lists/contacts/
     * @param guid the guid of the list
     * @param siteName the display name of the website
     * @param listname the display name of the list
     * 
     * @return
     */
    protected String generateStsSyncUrl(String type, String weburl, String listurl, String guid, String siteName, String listName, String folderUrl, String folderId)
    {
        URLEncoder strictEncoder = new URLEncoder();
        StringBuffer link = new StringBuffer();
        link.append("stssync://sts/");
        link.append("?ver=1.1");
        link.append("&type=");
        link.append(strictEncoder.encode(type));
        link.append("&cmd=add-folder");
        link.append("&base-url=");
        link.append(strictEncoder.encode(weburl));
        link.append("&list-url=");
        link.append(strictEncoder.encode(listurl));
        link.append("&guid=");
        link.append(strictEncoder.encode(guid));
        link.append("&site-name=");
        link.append(strictEncoder.encode(siteName));
        link.append("&list-name=");
        link.append(strictEncoder.encode(listName));
        if( (folderUrl != null) && (folderId != null) )
        {
            link.append("&folder-url=");
            link.append(strictEncoder.encode(folderUrl));
            link.append("&folder-id=");
            link.append(strictEncoder.encode(folderId));
        }
        return link.toString();
    }

    protected String getServerUrl(HttpServletRequest request)
    {
        String protocol = request.isSecure() ? "https://" : "http://";
        int defaultPort = request.isSecure() ? 443 : 80;
        String portString = (request.getLocalPort() != defaultPort) ? ":" + Integer.toString(request.getLocalPort()) : "";
        return protocol + request.getServerName() + portString;
    }

    // Authentication

    @Override
    public UserData negotiateAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        return new AuthenticationServiceUserData(authenticationService);
    }

    @Override
    public void requestAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
    	// not required
    }

    @Override
    public void invalidateAuthentication(UserData userData, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
    	// not required
    }

}
