package org.alfresco.module.org_alfresco_module_cloud.webscripts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.module.org_alfresco_module_cloud.repo.content.transform.TransformationOptionsModule;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.RuntimeExecutableContentTransformerOptions;
import org.alfresco.repo.content.transform.magick.ImageTransformationOptions;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.repository.CropSourceOptions;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.PagedSourceOptions;
import org.alfresco.service.cmr.repository.TemporalSourceOptions;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.alfresco.service.namespace.QName;
import org.alfresco.test_category.SharedJVMTestsCategory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.experimental.categories.Category;
import org.springframework.core.io.ClassPathResource;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.TestWebScriptServer.PostRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * Tests for {@link TransformContent}
 * 
 * @author Abraham Ayala
 * @author Francisco J. Alvarez
 * 
 */
@Category(SharedJVMTestsCategory.class)
public class TransformContentTest extends BaseWebScriptTest
{
    // URL & files path
    private static final String TRANSFORM_URL = "/content/transform";
    private static final String TRANSFORM_CHECK_URL = "/content/transform/check";

    // Constants
    private static final String CONTENT_FIELD = "content";
    private static final String OPTIONSCLASS_FIELD = "optionsClass";
    private static final String OPTIONS_FIELD = "options";
    private static final String SOURCEMIMETYPE_FIELD = "sourceMimetype";
    private static final String TARGETMIMETYPE_FIELD = "targetMimetype";

    private static final String PATH_FILE = "remote-transform-server/files/samplefile.txt";

    private static final String MULTIPART_FORM_DATA_BOUNDARY = "multipart/form-data; boundary=";
    private static final String USER_ADMIN = "admin";
    private static final String CHARSET = "UTF-8";
    private static final String CRLF = "\r\n";
    private final static String AUTHENTICATIONCOMPONENT = "authenticationComponent";

    private AuthenticationComponent authenticationComponent;

    // Jackson Object Mapper
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    static
    {
        SimpleModule module = new SimpleModule("CustomSerializersModule", new Version(1, 0, 0, null));
        module.addSerializer(NodeRef.class, new NodeRefJsonSerializer());
        module.addSerializer(QName.class, new QNameJsonSerializer());
        jsonMapper.registerModule(module);
        jsonMapper.registerModule(new TransformationOptionsModule());
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        this.authenticationComponent = (AuthenticationComponent) getServer().getApplicationContext().getBean(
                AUTHENTICATIONCOMPONENT);

        this.authenticationComponent.setCurrentUser(USER_ADMIN);
    }

