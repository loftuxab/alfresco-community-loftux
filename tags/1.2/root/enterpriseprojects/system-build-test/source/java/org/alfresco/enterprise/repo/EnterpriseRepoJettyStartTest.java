/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.enterprise.repo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.jetty.http.security.Password;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.ClientCertAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Start Alfresco Repository (running on embedded Jetty)
 * 
 * @author Stefan Kopf
 */
public class EnterpriseRepoJettyStartTest extends TestCase
{
    public static final int JETTY_STOP_PORT = 8079;

    public static final String JETTY_LOCAL_IP = "localhost";

    private static Server server = null;

    public static void startJetty() throws Exception
    {
        try
        {
            System.out.println("[" + new Date() + "] startJetty: starting embedded Jetty server ...");
            System.out.println("Current working directory: "+(new java.io.File( "." ).getCanonicalPath()));
            System.out.println("Classpath:");
            for(String pathElement : System.getProperty("java.class.path").split(":"))
            {
                System.out.println("  "+pathElement);
            }

            server = new Server();

            //----- Jetty 7 connector setup -----------------------------------
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setPort(8080);
            connector.setHost(JETTY_LOCAL_IP);
            connector.setAcceptors(100);
            connector.setConfidentialPort(8443);
            connector.setMaxIdleTime(240000);
            SslSelectChannelConnector ssl_connector = new SslSelectChannelConnector();
            ssl_connector.setPort(8443);
            ssl_connector.setHost(JETTY_LOCAL_IP);
            ssl_connector.setKeystoreType("JCEKS");
            ssl_connector.setKeystore("keystore/ssl.keystore");
            ssl_connector.setKeyPassword("kT9X6oe68t");
            ssl_connector.setTruststoreType("JCEKS");
            ssl_connector.setTruststore("keystore/ssl.truststore");
            ssl_connector.setTrustPassword("kT9X6oe68t");
            ssl_connector.setAllowRenegotiate(true);
            ssl_connector.setNeedClientAuth(true);
            ssl_connector.setProtocol("https");
            ssl_connector.setProtocol("TLS");
            server.setConnectors(new Connector[] { connector, ssl_connector});

            //----- Jetty 9 connector setup -----------------------------------
            /*
            ServerConnector httpConnector = new ServerConnector(server);
            httpConnector.setPort(8080);
            server.addConnector(httpConnector);
            */

            HandlerList handlerList = new HandlerList();

            //----- Alfresco --------------------------------------------------

            // note: .../web-client/build/dist must be on classpath (and "alfresco.war" pre-built)
            String jspWebDescriptorPath = getResourceURI("jetty-jsp-web-descriptor.xml").toString();

            String alfrescoWarPath = getResourceURI("alfresco.war").toString();

            System.out.println("[" + new Date() + "] startJetty: jspWebDescriptorPath = " + jspWebDescriptorPath);
            System.out.println("[" + new Date() + "] startJetty: alfrescoWarPath = " + alfrescoWarPath);

            WebAppContext webAppContext = new WebAppContext();
            webAppContext.setContextPath("/alfresco");
            webAppContext.addOverrideDescriptor(jspWebDescriptorPath);

            SecurityHandler sh = webAppContext.getSecurityHandler();
            sh.setRealmName("Repository");
            sh.setAuthMethod("CLIENT-CERT");
            ClientCertAuthenticator authenticator = new ClientCertAuthenticator();
            sh.setAuthenticator(authenticator);

            HashLoginService loginService = new HashLoginService();
            loginService.setName("Repository");
            loginService
                    .putUser(
                            "CN=Alfresco Repository Client, OU=Unknown, O=Alfresco Software Ltd., L=Maidenhead, ST=UK, C=GB",
                            new Password(
                                    "2ieQnz2ZOA69gNmTJYBeYrZcqyJnY46maii7LsI9gjr53KJ8/+TZSxCyLXD7mwG28aEU5VErcA/KS4wCHHA+SARkNLRcnRlaZkd0QGWlFGnYFUNZnlU1fpJQ4lw2JDlsjU3LBYestw90UvAJwAQ5DkNjGaR2egKhOJ2fP93bfgU="),
                            new String[] { "repoclient" });
            sh.setLoginService(loginService);

            webAppContext.setWar(alfrescoWarPath);

            handlerList.addHandler(webAppContext);

            //----- SOLR ------------------------------------------------------

            java.net.URI solrWarUri = getResourceURI("apache-solr-1.4.1.war");
            String solrWarPath = solrWarUri.toString();
            String solrHomePath = uriToFile(solrWarUri).getParent();
            System.setProperty("solr.solr.home", solrHomePath);

            System.out.println("[" + new Date() + "] startJetty: solrWarPath = " + solrWarPath);
            System.out.println("[" + new Date() + "] startJetty: solrHomePath = " + solrHomePath);

            WebAppContext solrWebAppContext = new WebAppContext();
            solrWebAppContext.setContextPath("/solr");
            solrWebAppContext.setParentLoaderPriority(false);

            SecurityHandler solr_sh = solrWebAppContext.getSecurityHandler();
            solr_sh.setRealmName("Solr");
            solr_sh.setAuthMethod("CLIENT-CERT");
            ClientCertAuthenticator solr_authenticator = new ClientCertAuthenticator();
            solr_sh.setAuthenticator(solr_authenticator);

            HashLoginService solr_loginService = new HashLoginService();
            solr_loginService.setName("Solr");
            solr_loginService
                    .putUser(
                            "CN=Alfresco Repository, OU=Unknown, O=Alfresco Software Ltd., L=Maidenhead, ST=UK, C=GB",
                            new Password("YA3T/2YsCYIXouBgW6bKgNhmbOpQYNO9oLIgELnqy1J7hn5a4zT+hpPPDGAp+Sy508EYBt8EqQfSs1BsuGHhghTF2yA0eiNVJQErmrN3XTHKQof/vS4cgTZlne4WcVS8pg8+U6Hp2jLRuR5mEOCNQrnzthPX0v9REAa693iiyDM="),
                            new String[] { "repository" });
            solr_sh.setLoginService(solr_loginService);

            solrWebAppContext.setWar(solrWarPath);

            handlerList.addHandler(solrWebAppContext);

            //----- _vti_bin --------------------------------------------------

            String vtibinWarPath = getResourceURI("_vti_bin.war").toString();
            System.out.println("[" + new Date() + "] startJetty: vtibinWarPath = " + vtibinWarPath);

            WebAppContext vtibinWebAppContext = new WebAppContext();
            vtibinWebAppContext.setContextPath("/_vti_bin");
            vtibinWebAppContext.addOverrideDescriptor(jspWebDescriptorPath);
            vtibinWebAppContext.setWar(vtibinWarPath);
            handlerList.addHandler(vtibinWebAppContext);

            //----- ROOT ------------------------------------------------------

            String rootWarPath = getResourceURI("ROOT.war").toString();
            System.out.println("[" + new Date() + "] startJetty: rootWarPath = " + rootWarPath);

            WebAppContext rootWebAppContext = new WebAppContext();
            rootWebAppContext.setContextPath("/");
            rootWebAppContext.addOverrideDescriptor(jspWebDescriptorPath);
            rootWebAppContext.setWar(rootWarPath);
            handlerList.addHandler(rootWebAppContext);

            
            server.setHandler(handlerList);

            // for clean shutdown, add monitor thread

            // from: http://ptrthomas.wordpress.com/2009/01/24/how-to-start-and-stop-jetty-revisited/
            // adapted from: http://jetty.codehaus.org/jetty/jetty-6/xref/org/mortbay/start/Monitor.html
            Thread monitor = new EnterpriseRepoJettyMonitorThread();
            monitor.start();

            server.start();

            System.out.println("[" + new Date() + "] startJetty: ... embedded Jetty server started !");
        }
        catch (Throwable e)
        {
            System.out.println("[" + new Date() + "] startJetty: ... failed to start embedded Jetty server: " + e);
            e.printStackTrace(System.out);
            throw e;
        }
    }

