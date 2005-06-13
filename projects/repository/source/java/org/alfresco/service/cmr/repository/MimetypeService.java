package org.alfresco.service.cmr.repository;

import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;


public interface MimetypeService
{
    /**
     * @param mimetype a valid mimetype
     * @return Returns the default extension for the mimetype
     * @throws AlfrescoRuntimeException if the mimetype doesn't exist
     */
    public String getExtension(String mimetype);

    public Map<String, String> getDisplaysByExtension();

    public Map<String, String> getDisplaysByMimetype();

    public Map<String, String> getExtensionsByMimetype();

    public List<String> getMimetypes();

    public Map<String, String> getMimetypesByExtension();

}
