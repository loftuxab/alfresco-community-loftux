package org.alfresco.opencmis.dictionary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * Temporary workaround for:
 * <ul>
 *     <li>ACE-5041: CLONE - google docs content cannot be accessed via cmis</li>
 * </ul>
 * 
 * TODO: Remove this bean when rework for MNT-14819 is complete.
 * 
 * @author Matt Ward
 */
public final class CMISDictionaryReload extends AbstractLifecycleBean
{
    private static final Log log = LogFactory.getLog(CMISDictionaryReload.class);
    private final CMISAbstractDictionaryService cmisDictService;
    private final boolean enabled;
    
    public CMISDictionaryReload(CMISAbstractDictionaryService cmisDictService, boolean enabled)
    {
        this.cmisDictService = cmisDictService;
        this.enabled = enabled;
    }
    
    public void reload()
    {
        if (enabled)
        {
            // Avoid deadlock by making sure we already have a registry present.
            cmisDictService.getRegistry();
            log.debug("Reloading CMIS dictionary.");
            cmisDictService.afterDictionaryInit();
        }
    }

    @Override
    protected void onBootstrap(ApplicationEvent event)
    {
        reload();
    }

    @Override
    protected void onShutdown(ApplicationEvent event)
    {
        // Do nothing.
    }
}
