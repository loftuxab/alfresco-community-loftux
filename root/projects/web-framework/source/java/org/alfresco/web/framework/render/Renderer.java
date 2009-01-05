package org.alfresco.web.framework.render;

import org.alfresco.web.framework.exception.RendererExecutionException;

public interface Renderer 
{
    /**
     * Called upon initialization of the renderer
     */
    public void init();
    
    /**
     * Executes the renderer in the given focus
     * 
     * @param renderContext
     * @param focus
     * 
     * @throws RendererExecutionException
     */
    public void render(RenderContext renderContext, RenderFocus focus)
        throws RendererExecutionException;    

    /**
     * Executes the renderer in the "all" mode 
     * 
     * @param rendererContext
     * @throws RendererExecutionException
     */
    public void all(RenderContext renderContext)
        throws RendererExecutionException;    

    /**
     * Executes the renderer in the "head" mode
     * 
     * @param renderContext
     * @throws RendererExecutionException
     */
    public void header(RenderContext renderContext)
        throws RendererExecutionException;    

    /**
     * Executes the renderer in the "body" mode
     * 
     * @param renderContext
     * @throws RendererExecutionException
     */
    public void body(RenderContext renderContext)
        throws RendererExecutionException;
    
    /**
     * Executes the renderer in the "footer" mode
     * 
     * @param renderContext
     * @throws RendererExecutionException
     */
    public void footer(RenderContext renderContext)
        throws RendererExecutionException;

}
