/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
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
