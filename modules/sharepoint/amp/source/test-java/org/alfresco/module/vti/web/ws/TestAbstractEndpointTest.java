package org.alfresco.module.vti.web.ws;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for the {@link AbstractListEndpoint} class.
 * 
 * @author pavel.yurkevich
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAbstractEndpointTest
{
    private AbstractEndpoint endpoint;
    private @Mock VtiSoapRequest alfrescoContextSoapRequest;
    private @Mock VtiSoapRequest rootContextSoapRequest;
    private Element fileNameElement;

    @Before
    public void setUp()
    {
        endpoint = new AbstractEndpoint()
        {
            @Override
            public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
            {
            }

            @Override
            public String getHost(VtiSoapRequest request)
            {
                return "http://localhost:7070";
            }

            @Override
            public String getName()
            {
                return "GetVersions";
            }
        };

        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement(endpoint.getName());
        fileNameElement = rootElement.addElement("fileName");

        when(alfrescoContextSoapRequest.getAlfrescoContextName()).thenReturn("/alfresco");
        when(alfrescoContextSoapRequest.getRequestURI()).thenReturn("/alfresco/test/_vti_bin/versions.asmx");
        when(alfrescoContextSoapRequest.getDocument()).thenReturn(document);

        when(rootContextSoapRequest.getAlfrescoContextName()).thenReturn("/");
        when(rootContextSoapRequest.getRequestURI()).thenReturn("/test/_vti_bin/versions.asmx");
        when(rootContextSoapRequest.getDocument()).thenReturn(document);
    }

    @Test
    public void testMNT_10251() throws Throwable
    {
        // test that AbstractEndpoint#getDwsFromUri should not return site name with leading slash
        String siteName = AbstractEndpoint.getDwsFromUri(alfrescoContextSoapRequest);
        assertEquals("test", siteName);

        siteName = AbstractEndpoint.getDwsFromUri(rootContextSoapRequest);
        assertEquals("test", siteName);

        XPath fileNameXPath = new Dom4jXPath("/" + endpoint.getName() + "/fileName");

        // test that AbstractEndpoint#getFileName correctly parses absolute ant site-relative document names
        fileNameElement.setText("http://localhost:7070/alfresco/test/documentLibrary/test.docx");
        String fileName = endpoint.getFileName(alfrescoContextSoapRequest, fileNameXPath);
        assertEquals("documentLibrary/test.docx", fileName);

        fileNameElement.setText("http://localhost:7070/test/documentLibrary/test.docx");
        fileName = endpoint.getFileName(rootContextSoapRequest, fileNameXPath);
        assertEquals("documentLibrary/test.docx", fileName);

        fileNameElement.setText("documentLibrary/test.docx");
        fileName = endpoint.getFileName(alfrescoContextSoapRequest, fileNameXPath);
        assertEquals("documentLibrary/test.docx", fileName);

        fileNameElement.setText("documentLibrary/test.docx");
        fileName = endpoint.getFileName(rootContextSoapRequest, fileNameXPath);
        assertEquals("documentLibrary/test.docx", fileName);
    }
}
