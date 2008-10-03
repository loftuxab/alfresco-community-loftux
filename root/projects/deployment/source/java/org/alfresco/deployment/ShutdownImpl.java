package org.alfresco.deployment;

import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.impl.client.DeploymentReceiverServiceClient;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * Class to shutdown this instance of the deployment server.
 * @author mrogers
 *
 */
public class ShutdownImpl {
   
    private int registryPort = 44100;
    private String serviceName = "deployment";
    private String hostName = "localhost";
	
    public void init() {
	   String user = "admin";
	   String password = "admin";
	 		
	   // lookup service
	   DeploymentReceiverService service = getReceiver();
	   
	   try {
		   // Do the shutdown
		   service.shutDown(user, password);
	   
		   // how to dispose of the service ?
		   service = null;
	   
	   }  catch (Exception e) {
		   // Do nothing - the remote service should have terminated
		   service=null;
	   }
	   
   }
   
   private DeploymentReceiverService getReceiver()
   {
       try
       {
           RmiProxyFactoryBean factory = new RmiProxyFactoryBean();
           factory.setRefreshStubOnConnectFailure(true);
           factory.setServiceInterface(DeploymentReceiverTransport.class);
           factory.setServiceUrl("rmi://" + hostName + ":" + registryPort + "/" + serviceName);
           factory.afterPropertiesSet();
           DeploymentReceiverTransport transport = (DeploymentReceiverTransport)factory.getObject();
           DeploymentReceiverServiceClient service = new DeploymentReceiverServiceClient();
           service.setDeploymentReceiverTransport(transport);
           return service;
       }
       catch (Exception e)
       {
           throw new DeploymentException("Could not connect to " + hostName + " at " + registryPort, e);
       }
   }

    public void setRegistryPort(int servicePort) 
    {
	    this.registryPort = servicePort;
    }

    public int getRegistryPort() 
    {
	    return registryPort;
    }

    public void setServiceName(String serviceName) {
	    this.serviceName = serviceName;
    }

    public String getServiceName() {
	    return serviceName;
    }
    
    public void setHostName(String hostName) {
	    this.hostName = hostName;
    }

    public String getHostName() {
	    return hostName;
    }
}
