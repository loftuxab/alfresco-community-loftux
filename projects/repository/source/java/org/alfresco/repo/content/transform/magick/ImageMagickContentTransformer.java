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
package org.alfresco.repo.content.transform.magick;

import java.io.File;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.util.exec.RuntimeExec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Makes use of the {@link http://www.textmining.org/ TextMining} library to
 * perform conversions from MSWord documents to text.
 * 
 * @author Derek Hulley
 */
public class ImageMagickContentTransformer extends AbstractImageMagickContentTransformer
{
    /** source variable name */
    public static final String VAR_SOURCE = "${source}";
    /** source variable regex */
    public static final String VAR_SOURCE_REGEX = "\\$\\{source\\}";
    /** target variable name */
    public static final String VAR_TARGET = "${target}";
    /** target variable regex */
    public static final String VAR_TARGET_REGEX = "\\$\\{target\\}";
    
    private static final Log logger = LogFactory.getLog(ImageMagickContentTransformer.class);
    
    private String convertCommand;
    
    public ImageMagickContentTransformer()
    {
    }
    
    /**
     * Set the convertCommand or the command that must be executed in order to run
     * <b>ImageMagick</b>.  Whether or not this is the full path to the convertCommand
     * or just the convertCommand itself depends the environment setup.
     * <p>
     * The command must contain the variables <code>${source}</code> and
     * <code>${target}</code>, which will be replaced by the names of the file to
     * be transformed and the name of the output file respectively.
     * <pre>
     *    convert ${source} ${target}
     * </pre>
     *  
     * @param convertCommand
     */
    public void setConvertCommand(String executable)
    {
        if (!executable.contains(VAR_SOURCE) || !executable.contains(VAR_TARGET))
        {
            throw new AlfrescoRuntimeException
                    ("ImageMagick convertCommand string contain the ${source} and ${target} variables");
        }
        this.convertCommand = executable;
    }

    /**
     * Checks for the JMagick and ImageMagick dependencies, using the common
     * {@link #transformInternal(File, File) transformation method} to check
     * that the sample image can be converted. 
     */
    public void init()
    {
        if (convertCommand == null)
        {
            throw new AlfrescoRuntimeException("Executable not set");
        }
        super.init();
    }
    
    /**
     * Transform the image content from the source file to the target file
     * 
     * @param sourceFile
     * @param targetFile
     * @throws Exception
     */
    protected void transformInternal(File sourceFile, File targetFile) throws Exception
    {
        String sourceFilename = sourceFile.getAbsolutePath();
        String targetFilename = targetFile.getAbsolutePath();
        // avoid regex replacement issue
        if (File.separatorChar == '\\')
        {
            sourceFilename = sourceFilename.replace('\\', '/');
            targetFilename = targetFilename.replace('\\', '/');
        }
        
        // substitute the variables for the filenames
        String exe = convertCommand.replaceAll(VAR_SOURCE_REGEX, sourceFilename);
        exe = exe.replaceAll(VAR_TARGET_REGEX, targetFilename);
        
        // convert back
        if (File.separatorChar == '\\')
        {
            exe = exe.replace('/', '\\');
        }
       
        // execute the statement
        RuntimeExec exec = new RuntimeExec(exe);
        int retVal = exec.execute();
        if (retVal != 0 && exec.getStdErr() != null && exec.getStdErr().length() > 0)
        {
            throw new ContentIOException("Failed to perform ImageMagick transformation: \n" + exec);
        }
        // success
        if (logger.isDebugEnabled())
        {
            logger.debug("ImageMagic executed successfully: \n" + exec);
        }
    }
}
