package org.alfresco.model;

import org.alfresco.api.AlfrescoPublicApi;     
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Rendition Model Constants
 */
@AlfrescoPublicApi
public interface RenditionModel
{
    static final QName ASPECT_RENDITION = QName.createQName(NamespaceService.RENDITION_MODEL_1_0_URI, "rendition");
    static final QName ASPECT_HIDDEN_RENDITION = QName.createQName(NamespaceService.RENDITION_MODEL_1_0_URI, "hiddenRendition");
    static final QName ASPECT_VISIBLE_RENDITION = QName.createQName(NamespaceService.RENDITION_MODEL_1_0_URI, "visibleRendition");
    
    static final QName ASPECT_RENDITIONED = QName.createQName(NamespaceService.RENDITION_MODEL_1_0_URI, "renditioned");
    static final QName ASSOC_RENDITION = QName.createQName(NamespaceService.RENDITION_MODEL_1_0_URI, "rendition");
    
    /**
     * @since 4.0.1
     */
    static final QName ASPECT_PREVENT_RENDITIONS = QName.createQName(NamespaceService.RENDITION_MODEL_1_0_URI, "preventRenditions");
}
