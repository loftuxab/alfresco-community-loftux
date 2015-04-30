/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.enterprise.repo.web.scripts.bulkimport.inplace;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.enterprise.repo.bulkimport.impl.InPlaceNodeImporterFactory;
import org.alfresco.repo.bulkimport.BulkImportParameters;
import org.alfresco.repo.bulkimport.NodeImporter;
import org.alfresco.repo.bulkimport.impl.MultiThreadedBulkFilesystemImporter;
import org.alfresco.repo.web.scripts.bulkimport.AbstractBulkFileSystemImportWebScript;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Web Script class that invokes a BulkFilesystemImporter implementation. Supports
 * in-place bulk filesystem import; that is, the content files are left in place i.e.
 * not copied.
 *
 * @since 4.0
 * 
 */
public class BulkFilesystemImportWebScript extends AbstractBulkFileSystemImportWebScript
{
    // Web Script parameters (non-inherited)
    private static final String PARAMETER_CONTENT_STORE = "contentStore";
    
    private MultiThreadedBulkFilesystemImporter bulkImporter;
	private InPlaceNodeImporterFactory nodeImporterFactory;

	public void setBulkImporter(MultiThreadedBulkFilesystemImporter bulkImporter)
	{
		this.bulkImporter = bulkImporter;
	}

	public void setNodeImporterFactory(InPlaceNodeImporterFactory nodeImporterFactory)
	{
		this.nodeImporterFactory = nodeImporterFactory;
	}

    /**
     * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.Status, org.springframework.extensions.webscripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest request, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        String relativeSourceDirectory = null;
        String destinationContentStoreName = null;
        String targetNodeRefStr = null;
        String targetPath = null;
        String batchSizeStr = null;
        String numThreadsStr = null;
        String disableRulesStr = null;
        
        cache.setNeverCache(true);

        try
        {
        	importInProgress = bulkImporter.getStatus().inProgress();
            if(!importInProgress)
            {
                NodeRef targetNodeRef = null;
                boolean replaceExisting = true; // always
                int batchSize = bulkImporter.getDefaultBatchSize();
                int numThreads = bulkImporter.getDefaultNumThreads();
                boolean disableRules = false;

                // Retrieve, validate and convert parameters
                relativeSourceDirectory = request.getParameter(PARAMETER_SOURCE_DIRECTORY);
                destinationContentStoreName = request.getParameter(PARAMETER_CONTENT_STORE);
                targetNodeRefStr = request.getParameter(PARAMETER_TARGET_NODEREF);
                targetPath = request.getParameter(PARAMETER_TARGET_PATH);
                batchSizeStr = request.getParameter(PARAMETER_BATCH_SIZE);
                numThreadsStr = request.getParameter(PARAMETER_NUM_THREADS);
                disableRulesStr = request.getParameter(PARAMETER_DISABLE_RULES);

                targetNodeRef = getTargetNodeRef(targetNodeRefStr, targetPath);
                
                if (relativeSourceDirectory == null || relativeSourceDirectory.trim().length() == 0)
                {
                    throw new WebScriptException("Error: mandatory parameter '" + PARAMETER_SOURCE_DIRECTORY + "' was not provided !");
                }
                
                if (disableRulesStr != null && disableRulesStr.trim().length() > 0)
                {
                    disableRules = PARAMETER_VALUE_DISABLE_RULES.equals(disableRulesStr);
                }

                // Initiate the import
        		NodeImporter nodeImporter = nodeImporterFactory.getNodeImporter(destinationContentStoreName, relativeSourceDirectory);
                BulkImportParameters bulkImportParameters = new BulkImportParameters();
                
                if (numThreadsStr != null && numThreadsStr.trim().length() > 0)
                {
                	try
                	{
                		numThreads = Integer.parseInt(numThreadsStr);
                		if(numThreads < 1)
                		{
                            throw new RuntimeException("Error: parameter '" + PARAMETER_NUM_THREADS + "' must be an integer > 0.");
                		}
                        bulkImportParameters.setNumThreads(numThreads);
                	}
                	catch(NumberFormatException e)
                	{
                        throw new RuntimeException("Error: parameter '" + PARAMETER_NUM_THREADS + "' must be an integer > 0.");
                	}
                }
                
                if (batchSizeStr != null && batchSizeStr.trim().length() > 0)
                {
                	try
                	{
                		batchSize = Integer.parseInt(batchSizeStr);
                		if(batchSize < 1)
                		{
                            throw new RuntimeException("Error: parameter '" + PARAMETER_BATCH_SIZE + "' must be an integer > 0.");
                		}
                        bulkImportParameters.setBatchSize(batchSize);
                	}
                	catch(NumberFormatException e)
                	{
                        throw new RuntimeException("Error: parameter '" + PARAMETER_BATCH_SIZE + "' must be an integer > 0.");
                	}
                }

                bulkImportParameters.setReplaceExisting(replaceExisting);
                bulkImportParameters.setTarget(targetNodeRef);
                bulkImportParameters.setDisableRulesService(disableRules);

                bulkImporter.asyncBulkImport(bulkImportParameters, nodeImporter);

                // redirect to the status Web Script
    	        status.setCode(Status.STATUS_MOVED_TEMPORARILY);
    	        status.setRedirect(true);
    	        status.setLocation(request.getServiceContextPath() + WEB_SCRIPT_URI_BULK_FILESYSTEM_IMPORT_STATUS);
            }
            else
            {
            	model.put(IMPORT_ALREADY_IN_PROGRESS_MODEL_KEY, I18NUtil.getMessage(IMPORT_ALREADY_IN_PROGRESS_ERROR_KEY));
            }
        }
        catch (WebScriptException wse)
        {
        	status.setCode(Status.STATUS_BAD_REQUEST, wse.getMessage());
        	status.setRedirect(true);
        }
        catch (FileNotFoundException fnfe)
        {
        	status.setCode(Status.STATUS_BAD_REQUEST,"The repository path '" + targetPath + "' does not exist !");
        	status.setRedirect(true);
        }
        catch(IllegalArgumentException iae)
        {
        	status.setCode(Status.STATUS_BAD_REQUEST,iae.getMessage());
        	status.setRedirect(true);
        }
        catch (Throwable t)
        {
            throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, buildTextMessage(t), t);
        }
        
        return model;
    }

}