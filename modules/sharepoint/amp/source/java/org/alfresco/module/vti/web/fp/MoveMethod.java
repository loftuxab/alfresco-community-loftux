/*
* Copyright (C) 2005-2010 Alfresco Software Limited.
*
* This file is part of Alfresco
*
* Alfresco is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Alfresco is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
*/
package org.alfresco.module.vti.web.fp;

import javax.servlet.http.HttpServletRequestWrapper;

import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.lang.StringUtils;

/**
 * Implements the WebDAV MOVE method with VTI specific behaviours
 */
public class MoveMethod extends org.alfresco.repo.webdav.MoveMethod
{
    private String alfrescoContext;
    private VtiPathHelper pathHelper;

    public MoveMethod(VtiPathHelper pathHelper)
    {
        this.alfrescoContext = pathHelper.getAlfrescoContext();
        this.pathHelper = pathHelper;
    }

    /**
     * Alters the request to include the servlet path (needed for 
     *  building the destination path), then executes as usual
     */
    @Override
    public void execute() throws WebDAVServerException {
       // Wrap the request to include the servlet path
       m_request = new HttpServletRequestWrapper(m_request) {
          public String getServletPath()
          {
              return alfrescoContext.equals("") ? "/" : alfrescoContext;
          }
       };

       // Now have the move executed as normal
       super.execute();
    }

   /**
     * Returns the path, excluding the Servlet Context (if present)
     * @see org.alfresco.repo.webdav.WebDAVMethod#getPath()
     */
    @Override
    public String getPath()
    {
        String path = AbstractMethod.getPathWithoutContext(alfrescoContext, m_request);

        if (path.contains(VtiPathHelper.ALTERNATE_PATH_DOCUMENT_IDENTIFICATOR))
        {
            logger.warn("Found  '_IDX_NODE_' entry in node path for MOVE METHOD. Error (additional support is required), if it is not part of original path.");
        }

        if (path.contains(VtiPathHelper.ALTERNATE_PATH_SITE_IDENTIFICATOR))
        {
            String[] parts = path.split("/");

            for (int i = 0; i < parts.length; i++)
            {
                if (parts[i].contains(VtiPathHelper.ALTERNATE_PATH_SITE_IDENTIFICATOR))
                {
                    parts[i] = pathHelper.resolvePathFileInfo(parts[i]).getName();
                    path = StringUtils.join(parts, "/");
                    break;
                }
            }
        }

        return path;
    }
    
    @Override
    protected FileInfo getNodeForPath(NodeRef rootNodeRef, String path) throws FileNotFoundException
    {
        FileInfo nodeInfo = pathHelper.resolvePathFileInfo(path);
        return nodeInfo;
    }
}
