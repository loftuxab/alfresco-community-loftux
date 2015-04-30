/**
 * 
 */
package org.alfresco.module.org_alfresco_module_cloud.repo.content.transform;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.content.transform.ContentTransformerHelper;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Adei Mandaluniz
 * @author Alex Miller
 */
public class RemoteAlfrescoTransformerWorkerImpl extends ContentTransformerHelper implements InitializingBean, RemoteAlfrescoTransformerWorker
{
    private static class CheckCacheKey implements Serializable 
    {
        private static final long serialVersionUID = 1L;
        
        private String sourceMimetype;
        private String targetMimetype;
        private TransformationOptions options;
        
        public CheckCacheKey(String sourceMimetype, String targetMimetype, TransformationOptions options)
        {
            this.sourceMimetype = sourceMimetype;
            this.targetMimetype = targetMimetype;
            this.options = options;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.options == null) ? 0 : this.options.hashCode());
            result = prime * result + ((this.sourceMimetype == null) ? 0 : this.sourceMimetype.hashCode());
            result = prime * result + ((this.targetMimetype == null) ? 0 : this.targetMimetype.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) 
            { 
                return true; 
            }
            if (obj == null) 
            { 
                return false;
            }
            if (getClass() != obj.getClass()) 
            { 
                return false;
            }
            CheckCacheKey other = (CheckCacheKey) obj;
            if (this.options == null)
            {
                if (other.options != null) 
                { 
                    return false; 
                }
            }
            else if (!this.options.equals(other.options)) 
            { 
                return false; 
            }
            if (this.sourceMimetype == null)
            {
                if (other.sourceMimetype != null) 
                { 
                    return false; 
                }
            }
            else if (!this.sourceMimetype.equals(other.sourceMimetype)) 
            { 
                return false; 
            }
            if (this.targetMimetype == null)
            {
                if (other.targetMimetype != null) 
                { 
                    return false; 
                }
            }
            else if (!this.targetMimetype.equals(other.targetMimetype)) 
            { 
                return false; 
            }
            return true;
        }
    }
    
	// Logger
    private static final Log logger = LogFactory.getLog(RemoteAlfrescoTransformerWorkerImpl.class);

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
    
    // HTTP Client
    private HttpClientFactory httpClientFactory;
    private HttpClient httpClient;
    
    // Constants
    private static final String CONTENT_FIELD = "content";
    private static final String OPTIONSCLASS_FIELD = "optionsClass";
    private static final String OPTIONS_FIELD = "options";
    private static final String SOURCEMIMETYPE_FIELD = "sourceMimetype";
    private static final String TARGETMIMETYPE_FIELD = "targetMimetype";
    
    // Repository services
    private MimetypeService mimetypeService;
    
    private SimpleCache<CheckCacheKey, Boolean> checkCache;
    
    private String username;
    private String password;
    
    private boolean enabled;
    
    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory("RemoteAlfrescoTransformerWorker", "checkCache", this.checkCache);
        PropertyCheck.mandatory("RemoteAlfrescoTransformerWorker", "httpClientFactory", this.httpClientFactory);
        PropertyCheck.mandatory("RemoteAlfrescoTransformerWorker", "username", this.username);
        PropertyCheck.mandatory("RemoteAlfrescoTransformerWorker", "password", this.password);
        initHttpClient();
    }
    
    /**
     * Use the HttpClientFactory to obtain an HttpClient instance.
     */
    @Override
    public void initHttpClient()
    {
        if (isEnabled())
        {
            try
            {    
                httpClient = httpClientFactory.getHttpClient();
                httpClient.getState().setCredentials(
                            new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                            new UsernamePasswordCredentials(username, password));
            }
            catch (Exception e)
            {
                setEnabled(false);
                if (logger.isErrorEnabled())
                {
                    logger.error("Can't set the HTTP Client: disabled remote transformations.", e);
                }
            }
        }
        
        if (logger.isInfoEnabled())
        {
            logger.info("Remote transformations client: " + (isEnabled() ? "enabled" : "disabled"));
        }
    }

    @Override
    public void transform(ContentReader reader, ContentWriter writer, TransformationOptions options) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Transforming " + reader.getContentUrl() + " to " + writer.getContentUrl());
        }
        
        writer.putContent(call(reader.getMimetype(), writer.getMimetype(), reader, options, false));
    }
    
    @Override
    public boolean isAvailable()
    {
        return (isEnabled() && httpClient != null);
    }
    
    @Override
    public String getVersionString()
    {
        return null;
    }
    
    /**
     * Ensure remote transformations are always delegated to the transformations server.
     */
    @Override
    public boolean isExplicitTransformation(String sourceMimetype, String targetMimetype, TransformationOptions options)
    {
        return true;
    }
    
    @Override
    public boolean isTransformable(String sourceMimetype, String targetMimetype, TransformationOptions options)
    {
        if (!isEnabled())
        {
            // Remote transformations are disabled.
            return false;
        }
        
        CheckCacheKey key = new CheckCacheKey(sourceMimetype, targetMimetype, options);
        if (checkCache.contains(key)) 
        {
            return checkCache.get(key);
        }
        else
        {
            // Check if the transformation is available in the remote server
            try
            {
                final JsonNode json = jsonMapper.readTree(call(sourceMimetype, targetMimetype, null, options, true));
                boolean result = json.has("success") && json.get("success").getBooleanValue();
                checkCache.put(key, result);
                return result;
            }
            catch (Exception e)
            {
                if (logger.isDebugEnabled())
                {
                    logger.error("Content is not transformable [" + e.getMessage() + "]", e);
                }
                return false;
            }
        }
    }
    
    /**
     * Perform a REST call to the transformation server
     * 
     * @param sourceMimetype
     * @param targetMimetype
     * @param reader
     * @param options
     * @param check
     * @return InputStream with the server response
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    private InputStream call(String sourceMimetype, String targetMimetype, ContentReader reader, TransformationOptions options, boolean check) throws JsonGenerationException, JsonMappingException,
            IOException
    {
        // Create the post method and the parts to be submitted
        PostMethod method = new PostMethod("/alfresco/service/content/transform" + (check ? "/check" : ""));
        
        try
        {
            List<Part> partList = new ArrayList<Part>();
            partList.add(createStringPart(OPTIONSCLASS_FIELD, options.getClass().getCanonicalName()));
            partList.add(createStringPart(OPTIONS_FIELD, getJson(options)));
            partList.add(createStringPart(SOURCEMIMETYPE_FIELD, sourceMimetype));
            partList.add(createStringPart(TARGETMIMETYPE_FIELD, targetMimetype));

            // set content only if check = false
            if (!check)
            {
                partList.add(createFilePart(CONTENT_FIELD, getFile(reader)));
            }
            
            MultipartRequestEntity requestEntity = new MultipartRequestEntity(partList.toArray(new Part[partList.size()]), method.getParams());
            method.setRequestEntity(requestEntity);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Sending multipart POST message to " + method.getURI().getURI() + " with parts " + partList);
            }

            int statusCode = httpClient.executeMethod(method);

            if (logger.isDebugEnabled())
            {
                logger.debug("Response status code: " + statusCode);
            }

            if (statusCode != HttpStatus.SC_OK)
            {
                throw new AlfrescoRuntimeException("Content couldn't be transformed remotely " + "[Server status " + statusCode + " - " + HttpStatus.getStatusText(statusCode) + "]");
            }

            return new ByteArrayInputStream(method.getResponseBody());
        }
        finally
        {
            method.releaseConnection();
        }
    }
    
    /**
     * Creates a string part
     * 
     * @param name
     * @param value
     * @return
     */
    private StringPart createStringPart(String name, String value)
    {
        StringPart stringPart = new StringPart(name, value);
        stringPart.setContentType(null);
        stringPart.setTransferEncoding(null);
        stringPart.setCharSet("UTF-8");
        return stringPart;
    }

    /**
     * Returns the file from the reader
     * 
     * @param reader
     * @return
     */
    private File getFile(ContentReader reader)
    {
        File contentFile;

        File tempDir = TempFileProvider.getTempDir();
        contentFile = TempFileProvider.createTempFile("transformation-" + (new Date()).getTime(), mimetypeService.getExtensionsByMimetype().get(reader.getMimetype()), tempDir);
        reader.getContent(contentFile);

        return contentFile;
    }

    /**
     * Creates a file part
     * 
     * @param name
     * @param value
     * @return
     * @throws FileNotFoundException
     */
    private FilePart createFilePart(String name, File value) throws FileNotFoundException
    {
        FilePart filePart = new FilePart(name, value);
        filePart.setTransferEncoding(null);
        filePart.setCharSet(null);
        return filePart;
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
    String getJson(Object o) throws JsonGenerationException, JsonMappingException, IOException
    {
        String json = jsonMapper.writeValueAsString(o);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Posting: " + json);
        }
        
        return json;
    }
    
	/**
	 * @return the username
	 */
	@Override
    public String getUsername()
	{
		return username;
	}

	/**
	 * @param username the username to set
	 */
	@Override
    public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * @return the password
	 */
	@Override
    public String getPassword()
	{
		return password;
	}

	/**
	 * @param password the password to set
	 */
	@Override
    public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * @return the mimetypeService
	 */
	@Override
	public MimetypeService getMimetypeService()
	{
		return mimetypeService;
	}

	/**
	 * @param mimetypeService the mimetypeService to set
	 */
	@Override
	public void setMimetypeService(MimetypeService mimetypeService)
	{
		this.mimetypeService = mimetypeService;
	}
	    
    /**
     * @return the httpClientFactory
     */
	@Override
    public HttpClientFactory getHttpClientFactory()
    {
        return httpClientFactory;
    }

    /**
     * @param httpClientFactory the httpClientFactory to set
     */
	@Override
    public void setHttpClientFactory(HttpClientFactory httpClientFactory)
    {
        this.httpClientFactory = httpClientFactory;
    }

    /**
     * Are remote transformations enabled? Returns true if the repository should
     * attempt to delegate transformations to a remote transformation server or
     * return false if remote transformations should not be used.
     * 
     * @return boolean - true if remote transformations are enabled.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Sets whether remote transformations should be enabled.
     * @see #isEnabled()
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
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
        public void serialize(NodeRef nodeRef, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException
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
        public void serialize(QName qname, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException
        {
            jg.writeString(qname.toString());
        }
    }

    public void setCheckCache(SimpleCache<CheckCacheKey, Boolean> checkCache)
    {
        this.checkCache = checkCache;
    }
}
