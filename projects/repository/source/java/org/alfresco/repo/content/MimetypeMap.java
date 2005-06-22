package org.alfresco.repo.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigLookupContext;
import org.alfresco.config.ConfigService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides a bidirectional mapping between well-known mimetypes and
 * the registered file extensions.
 * 
 * @author Derek Hulley
 */
public class MimetypeMap implements MimetypeService
{
    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    public static final String MIMETYPE_XML = "text/xml";
    public static final String MIMETYPE_HTML = "text/html";
    public static final String MIMETYPE_PDF = "application/pdf";
    public static final String MIMETYPE_WORD = "application/msword";
    public static final String MIMETYPE_EXCEL = "application/vnd.excel";
    public static final String MIMETYPE_BINARY = "application/octet-stream";
    
    private static final String CONFIG_AREA = "mimetype-map";
    private static final String CONFIG_CONDITION = "Mimetype Map";
    private static final String ELEMENT_MIMETYPES = "mimetypes";
    private static final String ELEMENT_MIMETYPE = "mimetype";
    private static final String ATTR_MIMETYPE = "mimetype";
    private static final String ATTR_DISPLAY = "display";
    private static final String ELEMENT_EXTENSION = "extension";
    private static final String ATTR_DEFAULT = "default";
    
    private static final Log logger = LogFactory.getLog(MimetypeMap.class);
    
    private ConfigService configService;
    
    private List<String> mimetypes;
    private Map<String, String> extensionsByMimetype;
    private Map<String, String> mimetypesByExtension;
    private Map<String, String> displaysByMimetype;
    private Map<String, String> displaysByExtension;
    
    /**
     * @param configService the config service to use to read mimetypes from
     */
    public MimetypeMap(ConfigService configService)
    {
        this.configService = configService;
    }    
    
    /**
     * Initialises the map using the configuration service provided
     */
    public void init()
    {
        this.mimetypes = new ArrayList<String>(40);
        this.extensionsByMimetype = new HashMap<String, String>(59);
        this.mimetypesByExtension = new HashMap<String, String>(59);
        this.displaysByMimetype = new HashMap<String, String>(59);
        this.displaysByExtension = new HashMap<String, String>(59);

        Config config = configService.getConfig(CONFIG_CONDITION, new ConfigLookupContext(CONFIG_AREA));
        ConfigElement mimetypesElement = config.getConfigElement(ELEMENT_MIMETYPES);
        List<ConfigElement> mimetypes = mimetypesElement.getChildren();
        int count = 0;
        for (ConfigElement mimetypeElement : mimetypes)
        {
            count++;
            // add to list of mimetypes
            String mimetype = mimetypeElement.getAttribute(ATTR_MIMETYPE);
            if (mimetype == null || mimetype.length() == 0)
            {
                logger.warn("Ignoring empty mimetype " + count);
                continue;
            }
            if (this.mimetypes.contains(mimetype))
            {
                throw new AlfrescoRuntimeException("Duplicate mimetype definition: " + mimetype);
            }
            this.mimetypes.add(mimetype);
            // add to map of mimetype displays
            String mimetypeDisplay = mimetypeElement.getAttribute(ATTR_DISPLAY);
            if (mimetypeDisplay != null && mimetypeDisplay.length() > 0)
            {
                this.displaysByMimetype.put(mimetype, mimetypeDisplay);
            }
            
            // get all the extensions
            boolean isFirst = true;
            List<ConfigElement> extensions = mimetypeElement.getChildren();
            for (ConfigElement extensionElement : extensions)
            {
                // add to map of mimetypes by extension
                String extension = extensionElement.getValue();
                if (extension == null || extension.length() == 0)
                {
                    logger.warn("Ignoring empty extension for mimetype: " + mimetype);
                    continue;
                }
                this.mimetypesByExtension.put(extension, mimetype);
                // add to map of extension displays
                String extensionDisplay = extensionElement.getAttribute(ATTR_DISPLAY);
                if (extensionDisplay != null && extensionDisplay.length() > 0)
                {
                    this.displaysByExtension.put(extension, extensionDisplay);
                }
                else if (mimetypeDisplay != null && mimetypeDisplay.length() > 0)
                {
                    // no display defined for the extension - use the mimetype's display
                    this.displaysByExtension.put(extension, mimetypeDisplay);
                }
                // add to map of extensions by mimetype if it is the default or first extension
                String isDefaultStr = extensionElement.getAttribute(ATTR_DEFAULT);
                boolean isDefault = Boolean.parseBoolean(isDefaultStr);
                if (isDefault || isFirst)
                {
                    this.extensionsByMimetype.put(mimetype, extension);
                }
                isFirst = false;
            }
            // check that there were extensions defined
            if (extensions.size() == 0)
            {
                logger.warn("No extensions defined for mimetype: " + mimetype);
            }
        }
        
        // make the collections read-only
        this.mimetypes = Collections.unmodifiableList(this.mimetypes);
        this.extensionsByMimetype = Collections.unmodifiableMap(this.extensionsByMimetype);
        this.mimetypesByExtension = Collections.unmodifiableMap(this.mimetypesByExtension);
        this.displaysByMimetype = Collections.unmodifiableMap(this.displaysByMimetype);
        this.displaysByExtension = Collections.unmodifiableMap(this.displaysByExtension);
    }
    
    /**
     * @param mimetype a valid mimetype
     * @return Returns the default extension for the mimetype
     * @throws AlfrescoRuntimeException if the mimetype doesn't exist
     */
    public String getExtension(String mimetype)
    {
        String extension = extensionsByMimetype.get(mimetype);
        if (extension == null)
        {
            throw new AlfrescoRuntimeException("No extension available for mimetype: " + mimetype);
        }
        return extension;
    }

    public Map<String, String> getDisplaysByExtension()
    {
        return displaysByExtension;
    }

    public Map<String, String> getDisplaysByMimetype()
    {
        return displaysByMimetype;
    }

    public Map<String, String> getExtensionsByMimetype()
    {
        return extensionsByMimetype;
    }

    public List<String> getMimetypes()
    {
        return mimetypes;
    }

    public Map<String, String> getMimetypesByExtension()
    {
        return mimetypesByExtension;
    }

    /**
     * @see #MIMETYPE_BINARY
     */
    public String guessMimetype(String filename)
    {
        String mimetype = MIMETYPE_BINARY;
        // extract the extension
        int index = filename.lastIndexOf('.');
        if (index > -1 && (index < filename.length() - 1))
        {
            String extension = filename.substring(index + 1);
            if (mimetypesByExtension.containsValue(extension))
            {
                mimetype = mimetypesByExtension.get(extension);
            }
        }
        return mimetype;
    }
}
