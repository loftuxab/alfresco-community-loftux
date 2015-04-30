package org.alfresco.module.org_alfresco_module_cloud.repo.content.transform;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.CropSourceOptions;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.PagedSourceOptions;
import org.alfresco.service.cmr.repository.TemporalSourceOptions;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseAlfrescoTestCase;
import org.alfresco.util.PropertyMap;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.io.ClassPathResource;

/**
 * Tests for {@link RemoteAlfrescoTransformer}
 * 
 * @author Ezequiel Foncubierta
 * @author Abraham Ayala
 * @author Francisco J. Alvarez
 * 
 */
public class RemoteAlfrescoTransformerTest extends BaseAlfrescoTestCase
{
    private RemoteAlfrescoTransformerWorker rat;
    private NodeRef node1;
    private NodeRef node2;

    /**
     * Check config values
     * 
     * @author falvarez
     * 
     */
    private enum TestCheckConfig
    {
        CHECK_OK, CHECK_KO
    };

    /**
     * test Transform values
     * 
     * @author falvarez
     * 
     */
    private enum TestTransformConfig
    {
        TRANSFORM_OK, TRANSFORM_KO
    }

    /**
     * Setup the test cases
     * 
     * @throws Exception when an unexpected behaviour occurs
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        rat = (RemoteAlfrescoTransformerWorker) ctx.getBean("transformer.worker.remote.alfresco");
        // Enable remote transformations.
        rat.setEnabled(true);
        
        // NODE 1: testNode1.txt
        PropertyMap props = new PropertyMap();
        props.put(ContentModel.PROP_NAME, "testNode1.txt");

        node1 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "testNode1.txt"), ContentModel.TYPE_CONTENT,
                props).getChildRef();

        ContentWriter writer = contentService.getWriter(node1, ContentModel.PROP_CONTENT, true);
        File file = new ClassPathResource("remote-transform/samplefile.txt").getFile();
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.putContent(file);

        // NODE 2: testNode1.pdf
        props.put(ContentModel.PROP_NAME, "testNode1.pdf");

        node2 = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "testNode1.pdf"), ContentModel.TYPE_CONTENT,
                props).getChildRef();
    }

    /**
     * Mock the response from the server according to some params
     * 
     * @param checkConfig Config for check is OK or KO
     * @param transformConfig Config for transformation is OK or KO
     * @throws IOException
     * @throws HttpException
     */
    private void setUpClient(final TestCheckConfig checkConfig, final TestTransformConfig transformConfig)
            throws IOException, HttpException
    {
        HttpClient httpClientMock = mock(HttpClient.class);

        when(httpClientMock.executeMethod(any(HttpMethod.class))).thenAnswer(new Answer<Integer>()
        {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable
            {
                final HttpMethodBase method = (HttpMethodBase) invocation.getArguments()[0];

                if (method != null)
                {
                    final String path = method.getURI().getPath();
                    final Field respStream = HttpMethodBase.class.getDeclaredField("responseStream");
                    respStream.setAccessible(true);

                    if (path.startsWith("/alfresco/service/content/transform/check"))
                    {
                        switch (checkConfig)
                        {
                        case CHECK_OK:
                            respStream.set(method,
                                    new FileInputStream(new ClassPathResource("remote-transform/check_ok.json").getFile()));
                            return HttpStatus.SC_OK;
                        case CHECK_KO:
                        default:
                            respStream.set(method,
                                    new FileInputStream(new ClassPathResource("remote-transform/check_ko.json").getFile()));
                            return HttpStatus.SC_OK;
                        }
                    }
                    else if (path.startsWith("/alfresco/service/content/transform"))
                    {
                        switch (transformConfig)
                        {
                        case TRANSFORM_OK:
                            respStream.set(method,
                                    new FileInputStream(new ClassPathResource("remote-transform/transform_ok.pdf").getFile()));
                            return HttpStatus.SC_OK;
                        case TRANSFORM_KO:
                        default:
                            return HttpStatus.SC_INTERNAL_SERVER_ERROR;
                        }
                    }
                }

                return HttpStatus.SC_NOT_FOUND;
            }
        });

        HttpState httpStateMock = mock(HttpState.class);
        when(httpClientMock.getState()).thenReturn(httpStateMock);

        HttpClientFactory httpClientFactory = mock(HttpClientFactory.class);
        when(httpClientFactory.getHttpClient()).thenReturn(httpClientMock);
        
        rat.setHttpClientFactory(httpClientFactory);
        rat.initHttpClient();
    }

    /**
     * Test if the behaviour of the check content transformer is OK when it receives a good response from the remote
     * webscript
     * 
     * @throws IOException
     */
    public void testValidationOK() throws IOException
    {
        setUpClient(TestCheckConfig.CHECK_OK, TestTransformConfig.TRANSFORM_OK);
        assertTrue("Content should be transformable",
                rat.isTransformable("application/pdf", "text/plain", new TransformationOptions()));
    }

