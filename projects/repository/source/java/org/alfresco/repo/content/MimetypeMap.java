package org.alfresco.repo.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigService;
import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Provides a bidirectional mapping between well-known mimetypes and
 * the registered file extensions.
 * 
 * @author Derek Hulley
 */
public class MimetypeMap
{
    private static final String CONFIG_AREA = "mimetype-map";
    private static final String CONFIG_CONDITION = "Mimetype Map";
    private static final String ELEMENT_MIMETYPES = "mimetypes";
    private static final String ELEMENT_MIMETYPE = "mimetype";
    private static final String ATTR_MIMETYPE = "mimetype";
    private static final String ATTR_DISPLAY = "display";
    private static final String ELEMENT_EXTENSION = "extension";
    private static final String ATTR_DEFAULT = "default";
    
    private ConfigService configService;
    
    private Collection<String> mimetypes;
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
        this. displaysByMimetype = new HashMap<String, String>(59);
        this.displaysByExtension = new HashMap<String, String>(59);

        Config config = configService.getConfig(CONFIG_CONDITION, CONFIG_AREA);
        ConfigElement mimetypesElement = config.getConfigElement(ELEMENT_MIMETYPES);
        List<ConfigElement> mimetypes = mimetypesElement.getChildren();
        for (ConfigElement mimetypeElement : mimetypes)
        {
            String mimetype = mimetypeElement.getAttribute(ATTR_MIMETYPE);
            String mimetypeDisplay = mimetypeElement.getAttribute(ATTR_DISPLAY);
            if (this.mimetypes.contains(mimetype))
            {
                throw new AlfrescoRuntimeException("Duplicate mimetype definition: " + mimetype);
            }
            // add to list of mimetypes
            this.mimetypes.add(mimetype);
            // add to map of mimetype displays
            this.displaysByMimetype.put(mimetype, mimetypeDisplay);
            List<ConfigElement> extensions = mimetypeElement.getChildren();
            for (ConfigElement extension : extensions)
            {
                String extensionValue = extension.getValue();
                String extensionDisplay = extension.getAttribute(ATTR_DISPLAY);
                String isDefaultStr = extension.getAttribute(ATTR_DEFAULT);
                boolean isDefault = Boolean.parseBoolean(isDefaultStr);
                // add to map of mimetypes by extension
                this.mimetypesByExtension.put(extensionValue, mimetype);
                // add to map of extension displays
                this.displaysByExtension.put(extensionValue, extensionDisplay);
                // add to map of extensions by mimetype
                if (isDefault)
                {
                    this.extensionsByMimetype.put(mimetype, extensionValue);
                }
            }
        }
        
        // make the collections read-only
        this.mimetypes = Collections.unmodifiableCollection(this.mimetypes);
        this.extensionsByMimetype = Collections.unmodifiableMap(this.extensionsByMimetype);
        this.mimetypesByExtension = Collections.unmodifiableMap(this.mimetypesByExtension);
        this.displaysByMimetype = Collections.unmodifiableMap(this.displaysByMimetype);
        this.displaysByExtension = Collections.unmodifiableMap(this.displaysByExtension);
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

    public Collection<String> getMimetypes()
    {
        return mimetypes;
    }

    public Map<String, String> getMimetypesByExtension()
    {
        return mimetypesByExtension;
    }
}
