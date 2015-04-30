package org.alfresco.enterprise.repo.officeservices.dispatch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.xaldon.officeservices.GetWebWebDescription;
import com.xaldon.officeservices.StandardWebsService;
import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.GetWebCollectionWebDescription;
import com.xaldon.officeservices.WebsGetContentTypeContentType;
import com.xaldon.officeservices.WebsGetContentTypesContentType;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.protocol.SimpleSoapParser;
import com.xaldon.officeservices.protocol.SoapParameter;

public class WebsService extends StandardWebsService
{

	private static final long serialVersionUID = -6294184506392454609L;

	protected List<String> servicePrefixes = new ArrayList<String>();
    
    private static Logger logger = Logger.getLogger("org.alfresco.enterprise.repo.officeservices.dispatch");

    public void init(ServletConfig servletConfig_p) throws ServletException
    {
        super.init(servletConfig_p);
        // read list of service names
        String spaceSeparatedEncodedServices = getServletContext().getInitParameter("org.alfresco.enterprise.repo.officeservices.dispatch.SERVICES");
        if(spaceSeparatedEncodedServices != null)
        {
            for(String encodedContext : spaceSeparatedEncodedServices.split(" "))
            {
                try
                {
                    servicePrefixes.add(URLDecoder.decode(encodedContext, "UTF-8"));
                }
                catch (UnsupportedEncodingException e)
                {
                    logger.error("Unsupported encoding",e);
                }
            }
        }
        // and finally add the server root as service prefix
        servicePrefixes.add("");
    }

	@Override
    public List<?> getServicePrefixes(SimpleSoapParser parser, HttpServletRequest request)
    {
        return servicePrefixes;
    }
    
    protected String getServerUrl(HttpServletRequest request)
    {
        String protocol = request.isSecure() ? "https://" : "http://";
        int defaultPort = request.isSecure() ? 443 : 80;
        String portString = (request.getLocalPort() != defaultPort) ? ":" + Integer.toString(request.getLocalPort()) : "";
        return protocol + request.getServerName() + portString;
    }

	@Override
    public List<?> getWebCollection(SimpleSoapParser parser, HttpServletRequest request)
    {
        String serverUrl = getServerUrl(request);
        List<Object> webCollection = new ArrayList<Object>(servicePrefixes.size()-1);
        for(int i = 0;  i <  servicePrefixes.size()-1;  i++)
        {
            String servicePrefix = (String)servicePrefixes.get(i);
            webCollection.add(new GetWebCollectionWebDescription(servicePrefix.substring(1),serverUrl+servicePrefix));
        }
        return webCollection;
    }

	@Override
	protected GetWebWebDescription getWebResult(UserData userData, String webUrl, SimpleSoapParser parser, HttpServletRequest request)
	{
        String serverUrl = getServerUrl(request);
        for(int i = 0;  i <  servicePrefixes.size()-1;  i++)
        {
            String servicePrefix = (String)servicePrefixes.get(i);
            String webUrlForService = serverUrl+servicePrefix;
            if(webUrl.equals(webUrlForService))
            {
                return new GetWebWebDescription(servicePrefix.substring(1),serverUrl+servicePrefix,"",GetWebWebDescription.LCID_ENUS,"");
            }
        }
		return null;
	}

    protected class WebsServiceUserData implements UserData
    {

        public String getUsername()
        {
            return "";
        }

    }

    @Override
    public WebsGetContentTypesContentType[] getContentTypes(SimpleSoapParser parser, HttpServletRequest request)
    {
        return null;
    }

    @Override
    protected WebsGetContentTypeContentType getContentType(UserData userData, String contentTypeId, HttpServletRequest request) throws AuthenticationRequiredException
    {
        return null;
    }

    protected UserData userData = new WebsServiceUserData();

	@Override
    public UserData negotiateAuthentication(HttpServletRequest arg0, HttpServletResponse arg1) throws IOException
    {
        return userData;
    }

	@Override
    public void invalidateAuthentication(UserData userData, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // nothing to do here
    }

	@Override
    public void requestAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // nothing to do here
    }

    @Override
    protected boolean updateContentTypeXmlDocument(UserData userData, String contentTypeId, SoapParameter newDocument, HttpServletRequest request) throws AuthenticationRequiredException
    {
        //  content type  XSN update is not implemented
        return false;
    }

}