    /**
     * Test if the behaviour of the check content transformer is KO when it receives a bad response from the remote
     * webscript
     * 
     * @throws IOException
     */
    public void testValidationKO() throws IOException
    {
        setUpClient(TestCheckConfig.CHECK_KO, TestTransformConfig.TRANSFORM_OK);
        assertFalse("Content shouldn't transformable",
                rat.isTransformable("application/pdf", "text/rubbish", new TransformationOptions()));
    }

    /**
     * Test if the behaviour of the content transformer is OK when it tries to perform a valid transformation
     * @throws Exception 
     */
    public void testValidTransformationOK() throws Exception
    {
        setUpClient(TestCheckConfig.CHECK_OK, TestTransformConfig.TRANSFORM_OK);

        // reader and writer for node1 and node2
        ContentReader reader = contentService.getReader(node1, ContentModel.PROP_CONTENT);
        ContentWriter writer = contentService.getWriter(node2, ContentModel.PROP_CONTENT, true);

        // transform node1 (txt -> pdf) into node2
        writer.setMimetype(MimetypeMap.MIMETYPE_PDF);
        rat.transform(reader, writer, new TransformationOptions());

        // check transformation
        File transOKFile = new ClassPathResource("/remote-transform/transform_ok.pdf").getFile();
        FileInputStream okInStream = new FileInputStream(transOKFile);
        ContentReader n2Reader = contentService.getReader(node2, ContentModel.PROP_CONTENT);
        InputStream n2InStream = n2Reader.getContentInputStream();
        assertTrue("Transformed content is different than expected", IOUtils.contentEquals(okInStream, n2InStream));
    }

    /**
     * Test if the behaviour of the content transformer is KO when it tries to perform an invalid transformation
     * @throws Exception 
     */
    public void testValidTransformationKO() throws Exception
    {
        setUpClient(TestCheckConfig.CHECK_OK, TestTransformConfig.TRANSFORM_KO);

        // reader and writer for node1 and node2
        ContentReader reader = contentService.getReader(node1, ContentModel.PROP_CONTENT);
        ContentWriter writer = contentService.getWriter(node2, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_PDF);

        try
        {
            rat.transform(reader, writer, new TransformationOptions());
            fail("Transformation shouldn't be allowed");
        }
        catch (AlfrescoRuntimeException e)
        {
            // Expected exception
        }

        assertTrue("Transformed content must be null",
                contentService.getReader(node2, ContentModel.PROP_CONTENT) == null);
    }
    
    // Serialisation tests.
    private final RemoteAlfrescoTransformerWorkerImpl worker = new RemoteAlfrescoTransformerWorkerImpl();
    
    public void testSerializePagedSourceOptions() throws JsonGenerationException, JsonMappingException, IOException {
        PagedSourceOptions sourceOptions = new PagedSourceOptions();
        sourceOptions.setStartPageNumber(2);
        sourceOptions.setEndPageNumber(2);
        String json = worker.getJson(sourceOptions);
        assertNotNull(json);
    }
    
    public void testSerializeCropSourceOptions() throws JsonGenerationException, JsonMappingException, IOException {
        CropSourceOptions sourceOptions = new CropSourceOptions();
        sourceOptions.setHeight(5);
        sourceOptions.setWidth(10);
        String json = worker.getJson(sourceOptions);
        assertNotNull(json);
    }
    
    public void testSerializeTemporalSourceOptions() throws JsonGenerationException, JsonMappingException, IOException {
        TemporalSourceOptions sourceOptions = new TemporalSourceOptions();
        sourceOptions.setDuration("12:23:45");
        sourceOptions.setOffset("01:23:34");;
        String json = worker.getJson(sourceOptions);
        assertNotNull(json);
    }
    
    public void testSerializeTransformationOptionsWithSourceOptionsMap() throws Exception
    {
        TransformationOptions options = new TransformationOptions();
        // TODO more setting?
        
        PagedSourceOptions pagedSourceOptions = new PagedSourceOptions();
        pagedSourceOptions.setStartPageNumber(2);
        pagedSourceOptions.setEndPageNumber(2);
        
        CropSourceOptions cropSourceOptions = new CropSourceOptions();
        cropSourceOptions.setHeight(5);
        cropSourceOptions.setWidth(10);
        
        TemporalSourceOptions temporalSourceOptions = new TemporalSourceOptions();
        temporalSourceOptions.setDuration("12:23:45");
        temporalSourceOptions.setOffset("01:23:34");
        
        options.addSourceOptions(cropSourceOptions);
        options.addSourceOptions(pagedSourceOptions);
        options.addSourceOptions(temporalSourceOptions);
        
        String json = worker.getJson(options);
        System.out.println(json);
    }
}
