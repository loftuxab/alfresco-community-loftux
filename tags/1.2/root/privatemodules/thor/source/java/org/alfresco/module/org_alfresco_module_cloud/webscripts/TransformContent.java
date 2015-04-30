package org.alfresco.module.org_alfresco_module_cloud.webscripts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_cloud.repo.content.transform.TransformationOptionsModule;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.ContentTransformer;
import org.alfresco.repo.content.transform.ContentTransformerRegistry;
import org.alfresco.repo.web.scripts.content.StreamContent;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;

/**
 * Transform the content posted and stream the transformed content
 * 
 * @author Adei Mandaluniz
 * @author Ezequiel Foncubierta
 */
public class TransformContent extends StreamContent
{
    // Constants
    private static final String CONTENT_FIELD = "content";
    private static final String OPTIONSCLASS_FIELD = "optionsClass";
    private static final String OPTIONS_FIELD = "options";
    private static final String SOURCEMIMETYPE_FIELD = "sourceMimetype";
    private static final String TARGETMIMETYPE_FIELD = "targetMimetype";

    // Jackson Object Mapper
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    static
    {
        SimpleModule module = new SimpleModule("CustomDeserializersModule", new Version(1, 0, 0, null));
        module.addDeserializer(NodeRef.class, new NodeRefJsonDeserializer());
        module.addDeserializer(QName.class, new QNameJsonDeserializer());
        jsonMapper.registerModule(module);
        jsonMapper.registerModule(new TransformationOptionsModule());
    }

    // Logger
    private static final Log logger = LogFactory.getLog(TransformContent.class);

    // Parameters
    private List<String> classesStr;
    private Map<String, Class<TransformationOptions>> classes;

    // Repository services
    private ContentTransformerRegistry contentTransformerRegistry;
    private ContentService contentService;
    private MimetypeService mimetypeService;

    /**
     * Initialize the webscript
     */
    @SuppressWarnings("unchecked")
    public void init()
    {
        PropertyCheck.mandatory(this, "classes", classesStr);
        PropertyCheck.mandatory(this, "contentService", contentService);
        PropertyCheck.mandatory(this, "mimetypeService", mimetypeService);

        try
        {
            this.classes = new HashMap<String, Class<TransformationOptions>>(classesStr.size());
            for (String clazz : classesStr)
            {
                this.classes.put(clazz, (Class<TransformationOptions>) Class.forName(clazz));
            }
        }
        catch (ClassNotFoundException e)
        {
            throw new AlfrescoRuntimeException("Transformation class not found.", e);
        }
    }

