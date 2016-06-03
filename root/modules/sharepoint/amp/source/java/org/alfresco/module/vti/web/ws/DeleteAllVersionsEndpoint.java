package org.alfresco.module.vti.web.ws;

import java.util.List;

import org.alfresco.module.vti.handler.VersionsServiceHandler;
import org.alfresco.module.vti.metadata.model.DocumentVersionBean;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.dom4j.Element;

/**
 * Class for handling DeleteAllVersions method from versions web service
 *
 * @author PavelYur
 */
public class DeleteAllVersionsEndpoint extends AbstractVersionEndpoint
{
    public DeleteAllVersionsEndpoint(VersionsServiceHandler handler)
    {
       super(handler);
    }

    @Override
    protected List<DocumentVersionBean> executeVersionAction(
         VtiSoapRequest soapRequest, String dws, String fileName, Element fileVersion) throws Exception 
    {
       // Delete all versions of given file
       List<DocumentVersionBean> versions;
       try {
          versions = handler.deleteAllVersions(dws + "/" + fileName);
       }
       catch(FileNotFoundException e)
       {
          // The specification defines the exact code that must be
          //  returned in case of a file not being found
          long code = 0x81070906l;
          String message = "File not found: " + e.getMessage();
          throw new VtiSoapException(message, code, e);
       }
       
       return versions;
    }
}
