/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework.render.bean;

import org.alfresco.web.framework.exception.RendererExecutionException;
import org.alfresco.web.framework.exception.RendererInitializationException;
import org.alfresco.web.framework.model.Chrome;
import org.alfresco.web.framework.render.AbstractRenderer;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderFocus;
import org.alfresco.web.framework.render.RenderHelper;
import org.alfresco.web.site.Timer;

/**
 * Bean responsible for rendering chrome
 * 
 * @author muzquiano
 */
public class ChromeRenderer extends AbstractRenderer
{
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.render.Renderer#init()
	 */
	public void init()
		throws RendererInitializationException
	{
		super.init();
		
		// additional initialization (if necessary)
	}
	
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractRenderer#body(org.alfresco.web.framework.render.RendererContext)
     */
    public void body(RenderContext parentContext)
    	throws RendererExecutionException
	{
    	Chrome chrome = (Chrome) parentContext.getObject();
    	
    	// create a new render context (for the chrome)
        // execute the renderer
        RenderContext context = RenderHelper.provideRenderContext(parentContext, chrome);
        try
        {
            // start a timer
            if (Timer.isTimerEnabled())
                Timer.start(context, "ChromeRenderer-" + chrome.getId());

            // process the chrome
            RenderHelper.processRenderable(context, RenderFocus.BODY, chrome);
        }
        finally
        {
            // release the render context
        	context.release();

            if (Timer.isTimerEnabled())
                Timer.stop(context, "ChromeRenderer-" + chrome.getId());
        }    	
	}	
}
