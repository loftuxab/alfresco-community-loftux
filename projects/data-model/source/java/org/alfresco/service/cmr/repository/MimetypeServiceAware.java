package org.alfresco.service.cmr.repository;

/**
 * Consumers of the {@link MimetypeService} should implement this interface.
 * 
 * @author Matt Ward
 */
public interface MimetypeServiceAware
{
    void setMimetypeService(MimetypeService mimetypeService);
}
