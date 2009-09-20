/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.io.File;
import java.io.IOException;

import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation for Java backed webscript to return audit
 * log of RM events, optionally scoped to an RM node.
 * 
 * @author Gavin Cornwell
 */
public class AuditLogGet extends BaseAuditRetrievalWebScript
{
    /** Logger */
    private static Log logger = LogFactory.getLog(AuditLogGet.class);
    
    protected final static String PARAM_EXPORT = "export";

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        File auditTrail = null;
        
        try
        {
            // parse the parameters and get a file containing the audit trail
            auditTrail = this.rmAuditService.getAuditTrailFile(parseQueryParameters(req), parseReportFormat(req));
            
            if (logger.isDebugEnabled())
                logger.debug("Streaming audit trail from file: " + auditTrail.getAbsolutePath());
            
            boolean attach = false;
            String attachFileName = null;
            String export = req.getParameter(PARAM_EXPORT);
            if (export != null && Boolean.parseBoolean(export))
            {
                attach = true;
                attachFileName = auditTrail.getName();
                
                if (logger.isDebugEnabled())
                    logger.debug("Exporting audit trail using file name: " + attachFileName);
            }
            
            // stream the file back to the client
            streamContent(req, res, auditTrail, attach, attachFileName);
        }
        finally
        {
            if (auditTrail != null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug(
                            "Audit results written to file: \n" +
                            "   File:      " + auditTrail + "\n" +
                            "   Parameter: " + parseQueryParameters(req));
                }
                else
                {
                    auditTrail.delete();
                }
            }
        }
    }
}