    /**
     * Test if a file is transformed to HTML with the option class field
     * "org.alfresco.service.cmr.repository.TransformationOptions"
     * 
     * @throws Exception
     */
    public void testTransformationOptions() throws Exception
    {
        // transformation options
        TransformationOptions to = new TransformationOptions();

        // request parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put(OPTIONSCLASS_FIELD, "org.alfresco.service.cmr.repository.TransformationOptions");
        params.put(OPTIONS_FIELD, getJson(to));
        params.put(SOURCEMIMETYPE_FIELD, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        params.put(TARGETMIMETYPE_FIELD, MimetypeMap.MIMETYPE_HTML);

        // request
        Response response = transformPostMethod(params, new ClassPathResource(PATH_FILE).getFile(), Status.STATUS_OK);

        // response validation
        assertTrue("The generated file can't be empty", response.getContentAsByteArray().length > 0);
    }

    public void testTransformationPagedSourceOptions() throws Exception
    {
        // transformation options
        TransformationOptions to = new TransformationOptions();
        
        PagedSourceOptions sourceOptions = new PagedSourceOptions();
        sourceOptions.setStartPageNumber(10);
        sourceOptions.setEndPageNumber(100);
        sourceOptions.setApplicableMimetypes(Arrays.asList(new String[] {MimetypeMap.MIMETYPE_TEXT_PLAIN, MimetypeMap.MIMETYPE_IMAGE_JPEG}));
        to.addSourceOptions(sourceOptions);

        // request parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put(OPTIONSCLASS_FIELD, "org.alfresco.service.cmr.repository.TransformationOptions");
        params.put(OPTIONS_FIELD, getJson(to));
        params.put(SOURCEMIMETYPE_FIELD, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        params.put(TARGETMIMETYPE_FIELD, MimetypeMap.MIMETYPE_HTML);

        // request
        Response response = transformPostMethod(params, new ClassPathResource(PATH_FILE).getFile(), Status.STATUS_OK);

        // response validation
        assertTrue("The generated file can't be empty", response.getContentAsByteArray().length > 0);
    }

    public void testTransformationCroppedSourceOptions() throws Exception
    {
        // transformation options
        TransformationOptions to = new TransformationOptions();
        
        CropSourceOptions sourceOptions = new CropSourceOptions();
        sourceOptions.setHeight(100);
        sourceOptions.setWidth(101);
        sourceOptions.setGravity("up");
        
        sourceOptions.setApplicableMimetypes(Arrays.asList(new String[] {MimetypeMap.MIMETYPE_TEXT_PLAIN, MimetypeMap.MIMETYPE_IMAGE_JPEG}));
        to.addSourceOptions(sourceOptions);

        // request parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put(OPTIONSCLASS_FIELD, "org.alfresco.service.cmr.repository.TransformationOptions");
        params.put(OPTIONS_FIELD, getJson(to));
        params.put(SOURCEMIMETYPE_FIELD, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        params.put(TARGETMIMETYPE_FIELD, MimetypeMap.MIMETYPE_HTML);

        // request
        Response response = transformPostMethod(params, new ClassPathResource(PATH_FILE).getFile(), Status.STATUS_OK);

        // response validation
        assertTrue("The generated file can't be empty", response.getContentAsByteArray().length > 0);
    }

    public void testTransformationTemporalSourceOptions() throws Exception
    {
        // transformation options
        TransformationOptions to = new TransformationOptions();
        
        TemporalSourceOptions sourceOptions = new TemporalSourceOptions();
        sourceOptions.setDuration("10:10:10");
        sourceOptions.setOffset("10:10:10");
        
        sourceOptions.setApplicableMimetypes(Arrays.asList(new String[] {MimetypeMap.MIMETYPE_TEXT_PLAIN, MimetypeMap.MIMETYPE_IMAGE_JPEG}));
        to.addSourceOptions(sourceOptions);

        // request parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put(OPTIONSCLASS_FIELD, "org.alfresco.service.cmr.repository.TransformationOptions");
        
        params.put(OPTIONS_FIELD, getJson(to));
        params.put(SOURCEMIMETYPE_FIELD, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        params.put(TARGETMIMETYPE_FIELD, MimetypeMap.MIMETYPE_HTML);

        // request
        Response response = transformPostMethod(params, new ClassPathResource(PATH_FILE).getFile(), Status.STATUS_OK);

        // response validation
        assertTrue("The generated file can't be empty", response.getContentAsByteArray().length > 0);
    }

    /**
     * Test if a file is transformed to an Image (PNG File) with the option class field
     * "org.alfresco.repo.content.transform.magick.ImageTransformationOptions"
     * 
     * @throws Exception
     */
// amiller: Disabled in fixing CLOUD-961, at the request of David Caruana 
//    public void testImageTransformationOptions() throws Exception
//    {
//        // image transformation options
//        ImageResizeOptions iro = new ImageResizeOptions();
//        iro.setWidth(100);
//        iro.setHeight(100);
//        iro.setMaintainAspectRatio(true);
//        iro.setResizeToThumbnail(true);
//
//        ImageTransformationOptions to = new ImageTransformationOptions();
//        to.setResizeOptions(iro);
//
//        // request parameters
//        Map<String, String> params = new HashMap<String, String>();
//        params.put(OPTIONSCLASS_FIELD, "org.alfresco.repo.content.transform.magick.ImageTransformationOptions");
//        params.put(OPTIONS_FIELD, getJson(to));
//        params.put(SOURCEMIMETYPE_FIELD, MimetypeMap.MIMETYPE_TEXT_PLAIN);
//        params.put(TARGETMIMETYPE_FIELD, MimetypeMap.MIMETYPE_IMAGE_PNG);
//
//        // request
//        Response response = transformPostMethod(params, new ClassPathResource(PATH_FILE).getFile(), Status.STATUS_OK);
//
//        // response validation
//        assertTrue("The generated file can't be empty", response.getContentAsByteArray().length > 0);
//    }

    /**
     * Test if a file is transformed to a HTML with the option class field
     * "org.alfresco.repo.content.transform.RuntimeExecutableContentTransformerOptions"
     * 
     * @throws Exception
     */
    public void testRuntimeExecutableTransformationOptions() throws Exception
    {
        // runtime transformation options
        RuntimeExecutableContentTransformerOptions to = new RuntimeExecutableContentTransformerOptions();

        // request parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put(OPTIONSCLASS_FIELD, "org.alfresco.repo.content.transform.RuntimeExecutableContentTransformerOptions");
        params.put(OPTIONS_FIELD, getJson(to));
        params.put(SOURCEMIMETYPE_FIELD, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        params.put(TARGETMIMETYPE_FIELD, MimetypeMap.MIMETYPE_HTML);

        // request
        Response response = transformPostMethod(params, new ClassPathResource(PATH_FILE).getFile(), Status.STATUS_OK);

        // response validation
        assertTrue("The generated file can't be empty", response.getContentAsByteArray().length > 0);
    }

    /**
     * Test if a file is transformed to a Flash file with the option class field
     * "org.alfresco.repo.content.transform.swf.SWFTransformationOptions"
     * 
     * @throws Exception
     */
 // amiller: Disabled in fixing CLOUD-961, at the request of David Caruana 
//    public void testSWFTransformationOptions() throws Exception
//    {
//        // swf transformation options
//        SWFTransformationOptions to = new SWFTransformationOptions();
//
//        // request parameters
//        Map<String, String> params = new HashMap<String, String>();
//        params.put(OPTIONSCLASS_FIELD, "org.alfresco.repo.content.transform.swf.SWFTransformationOptions");
//        params.put(OPTIONS_FIELD, getJson(to));
//        params.put(SOURCEMIMETYPE_FIELD, MimetypeMap.MIMETYPE_TEXT_PLAIN);
//        params.put(TARGETMIMETYPE_FIELD, MimetypeMap.MIMETYPE_FLASH);
//
//        // request
//        Response response = transformPostMethod(params, new ClassPathResource(PATH_FILE).getFile(), Status.STATUS_OK);
//
//        // response validation
//        assertTrue("The generated file can't be empty", response.getContentAsByteArray().length > 0);
//    }

    /**
     * Test if the check feature of the webscript returns success when a mimetype can be converted to another
     * 
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     * @throws JSONException
     */
    public void testCheckTransformation() throws JsonGenerationException, JsonMappingException, IOException,
            JSONException
    {
        // image transformation options
        ImageTransformationOptions to = new ImageTransformationOptions();

        // request parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put(OPTIONSCLASS_FIELD, "org.alfresco.repo.content.transform.magick.ImageTransformationOptions");
        params.put(OPTIONS_FIELD, getJson(to));
        params.put(SOURCEMIMETYPE_FIELD, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        params.put(TARGETMIMETYPE_FIELD, MimetypeMap.MIMETYPE_HTML);

        // request
        Response response = transformPostMethodNoFile(params, Status.STATUS_OK);

        // response parse
        JSONObject json = new JSONObject(response.getContentAsString());

        // response validation
        assertTrue("Expected success true", json.getBoolean("success"));
    }

    /**
     * Test if the check feature of the webscript returns no sucess when a mimetype can't be converted to another
     * 
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     * @throws JSONException
     */
    public void testCheckTransformationInvalid() throws JsonGenerationException, JsonMappingException, IOException,
            JSONException
    {
        // image transformation options
        ImageTransformationOptions to = new ImageTransformationOptions();

        // request parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put(OPTIONSCLASS_FIELD, "org.alfresco.repo.content.transform.magick.ImageTransformationOptions");
        params.put(OPTIONS_FIELD, getJson(to));
        params.put(SOURCEMIMETYPE_FIELD, MimetypeMap.MIMETYPE_IMAGE_JPEG);
        params.put(TARGETMIMETYPE_FIELD, MimetypeMap.MIMETYPE_HTML);

        // request
        Response response = transformPostMethodNoFile(params, Status.STATUS_OK);

        // response parse
        JSONObject json = new JSONObject(response.getContentAsString());

        // response validation
        assertFalse("Expected success false", json.getBoolean("success"));
    }

    /**
     * Execute a POST webscript with a file attached
     * 
     * @param params Transformation options and options classes
     * @param file File to transform
     * @param expectedStatus Expected Status for the response of the webscript
     * @return WebScript Response
     * @throws IOException
     */
    private Response transformPostMethod(Map<String, String> params, File file, int expectedStatus) throws IOException
    {
        String boundary = Long.toHexString(System.currentTimeMillis());

        String multipartContentType = MULTIPART_FORM_DATA_BOUNDARY + boundary;

        PostRequest postReq = new PostRequest(TRANSFORM_URL, buildPostBodyFILE(file, boundary, params),
                multipartContentType);

        return sendRequest(postReq, expectedStatus);
    }

    /**
     * Execute a POST webscript
     * 
     * @param params Transformation options and options classes
     * @param expectedStatus Expected Status for the response of the webscript
     * @return WebScript Response
     * @throws IOException
     */
    private Response transformPostMethodNoFile(Map<String, String> params, int expectedStatus) throws IOException
    {
        String boundary = Long.toHexString(System.currentTimeMillis());

        String multipartContentType = MULTIPART_FORM_DATA_BOUNDARY + boundary;

        PostRequest postReq = new PostRequest(TRANSFORM_CHECK_URL, buildPostBody(boundary, params),
                multipartContentType);

        return sendRequest(postReq, expectedStatus);
    }

    /**
     * Build a POST body with a params map
     * 
     * @param jsonFile
     * @param file
     * @param boundary
     * @return
     * @throws IOException
     */
    private byte[] buildPostBody(String boundary, Map<String, String> params) throws IOException
    {
        StringBuilder sb = new StringBuilder();

        addPostParams(params, sb, boundary);

        // End of multipart/form-data.
        sb.append("--" + boundary + "--").append(CRLF);

        System.out.println(sb.toString());
        return sb.toString().getBytes(CHARSET);
    }

    /**
     * Build a POST body with a file and json file attached
     * 
     * @param jsonFile
     * @param file
     * @param boundary
     * @return
     * @throws IOException
     */
    private byte[] buildPostBodyFILE(File file, String boundary, Map<String, String> params) throws IOException
    {
        StringBuilder sb = new StringBuilder();

        addPostParams(params, sb, boundary);

        // Send binary file.
        sb.append("--" + boundary).append(CRLF);
        sb.append("Content-Disposition: form-data; name=\"" + CONTENT_FIELD + "\"; filename=\"" + file.getName() + "\"").append(
                CRLF);
        sb.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(CRLF);
        sb.append("Content-Transfer-Encoding: binary").append(CRLF);
        sb.append(CRLF);
        InputStream input = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            input = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            for (int length = 0; (length = input.read(buffer)) > 0;)
            {
                output.write(buffer, 0, length);
            }
            output.flush(); // Important! Output cannot be closed. Close of
                            // writer will close output as well.
        }
        finally
        {
            if (input != null)
                try
                {
                    input.close();
                }
                catch (IOException logOrIgnore)
                {
                }
        }
        sb.append(output.toString(CHARSET));
        sb.append(CRLF); // CRLF is important! It indicates end of binary
                         // boundary.

        // End of multipart/form-data.
        sb.append("--" + boundary + "--").append(CRLF);

        return sb.toString().getBytes(CHARSET);
    }

    /**
     * Add post header params to a StringBuilder object
     * 
     * @param params
     * @param sb
     * @param boundary
     */
    private void addPostParams(Map<String, String> params, StringBuilder sb, String boundary)
    {
        for (Entry<String, String> param : params.entrySet())
        {
            sb.append("--" + boundary).append(CRLF);
            sb.append("Content-Disposition: form-data; name=\"" + param.getKey() + "\"").append(CRLF);
            sb.append("Content-Type: text/plain; charset=" + CHARSET).append(CRLF);
            sb.append("Content-Transfer-Encoding: 8bit").append(CRLF);
            sb.append(CRLF);
            sb.append(param.getValue()).append(CRLF);
        }
    }

    /**
     * Returns a string JSON representation of the object
     * 
     * @param o
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    private String getJson(Object o) throws JsonGenerationException, JsonMappingException, IOException
    {
        return jsonMapper.writeValueAsString(o);
    }

    /**
     * Serializer for NodeRef objects
     * 
     * @author Ezequiel Foncubierta
     * 
     */
    private static class NodeRefJsonSerializer extends JsonSerializer<NodeRef>
    {
        @Override
        public void serialize(NodeRef nodeRef, JsonGenerator jg, SerializerProvider sp) throws IOException,
                JsonProcessingException
        {
            jg.writeString(nodeRef.toString());
        }
    }

    /**
     * Serializer for QName objects
     * 
     * @author Adei Mandaluniz
     * 
     */
    private static class QNameJsonSerializer extends JsonSerializer<QName>
    {
        @Override
        public void serialize(QName qname, JsonGenerator jg, SerializerProvider sp) throws IOException,
                JsonProcessingException
        {
            jg.writeString(qname.toString());
        }
    }
}
