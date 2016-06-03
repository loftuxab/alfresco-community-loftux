package org.alfresco.module.vti.web.ws;

import java.util.List;

import org.alfresco.module.vti.handler.VersionsServiceHandler;
import org.alfresco.module.vti.metadata.model.DocumentVersionBean;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.version.VersionDoesNotExistException;
import org.dom4j.Element;

/**
 * Class for handling DeleteVersion method from versions web service
 *
 * @author PavelYur
 */
public class DeleteVersionEndpoint extends AbstractVersionEndpoint
{
    public DeleteVersionEndpoint(VersionsServiceHandler handler)
    {
       super(handler);
    }
    
    /**
     * Deletes specified version of the document
     *
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param dws String
     * @param fileName String
     * @param fileVersion Element
     * @return List<DocumentVersionBean>
     * @throws Exception
     */
    @Override
    protected List<DocumentVersionBean> executeVersionAction(
          VtiSoapRequest soapRequest, String dws, String fileName, Element fileVersion) throws Exception 
    {
       List<DocumentVersionBean> versions;
       try
       {
          versions = handler.deleteVersion(dws + "/" + fileName, fileVersion.getText());
       }
       catch(FileNotFoundException fnfe)
       {
          // The specification defines the exact code that must be
          //  returned in case of a file not being found
          long code = 0x80131600l;
          String message = "File not found: " + fnfe.getMessage();
          throw new VtiSoapException(message, code, fnfe);
       }
       catch(VersionDoesNotExistException vne)
       {
          // The specification defines the exact code that must be
          //  returned in case of the version not existing
          long code = 0x80131600l;
          String message = "No such version: " + vne.getMessage();
          throw new VtiSoapException(message, code, vne);
       }
       
       return versions;
    }
}
