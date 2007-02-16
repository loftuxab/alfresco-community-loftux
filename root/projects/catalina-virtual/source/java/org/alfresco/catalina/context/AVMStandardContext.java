/*-----------------------------------------------------------------------------
*  Copyright 2006 Alfresco Inc.
*
*   * This program is free software; you can redistribute it and/or
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
 * http://www.alfresco.com/legal/licensing"*
*
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    AVMStandardContext.java
*----------------------------------------------------------------------------*/


package org.alfresco.catalina.context;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.LifecycleException;


public class   AVMStandardContext 
       extends    StandardContext
{
    /**
     * Create a new StandardContext component with the default basic Valve.
     */
    public AVMStandardContext() {
        super();
    }

    /**
     * The descriptive information string for this implementation.
     */
    private static final String info =
        "org.apache.catalina.context.AVMStandardContext/1.0";



    /**
     * Stop this Context component.
     *
     * @exception LifecycleException if a shutdown error occurs
     */
    public synchronized void stop() throws LifecycleException 
    {
        super.stop();
    }

    /**
     * Start this Context component.
     *
     * @exception LifecycleException if a startup error occurs
     */
    public synchronized void start() throws LifecycleException 
    {
        super.start();
    }

    /**
     * Reload this web application, if reloading is supported.
     * <p>
     * <b>IMPLEMENTATION NOTE</b>:  This method is designed to deal with
     * reloads required by changes to classes in the underlying repositories
     * of our class loader.  It does not handle changes to the web application
     * deployment descriptor.  If that has occurred, you should stop this
     * Context and create (and start) a new Context instance instead.
     *
     * @exception IllegalStateException if the <code>reloadable</code>
     *  property is set to <code>false</code>.
     */
    public synchronized void reload() 
    {
        // jcox TODO RESUME
        // This might be where I'd do the fancy footwork necessary 
        // to deal with recursive reload.
        //
        // The StandardContextValve does a 1 second like this
        // to avoid talking to webapps that are in the process
        // of reloading:
        //
        //        // Wait if we are reloading
        //        while (context.getPaused()) 
        //        {
        //            try   { Thread.sleep(1000); } 
        //            catch (InterruptedException e) { ; }
        //        }
        //

        super.reload();   // setPaused(true) ...stop/start...setPaused(false)

        // jcox TODO: 
        //      Consider doing a recursive reload() on kids here.


    }
    

    /**
     * Return a String representation of this component.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer();
        if (getParent() != null) {
            sb.append(getParent().toString());
            sb.append(".");
        }
        sb.append("AVMStandardContext[");
        sb.append(getName());
        sb.append("]");
        return (sb.toString());

    }
}
