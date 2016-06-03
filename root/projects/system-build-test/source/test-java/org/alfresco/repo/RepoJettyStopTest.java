
package org.alfresco.repo;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

import junit.framework.TestCase;

/**
 * Stop Alfresco Repository (running on embedded Jetty)
 * 
 * @author janv
 */
public class RepoJettyStopTest extends TestCase
{
    public void testStopJetty() throws Exception
    {
        try
        {
            System.out.println("["+new Date()+"] stopJetty: stopping embedded Jetty server ...");
            
            Socket s = new Socket(InetAddress.getByName(RepoJettyStartTest.JETTY_LOCAL_IP), RepoJettyStartTest.JETTY_STOP_PORT);
            OutputStream out = s.getOutputStream();
            
            out.write(("\r\n").getBytes());
            out.flush();
            s.close();
			
			//give jetty 10sec to stop
            Thread.sleep(10000);            
            System.out.println("["+new Date()+"] stopJetty: ... embedded Jetty server stopped !");
        }
        catch (Exception e)
        {
            System.out.println("["+new Date()+"] stopJetty: ... failed to stop embedded Jetty server: "+e);
            throw e;
        }
    }
}
