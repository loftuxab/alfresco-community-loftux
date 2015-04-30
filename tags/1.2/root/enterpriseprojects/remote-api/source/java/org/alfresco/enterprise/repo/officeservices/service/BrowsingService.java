package org.alfresco.enterprise.repo.officeservices.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.enterprise.repo.officeservices.vfs.AlfrescoVirtualFileSystem;
import org.alfresco.enterprise.repo.officeservices.vfs.DocumentNode;
import org.alfresco.enterprise.repo.officeservices.vfs.FolderNode;
import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.apache.log4j.Logger;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xaldon.officeservices.AbstractBrowsingService;
import com.xaldon.officeservices.URLEncoder;
import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.protocol.HtmlEncoder;
import com.xaldon.officeservices.vfs.VFSDocumentNode;
import com.xaldon.officeservices.vfs.VFSNode;
import com.xaldon.officeservices.vfs.VirtualFileSystem;

import freemarker.core.Environment;
import freemarker.template.Template;

public class BrowsingService extends AbstractBrowsingService
{
    
    private static final long serialVersionUID = -4790039946289453732L;

    protected VirtualFileSystem vfs;
    
    protected FileDialogWebViewRegistry webViewRegistry;
    
    protected AuthenticationService authenticationService;
    
    protected Logger logger = Logger.getLogger(this.getClass());

