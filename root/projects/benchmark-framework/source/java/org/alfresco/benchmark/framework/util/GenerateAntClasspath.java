/*
 * Copyright (C) 2005 Alfresco, Inc.
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
package org.alfresco.benchmark.framework.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Roy Wetherall
 */
public class GenerateAntClasspath
{
    private static final String FILE_BEGIN = "<project name=\"build-classpath\" basedir=\".\">\n" +
                                             "   <path id=\"path.generated\">\n";
    private static final String FILE_END   = "   </path>\n" +
                                             "</project>";
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            FileWriter fileWriter = new FileWriter("build-classpath.xml");
            try
            {
                fileWriter.write(FILE_BEGIN);
                
                BufferedReader fileReader = new BufferedReader(new FileReader("config/classpath.config"));
                try
                {
                    String nextLine = fileReader.readLine();
                    while (nextLine != null)
                    {
                        nextLine = resolveEnvironmentVariables(nextLine.trim());
                        if (nextLine.length() != 0 && nextLine.startsWith("#") == false)
                        {
                            if (nextLine.indexOf("*.jar") != -1)
                            {
                                int lastIndex = nextLine.lastIndexOf("/");
                                String path = nextLine.substring(0, lastIndex);
                                String file = nextLine.substring(lastIndex + 1);
                                fileWriter.write("      <fileset dir=\"" + path + "\" includes=\"" + file + "\"/>\n"); 
                            }
                            else
                            {
                                fileWriter.write("      <pathelement path=\"" + nextLine + "\" />\n");
                            }
                        }
                        nextLine = fileReader.readLine();
                    }
                }
                finally
                {
                    fileReader.close();
                }
                
                fileWriter.write(FILE_END);
            }
            finally
            {
                fileWriter.close();
            }
        }
        catch (FileNotFoundException exception)
        {
            throw new RuntimeException("No classpath.config file was found.", exception);
        }
        catch (IOException exception)
        {
            throw new RuntimeException("Error reading classpath.config file.", exception);
        }
    }
    
    private static String resolveEnvironmentVariables(String line)
    {
        StringBuilder stringBuilder = new StringBuilder(line.length());
        char[] characters = line.toCharArray();
        for (int i = 0; i < characters.length; i++)
        {
            char character = characters[i];
            if (character == '$')
            {
                char nextChar = characters[i+1];
                if (nextChar == '{')
                {
                    // Read untill }
                    StringBuilder variableName = new StringBuilder();
                    int j = i +2;
                    while (j < characters.length)
                    {
                        char varChar = characters[j];
                        if (varChar == '}')
                        {
                            break;
                        }
                        else
                        {
                            variableName.append(varChar);
                        }
                        j++;
                    }                    
                    
                    // try and get the value of the environment variable
                    String value = System.getenv(variableName.toString());
                    stringBuilder.append(value);                    
                    i = j;
                }
                else
                {
                    stringBuilder.append(character);
                }
            }
            else
            {
                stringBuilder.append(character);
            }
        }
        
        return stringBuilder.toString();
    }

}
