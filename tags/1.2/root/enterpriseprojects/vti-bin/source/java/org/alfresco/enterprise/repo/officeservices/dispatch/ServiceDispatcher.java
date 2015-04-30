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

import com.xaldon.officeservices.StandardDispatcherService;
import com.xaldon.officeservices.UserData;
import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.exceptions.VermeerException;
import com.xaldon.officeservices.protocol.VermeerRequest;
import com.xaldon.officeservices.protocol.VermeerResponse;
import com.xaldon.officeservices.protocol.VermeerReturnList;
import com.xaldon.officeservices.protocol.VermeerReturnStringValue;

public class ServiceDispatcher extends StandardDispatcherService
{

	private static final long serialVersionUID = 7637570058783423513L;

	protected List<String> servicePrefixes = new ArrayList<String>();
	
	private static Logger logger = Logger.getLogger("org.alfresco.enterprise.repo.officeservices.dispatch");

    public void init(ServletConfig servletConfig_p) throws ServletException
    {
        // initialize parent class
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
    }

    public List<?> getServicePrefixes(VermeerRequest vermeerRequest)
    {
        return servicePrefixes;
    }

    public void serverVersion(VermeerRequest vermeerRequest, VermeerResponse vermeerResponse) throws IOException, VermeerException, AuthenticationRequiredException
    {
        // compile and send response
        VermeerReturnList serverVersionDescription = new VermeerReturnList();
        serverVersionDescription.add("major ver",new VermeerReturnStringValue("14"));
        serverVersionDescription.add("minor ver",new VermeerReturnStringValue("0"));
        serverVersionDescription.add("phase ver",new VermeerReturnStringValue("0"));
        serverVersionDescription.add("ver incr",new VermeerReturnStringValue("4730"));
        vermeerResponse.addReturnItem("server version",serverVersionDescription);
        vermeerResponse.addReturnItem("source control",new VermeerReturnStringValue("1"));
        vermeerResponse.send();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.getWriter().print("<html><body>This is the xoservices xosdav ServiceDispatcher</body></html>");
    }


    protected class ServiceDispatcherUserData implements UserData
    {

        public String getUsername()
        {
            return "";
        }

    }

    protected UserData userData = new ServiceDispatcherUserData();

    public UserData negotiateAuthentication(HttpServletRequest arg0, HttpServletResponse arg1) throws IOException
    {
        return userData;
    }

    public void invalidateAuthentication(UserData arg0, HttpServletRequest arg1, HttpServletResponse arg2) throws IOException
    {
        // nothing to do here
    }

    public void requestAuthentication(HttpServletRequest arg0, HttpServletResponse arg1) throws IOException
    {
        // nothing to do here
    }

}