    /**
     * @see org.springframework.extensions.webscripts.WebScript#execute(org.springframework.extensions.webscripts.WebScriptRequest,
     *      org.springframework.extensions.webscripts.WebScriptResponse)
     */
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Request: " + req.toString());
        }
        // Input parameters
        boolean check = req.getPathInfo().endsWith("/check"); // only check
        InputStream content = null; // stream to be transformed
        String optionsClass = null; // transformation class
        String optionsString = null; // transformation options
        String sourceMimetype = null; // stream mimetype
        String targetMimetype = null; // stream target mimetype

        // Retrieve the form's fields' values
        for (FormField field : ((FormData) req.parseContent()).getFields())
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Retrieving field " + field.getName() + " info");
            }

            if (field.getName().equals(CONTENT_FIELD))
            {
                content = field.getInputStream();
            }
            else if (field.getName().equals(OPTIONSCLASS_FIELD))
            {
                optionsClass = field.getValue();
            }
            else if (field.getName().equals(OPTIONS_FIELD))
            {
                optionsString = field.getValue();
            }
            else if (field.getName().equals(SOURCEMIMETYPE_FIELD))
            {
                sourceMimetype = field.getValue();
            }
            else if (field.getName().equals(TARGETMIMETYPE_FIELD))
            {
                targetMimetype = field.getValue();
            }
        }

        // input stream is a required parameter
        if (!check && content == null)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "'" + CONTENT_FIELD
                    + "' parameter must not be empty.");
        }

        // transformation class is a required parameter
        if (optionsClass == null || optionsClass.isEmpty())
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "'" + OPTIONSCLASS_FIELD
                    + "' parameter must not be empty.");
        }

        // transformation options is a required parameter
        if (optionsString == null || optionsClass.isEmpty())
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "'" + OPTIONS_FIELD
                    + "' parameter must not be empty.");
        }

        // source mimetype is a required parameter
        if (sourceMimetype == null || sourceMimetype.isEmpty())
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "'" + SOURCEMIMETYPE_FIELD
                    + "' parameter must not be empty.");
        }

        // target mimetype is a required parameter
        if (targetMimetype == null || targetMimetype.isEmpty())
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "'" + TARGETMIMETYPE_FIELD
                    + "' parameter must not be empty.");
        }

        // transformation class must exists
        if (!classes.containsKey(optionsClass))
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid tranformation parameters [Class: "
                    + optionsClass + " | Options: " + optionsString + "]");
        }

        // transform json options to a valid transformation options object
        TransformationOptions transformationOptions = jsonMapper.readValue(optionsString, classes.get(optionsClass));

        if (check)
        {
        	if (logger.isDebugEnabled())
        	{
        		logger.debug("Checking if transformation is available from '" + sourceMimetype + "' to '" + targetMimetype + "'");
        	}
            // load transformers
            List<ContentTransformer> transformers = contentTransformerRegistry.getActiveTransformers(sourceMimetype,
                    -1, targetMimetype, transformationOptions);

            if (logger.isDebugEnabled())
        	{
        		logger.debug("Transformation available: " + (transformers.size() > 0));
        	}
            // build the response
            JSONObject json = new JSONObject();
            try
            {
                json.put("success", transformers.size() > 0);
            }
            catch (JSONException e)
            {
                throw new WebScriptException(e.getMessage(), e);
            }

            final String jsonStr = json.toString();

            // write the response
            res.setContentType(MimetypeMap.MIMETYPE_JSON);
            res.setContentEncoding("UTF-8");
            res.setHeader("Content-Length", "" + jsonStr.length());
            res.getWriter().write(jsonStr);
        }
        else
        {
        	if(logger.isDebugEnabled())
        	{
        		logger.debug("Transforming content from '" + sourceMimetype + "' to '" + targetMimetype + "'");
        	}
        	
            // transform the input content
            ContentReader reader = getReader(content, sourceMimetype);
            ContentWriter writer = contentService.getTempWriter();
            writer.setMimetype(targetMimetype);
            contentService.transform(reader, writer, transformationOptions);

            if (logger.isDebugEnabled())
            {
                logger.debug("Transformation is done. Streaming it.");
            }

            File transformedContent = getFile(writer.getReader());
            streamContent(req, res, transformedContent);
            
            // Cleaning the temporary files
            // Note that the files created by the transformers are not cleaned at this point
            File originalFile = new File(reader.getContentUrl());
            
            if (logger.isDebugEnabled())
            {
            	logger.debug("Cleaning temporary files:" + transformedContent.getPath() + ", " + originalFile.getPath());
            }
            
            originalFile.delete();
            transformedContent.delete();
            
        }
    }

    /**
     * Returns the reader for the given content
     * 
     * @param content
     * @return
     */
    private ContentReader getReader(InputStream content, String mimetype)
    {
        ContentWriter writer = contentService.getTempWriter();
        writer.setMimetype(mimetype);
        writer.putContent(content);
        
        ContentReader reader = writer.getReader();
        reader.setMimetype(mimetype);

        return reader;
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
        contentFile = TempFileProvider.createTempFile("transformation-" + (new Date()).getTime(),
                mimetypeService.getExtensionsByMimetype().get(reader.getMimetype()), tempDir);
        reader.getContent(contentFile);

        return contentFile;
    }

    /**
     * @param classes
     */
    public void setClasses(List<String> classes)
    {
        this.classesStr = classes;
    }

    /**
     * @param contentTransformerRegistry
     */
    public void setContentTransformerRegistry(ContentTransformerRegistry contentTransformerRegistry)
    {
        this.contentTransformerRegistry = contentTransformerRegistry;
    }

    /**
     * @param contentService the contentService to set
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

    /**
     * @param mimetypeService the mimetypeService to set
     */
    public void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }

    /**
     * Deserializer for NodeRef objects
     * 
     * @author Ezequiel Foncubierta
     * 
     */
    private static class NodeRefJsonDeserializer extends JsonDeserializer<NodeRef>
    {
        @Override
        public NodeRef deserialize(JsonParser jp, DeserializationContext ctx) throws IOException,
                JsonProcessingException
        {
            return new NodeRef(jsonMapper.readValue(jp, String.class));
        }
    }

    /**
     * Deserializer for QName objects
     * 
     * @author Adei Mandaluniz
     * 
     */
    private static class QNameJsonDeserializer extends JsonDeserializer<QName>
    {
        @Override
        public QName deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException
        {
            return QName.createQName(jsonMapper.readValue(jp, String.class));
        }
    }
}
