/*-----------------------------------------------------------------------------
*  Copyright 2006 Alfresco Inc.
*
*  Licensed under the Mozilla Public License version 1.1
*  with a permitted attribution clause. You may obtain a
*  copy of the License at:
*
*      http://www.alfresco.org/legal/license.txt
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
*  either express or implied. See the License for the specific
*  language governing permissions and limitations under the
*  License.
*
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
