/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */

package org.alfresco.deployment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.alfresco.deployment.impl.server.DeployedFile;
import org.alfresco.deployment.impl.server.Deployment;
import org.alfresco.util.GUID;

/**
 * Example post filesystem deployment runnable.
 * @author britt
 */
public class SampleRunnable implements FSDeploymentRunnable
{
    private static final long serialVersionUID = -5792264492686730729L;

    // The deployment just completed.
    private Deployment fDeployment = null;
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.FSDeploymentRunnable#init(org.alfresco.deployment.impl.server.Deployment)
     */
    public void init(Deployment deployment)
    {
        fDeployment = deployment;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        String guid = GUID.generate();
        try
        {
            Writer out = new FileWriter("dep-record-" + guid);
            for (DeployedFile file : fDeployment)
            {
                out.write(file.getType().toString() + " " + file.getPath() + " " + file.getGuid() + "\n");
            }
            out.close();
        }
        catch (IOException e)
        {
            // Do nothing.
        }
    }
}
