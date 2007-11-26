package org.alfresco.web.scripts;

import java.io.IOException;



public class PresentationContainer extends AbstractRuntimeContainer
{

    /**
     * Execute script whilst authenticated
     * 
     * @param scriptReq  Web Script Request
     * @param scriptRes  Web Script Response
     * @throws IOException
     */
    public void executeScript(WebScriptRequest scriptReq, WebScriptResponse scriptRes, Authenticator auth)
        throws IOException
    {
        // TODO: Consider Web Tier Authentication
        WebScript script = scriptReq.getServiceMatch().getWebScript();
        script.execute(scriptReq, scriptRes);
    }

    
    public ServerModel getDescription()
    {
        return new PresentationServerModel();
    }

    
    private class PresentationServerModel implements ServerModel
    {
        // TODO: Implement when
        
        public String getContainerName()
        {
            return getName();
        }

        public String getEdition()
        {
            return UNKNOWN;
        }

        public int getSchema()
        {
            return -1;
        }

        public String getVersion()
        {
            return UNKNOWN;
        }

        public String getVersionBuild()
        {
            return UNKNOWN;
        }

        public String getVersionLabel()
        {
            return UNKNOWN;
        }

        public String getVersionMajor()
        {
            return UNKNOWN;
        }

        public String getVersionMinor()
        {
            return UNKNOWN;
        }

        public String getVersionRevision()
        {
            return UNKNOWN;
        }
        
        private final static String UNKNOWN = "<unknown>"; 
    }
    
}
