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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.vti.method;

import java.io.IOException;

import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.VtiInternalErrorException;
import org.alfresco.module.vti.VtiRequest;
import org.alfresco.module.vti.VtiResponse;
import org.alfresco.module.vti.handler.VtiMethodHandler;
import org.alfresco.module.vti.metadata.DocMetaInfo;
import org.alfresco.module.vti.metadata.dic.VtiConstraint;
import org.alfresco.module.vti.metadata.dic.VtiProperty;
import org.alfresco.module.vti.metadata.dic.VtiType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Abstract base class for all the Vti method handling classes
 *
 * @author Michael Shavnev
 */
public abstract class AbstractVtiMethod implements VtiMethod
{
    private final static Log logger = LogFactory.getLog(AbstractVtiMethod.class);

    protected VtiMethodHandler vtiHandler;

    private final static String MAGIC_STRING_IRRECOVERABLE_ERROR = "*-*-* :-| :^| :-/  :-( 8-( *-*-*";

    /**
     * @return the vtiHandler
     */
    public VtiMethodHandler getVtiHandler()
    {
        return vtiHandler;
    }

    /**
     * @param vtiHandler the vtiHandler to set
     */
    public void setVtiHandler(VtiMethodHandler vtiHandler)
    {
        this.vtiHandler = vtiHandler;
    }


    public final void execute(VtiRequest request, VtiResponse response)
    {
        try
        {
            try
            {
                doExecute(request, response);
            }
            catch (VtiException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error(getName(), e);
                }

                response.beginVtiAnswer(getName(), ServerVersionMethod.version);
                response.beginList("status");
                response.addParameter("status", String.valueOf(e.getErrorCode()));
                response.addParameter("osstatus", "0");
                response.addParameter("msg", e.getMessage());
                response.addParameter("osmsg", "");
                response.endList();
                response.endVtiAnswer();
            }
            catch (VtiInternalErrorException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error(getName(), e);
                }

                response.getOutputStream().write(MAGIC_STRING_IRRECOVERABLE_ERROR.getBytes());
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected abstract void doExecute(VtiRequest request, VtiResponse response) throws VtiException, IOException;


    protected void processDocMetaInfo(DocMetaInfo docMetaInfo, VtiRequest request, VtiResponse response) throws VtiException, IOException
    {
        response.writeMetaDictionary(VtiProperty.FILE_THICKETDIR, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getThicketdir());
        response.writeMetaDictionary(VtiProperty.FILE_TIMECREATED, VtiType.TIME, VtiConstraint.R, docMetaInfo.getTimecreated());
        response.writeMetaDictionary(VtiProperty.FILE_TIMELASTMODIFIED, VtiType.TIME, VtiConstraint.R, docMetaInfo.getTimelastmodified());
        response.writeMetaDictionary(VtiProperty.FILE_TIMELASTWRITTEN , VtiType.TIME, VtiConstraint.R, docMetaInfo.getTimelastwritten());

        if (docMetaInfo.isFolder())
        {
            response.writeMetaDictionary(VtiProperty.FOLDER_DIRLATESTSTAMP, VtiType.TIME, VtiConstraint.R, docMetaInfo.getDirlateststamp());
            response.writeMetaDictionary(VtiProperty.FOLDER_HASSUBDIRS, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getHassubdirs());
            response.writeMetaDictionary(VtiProperty.FOLDER_ISBROWSABLE, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getIsbrowsable());
            response.writeMetaDictionary(VtiProperty.FOLDER_ISCHILDWEB, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getIschildweb());
            response.writeMetaDictionary(VtiProperty.FOLDER_ISEXECUTABLE, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getIsexecutable());
            response.writeMetaDictionary(VtiProperty.FOLDER_ISSCRIPTABLE, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getIsscriptable());
            response.writeMetaDictionary(VtiProperty.FOLDER_LISTBASETYPE, VtiType.INT, VtiConstraint.R, docMetaInfo.getListbasetype());
        }
        else
        {
            response.writeMetaDictionary(VtiProperty.FILE_TITLE, VtiType.STRING, VtiConstraint.R, docMetaInfo.getTitle());
            response.writeMetaDictionary(VtiProperty.FILE_FILESIZE, VtiType.INT, VtiConstraint.R, docMetaInfo.getFilesize());
            response.writeMetaDictionary(VtiProperty.FILE_METATAGS , VtiType.VECTOR, VtiConstraint.R, docMetaInfo.getMetatags());
            response.writeMetaDictionary(VtiProperty.FILE_SOURCECONTROLCHECKEDOUTBY, VtiType.STRING, VtiConstraint.R, docMetaInfo.getSourcecontrolcheckedoutby());
            response.writeMetaDictionary(VtiProperty.FILE_SOURCECONTROLTIMECHECKEDOUT, VtiType.TIME, VtiConstraint.R, docMetaInfo.getSourcecontroltimecheckedout());
            response.writeMetaDictionary(VtiProperty.FILE_SOURCECONTROLVERSION, VtiType.STRING, VtiConstraint.R, "V" + docMetaInfo.getSourcecontrolversion());
            response.writeMetaDictionary(VtiProperty.FILE_SOURCECONTROLLOCKEXPIRES, VtiType.TIME, VtiConstraint.R, docMetaInfo.getSourcecontrollockexpires());
            response.writeMetaDictionary(VtiProperty.FILE_THICKETSUPPORTINGFILE, VtiType.BOOLEAN, VtiConstraint.R, docMetaInfo.getThicketsupportingfile());
        }

    }

}
