package org.alfresco.module.vti.web.ws;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.vti.handler.VersionsServiceHandler;
import org.alfresco.module.vti.metadata.model.DocumentVersionBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

/**
 * Class for handling RestoreVersion method from versions web service
 *
 * @author PavelYur
 */
public class RestoreVersionEndpoint extends AbstractVersionEndpoint
{
    private static Log logger = LogFactory.getLog(RestoreVersionEndpoint.class);

    public RestoreVersionEndpoint(VersionsServiceHandler handler)
    {
        super(handler);
    }
    
   @Override
   protected List<DocumentVersionBean> executeVersionAction(
         VtiSoapRequest soapRequest, String dws, String fileName, Element fileVersion) throws Exception 
   {
        if (logger.isDebugEnabled())
            logger.debug("Restoring version " + fileVersion.getText() + " for file '" + dws + "/" + fileName + "'" );
        List<DocumentVersionBean> notSortedVersions = handler.restoreVersion(dws + "/" + fileName, fileVersion.getText());
 
        // Sort as required
        List<DocumentVersionBean> versions = new ArrayList<DocumentVersionBean>();
        
        versions.add(notSortedVersions.get(0));
        for (int i = notSortedVersions.size() - 1; i > 0; --i) {
            versions.add(notSortedVersions.get(i));
        }

        // All done
        return versions;
    }
}