    public static final String IMAGE_PREFIX = "images/filetypes/";
    public static final String IMAGE_POSTFIX = ".gif";
    public static final String DEFAULT_IMAGE = "images/filetypes/_default.gif";

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
        webViewRegistry = (FileDialogWebViewRegistry) wac.getBean("AosFileDialogWebViewRegistry");
        if(webViewRegistry == null)
        {
            throw new ServletException("Cannot find bean aosFileDialogWebViewRegistry in WebApplicationContext.");
        }
        authenticationService = (AuthenticationService) wac.getBean("AuthenticationService");
        if(authenticationService == null)
        {
            throw new ServletException("Cannot find bean AuthenticationService in WebApplicationContext.");
        }
    }
    
    // file dialog

    @Override
    public void doFileDialog(UserData userData, boolean saveAs, String location, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, AuthenticationRequiredException
    {
        // find configuration
        String normalizedLocation = AlfrescoVirtualFileSystem.normalizePath(location);
        FileDialogWebViewConfiguration webViewConfig = webViewRegistry.getConfiguration(normalizedLocation);
        if( (webViewConfig == null) || (webViewConfig.getFreeMarkerTemplateLocation() == null) )
        {
            doFileDialogNoTemplateError(response, null);
            return;
        }
        // try to get template as InputStream
        InputStream templateInputStream = this.getClass().getClassLoader().getResourceAsStream(webViewConfig.getFreeMarkerTemplateLocation());
        if(templateInputStream == null)
        {
            doFileDialogNoTemplateError(response, webViewConfig.getFreeMarkerTemplateLocation());
            return;
        }
        // get VFSNode
        VFSNode node = vfs.getNodeByPath(userData, normalizedLocation, VFSNode.CALLCONTEXT_BROWSINGSERVICE);
        if(node == null)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // list sub nodes
        String baseUrl = ((AlfrescoVirtualFileSystem)vfs).getSitePrefix(request) + normalizedLocation;
        @SuppressWarnings("unchecked")
        List<VFSNode> subNodes = (List<VFSNode>)node.getEnumerableContainees(userData, VFSNode.CALLCONTEXT_BROWSINGSERVICE);
        List<ItemData> itemDataList = new ArrayList<ItemData>();
        for(VFSNode n : subNodes)
        {
            itemDataList.add(new ItemData(n,baseUrl));
        }
        // Execute FreeMarker template
        Map<String, Object> freeMarkerMap = new HashMap<String, Object>();
        freeMarkerMap.put("resourcesUrl", ((AlfrescoVirtualFileSystem)vfs).getSitePrefix(request)+"/_aos_resources");
        freeMarkerMap.put("location", urlEncoder.encode(location));
        freeMarkerMap.put("items", itemDataList);
        freeMarkerMap.put("type", I18NUtil.getMessage("aos.webview.type"));
        freeMarkerMap.put("name", I18NUtil.getMessage("aos.webview.name"));
        freeMarkerMap.put("modified_by", I18NUtil.getMessage("aos.webview.modified_by"));
        freeMarkerMap.put("modified", I18NUtil.getMessage("aos.webview.modified"));
        freeMarkerMap.put("checked_out_by", I18NUtil.getMessage("aos.webview.checked_out_by"));
        freeMarkerMap.put("sort_by_type", I18NUtil.getMessage("aos.webview.sort_by_type"));
        freeMarkerMap.put("sort_by_name", I18NUtil.getMessage("aos.webview.sort_by_name"));
        freeMarkerMap.put("sort_by_modified_by", I18NUtil.getMessage("aos.webview.sort_by_name"));
        freeMarkerMap.put("sort_by_modified", I18NUtil.getMessage("aos.webview.sort_by_modified"));
        freeMarkerMap.put("sort_by_checked_out_by", I18NUtil.getMessage("aos.webview.sort_by_checked_out_by"));

        try
        {
            Template template = new Template("FileDialog", new InputStreamReader(templateInputStream), null);
            response.setContentType("text/html; charset=utf-8");
            response.flushBuffer();
            Environment env = template.createProcessingEnvironment(freeMarkerMap, response.getWriter());
            env.setOutputEncoding("utf-8");
            env.process();
            response.getWriter().flush();
        }
        catch (Exception e)
        {
            logger.error("Error processing FreeMarker template for file dialog",e);
            doFileDialogException(response, e);
        }
    }
    
    protected static URLEncoder urlEncoder;

    static
    {
        urlEncoder = new URLEncoder();
        urlEncoder.addSafeCharacter('-');
        urlEncoder.addSafeCharacter('_');
        urlEncoder.addSafeCharacter('.');
        urlEncoder.addSafeCharacter('*');
        urlEncoder.addSafeCharacter('/');
        urlEncoder.addSafeCharacter(':');
    }
    public class ItemData
    {
        public ItemData(VFSNode node, String baseUrl)
        {
            this.node = node;
            this.baseUrl = baseUrl;
        }
        private VFSNode node;
        private String baseUrl;
        public boolean isFolder()
        {
            return node instanceof FolderNode;
        }
        public String getUrl()
        {
            StringBuilder sb = new StringBuilder(baseUrl);
            if(!baseUrl.endsWith("/"))
            {
                sb.append("/");
            }
            sb.append(getName());
            return urlEncoder.encode(sb.toString());
        }
        public String getName()
        {
            return node.getName(VFSNode.CALLCONTEXT_BROWSINGSERVICE);
        }
        public String getModifiedBy()
        {
            if(node instanceof DocumentNode)
            {
                return ((DocumentNode)node).getModifier();
            }
            else
            {
                return "";
            }
        }
        public String getModifiedTime()
        {
            return node.getDateLastModified(VFSNode.CALLCONTEXT_BROWSINGSERVICE).toString();
        }
        public String getCheckedOutTo()
        {
            if(node instanceof DocumentNode)
            {
                String coOwner = ((DocumentNode)node).getCheckoutOwner();
                return coOwner == null ? "" : coOwner;
            }
            else
            {
                return "";
            }
        }
        public String getFileTypeImage()
        {
            String fileName = getName();
            String image = DEFAULT_IMAGE;
            int extIndex = fileName.lastIndexOf('.');
            if (extIndex != -1 && fileName.length() > extIndex + 1)
            {
                String ext = fileName.substring(extIndex + 1).toLowerCase();

                image = IMAGE_PREFIX + ext + IMAGE_POSTFIX;                
            }
            return image;
        }
    }
    
    
    public void doFileDialogNoTemplateError(HttpServletResponse response, String templateLocation) throws ServletException, IOException
    {
        PrintWriter w = response.getWriter();
        w.write("<html>\n");
        w.write("<head>\n");
        w.print("<style>");
        w.print("<!--");
        w.write(" html { margin: 0; padding: 0; }");
        w.write(" body { margin: 0; padding: 0; color: WindowText; background: Window url(images/blank.gif) fixed; color: #000000; overflow: hidden; border: 0; font: menu;font-size: 11px !important; background-color: ThreedFace; color: WindowText; }");
        w.print(" -->");
        w.print("</style>\n");
        w.write("</head>\n");
        w.write("<body servertype=\"OWS\" docLibsList=\"\">\n");
        w.write("<p>&nbsp;</p>\n");
        w.write("<p><b>Error:</b></p>\n");
        if(templateLocation == null)
        {
            w.write("<p>No FreeMarker Template defined for this path.</p>\n");
        }
        else
        {
            w.write("<p>Cannot find FreeMarker template. Classpath location:<br/>\n");
            w.write(WebDAVHelper.encodeHTML(templateLocation));
            w.write("</p>\n");
        }
        w.write("</body>\n");
        w.write("</html>\n");
    }
    
    public void doFileDialogException(HttpServletResponse response, Exception e) throws ServletException, IOException
    {
        PrintWriter w = response.getWriter();
        w.write("<html>\n");
        w.write("<head>\n");
        w.print("<style>");
        w.print("<!--");
        w.write(" html { margin: 0; padding: 0; }");
        w.write(" body { margin: 0; padding: 0; color: WindowText; background: Window url(images/blank.gif) fixed; color: #000000; overflow: scroll; border: 0; font: menu;font-size: 11px !important; background-color: ThreedFace; color: WindowText; }");
        w.print(" -->");
        w.print("</style>\n");
        w.write("</head>\n");
        w.write("<body servertype=\"OWS\" docLibsList=\"\">\n");
        w.write("<p>&nbsp;</p>\n");
        w.write("<p><b>"+WebDAVHelper.encodeHTML(e.getMessage())+"</b></p>\n");
        w.write("<p>\n");
        for(StackTraceElement ste : e.getStackTrace())
        {
            w.write("&nbsp;at ");
            w.write(WebDAVHelper.encodeHTML(ste.getClassName()));
            w.write(".");
            w.write(WebDAVHelper.encodeHTML(ste.getMethodName()));
            w.write("(");
            w.write(WebDAVHelper.encodeHTML(ste.getFileName()));
            w.write(":");
            w.write(ste.getLineNumber());
            w.write(")");
            w.write("<br/>\n");
        }
        w.write("</p>\n");
        w.write("</body>\n");
        w.write("</html>\n");
    }

    @Override
    public void doPropertiesDialog(UserData userData, String location, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, AuthenticationRequiredException
    {
        // we do not yet provide  a properties dialog
        response.sendError(500);
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
