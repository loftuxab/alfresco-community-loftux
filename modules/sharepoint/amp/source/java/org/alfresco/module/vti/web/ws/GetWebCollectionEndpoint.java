package org.alfresco.module.vti.web.ws;

import java.util.HashMap;

import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;

/**
 * Class for handling GetWebCollection method from webs web service
 *
 * @author PavelYur
 */
public class GetWebCollectionEndpoint extends AbstractEndpoint
{

    private static Log logger = LogFactory.getLog(GetWebCollectionEndpoint.class);

    // handler that provides methods for operating with documents and folders
    private MethodHandler handler;    

    // xml namespace prefix
    private static String prefix = "webs";

    /**
     * constructor
     *
     * @param handler that provides methods for operating with documents and folders
     */
    public GetWebCollectionEndpoint(MethodHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Returns the titles and URLs of all sites directly beneath the current site
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("Soap Method with name " + getName() + " is started.");

        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        // get site name that is used to list subsites
        String siteName = getDwsFromUri(soapRequest);
        DocsMetaInfo docsMetaInfo = null;

        if (siteName.equals(""))
        {
            docsMetaInfo = handler.getListDocuments(siteName, false, false, "", "", false, false, true, true, false, false, false, false, new HashMap<String, Object>(0), false);
        }

        // creating soap response
        Element responseElement = soapResponse.getDocument().addElement("GetWebCollectionResponse", namespace);
        Element result = responseElement.addElement("GetWebCollectionResult");       
        Element webs = result.addElement("Webs");

        if (docsMetaInfo != null)
        {
            for (DocMetaInfo docMetaInfo : docsMetaInfo.getFolderMetaInfoList())
            {
                Element web = webs.addElement("Web");
                web.addAttribute("Title", docMetaInfo.getPath());
                web.addAttribute("Url", getHost(soapRequest) + soapRequest.getAlfrescoContextName() + "/" + docMetaInfo.getPath());
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Soap Method with name " + getName() + " is finished.");
        }
    }

}
