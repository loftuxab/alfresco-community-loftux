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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.deployment.impl.server.DeployedFile;
import org.alfresco.deployment.impl.server.Deployment;

/**
 * FSDeployment Runnable to be used for running external programs.
 * @author britt
 */
public class ProgramRunnable implements FSDeploymentRunnable
{
    private static final long serialVersionUID = -6694971994859005513L;

    private Deployment fDeployment;
    
    /**
     * The path to the executable.
     */
    private String fProgram;
    
    /**
     * The directory that the program should run in.
     */
    private String fDirectory;
    
    /**
     * Additional arguments to the program.
     */
    private List<String> fArguments;
    
    ProgramRunnable()
    {
        fArguments = new ArrayList<String>();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.FSDeploymentRunnable#init(org.alfresco.deployment.impl.server.Deployment)
     */
    public void init(Deployment deployment)
    {
        fDeployment = deployment;
    }
    
    public void setProgram(String program)
    {
        fProgram = program;
    }
    
    public void setDirectory(String directory)
    {
        fDirectory = directory;
    }

    public void setArguments(List<String> arguments)
    {
        fArguments = arguments;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        try
        {
            File tempFile = File.createTempFile("deployment", "txt");
            Writer out = new FileWriter(tempFile);
            for (DeployedFile file : fDeployment)
            {
                out.write(file.getType().toString() + " '" + file.getPath().replaceAll("'", "\\\\'") + "' " + file.getGuid() + "\n");
            }
            out.close();
            Runtime runTime = Runtime.getRuntime();
            int commandLength = 2 + fArguments.size();
            String[] command = new String[commandLength];
            command[0] = fProgram;
            command[1] = tempFile.getAbsolutePath();
            int off = 2;
            for (String arg : fArguments)
            {
                command[off++] = arg;
            }
            Map<String, String> envMap = System.getenv();
            String[] env = new String[envMap.size()];
            off = 0;
            for (Map.Entry<String, String> entry : envMap.entrySet())
            {
                env[off++] = entry.getKey() + '=' + entry.getValue();
            }
            Process process = runTime.exec(command, env, new File(fDirectory));
            process.waitFor();
            tempFile.delete();
        }
        catch (IOException ie)
        {
            // Do nothing for now.
        }
        catch (InterruptedException inte)
        {
            // Do nothing for now.
        }
    }
}