    private static class EnterpriseRepoJettyMonitorThread extends Thread
    {
        private ServerSocket socket;

        public EnterpriseRepoJettyMonitorThread()
        {
            setDaemon(true);
            setName("StopMonitor");
            try
            {
                socket = new ServerSocket(JETTY_STOP_PORT, 1, InetAddress.getByName(JETTY_LOCAL_IP));
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run()
        {
            Socket accept;
            try
            {
                accept = socket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                reader.readLine();
                server.stop();
                accept.close();
                socket.close();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public void testStartJetty() throws Exception
    {
        EnterpriseRepoJettyStartTest.startJetty();
    }
    
    public static java.net.URI getResourceURI(String path) throws URISyntaxException, FileNotFoundException
    {
    	return urlToURI(getResourceURL(path));
    }
    
    public static URL getResourceURL(String path) throws URISyntaxException, FileNotFoundException
    {
    	URL url = getDefaultClassLoader().getResource(path);
    	if(url == null)
    	{
    		throw new FileNotFoundException("File '" + path + "' cannot be found on the Classpath.");
    	}
    	return url;
    }
    
    public static java.net.URI urlToURI(URL url) throws URISyntaxException
    {
    	return new java.net.URI(url.toString().replace(" ", "%20"));
    }
    
    public static File uriToFile(java.net.URI uri) throws FileNotFoundException
    {
    	if(!"file".equals(uri.getScheme()))
    	{
    		throw new FileNotFoundException(uri.toString() + " is not a file.");
    	}
    	return new File(uri.getSchemeSpecificPart());
    }
    
	public static ClassLoader getDefaultClassLoader()
	{
		ClassLoader cl = null;
		try
		{
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex)
		{
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		if (cl == null)
		{
			// No thread context class loader -> use class loader of this class.
			cl = EnterpriseRepoJettyStartTest.class.getClassLoader();
		}
		return cl;
	}
    
}
