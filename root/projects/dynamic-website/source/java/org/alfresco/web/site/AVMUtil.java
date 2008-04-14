/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site;

import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.site.filesystem.AVMFileSystem;
import org.alfresco.web.site.filesystem.IFile;

/**
 * This class will only work in-process within the Alfresco server. Thus, it's
 * pretty much only for scripting.
 * 
 * If you try to use this on a production/runtime server, you'll get
 * ClassNotFoundExceptions, thus, the entire thing is try/catched in case you
 * should happen to do so.
 * 
 * @author muzquiano
 * 
 */
public class AVMUtil
{
    public static String TYPE_AVMPLAINCONTENT = "{http://www.alfresco.org/model/wcmmodel/1.0}avmplaincontent";
    public static String ASPECT_FORMINSTANCEDATA = "{http://www.alfresco.org/model/wcmappmodel/1.0}forminstancedata";
    public static String PROP_PARENTFORMNAME = "{http://www.alfresco.org/model/wcmappmodel/1.0}parentformname"; // site-configuration
    public static String PROP_ORIGINALPARENTPATH = "{http://www.alfresco.org/model/wcmappmodel/1.0}orginalparentpath"; // ads--admin--preview:/www/avm_webapps/ROOT

    public static void ensureFormBinding(RequestContext context,
            String relativePath, String fileName, String formName)
    {
        try
        {
            // get the avm file system
            AVMFileSystem avmFileSystem = (AVMFileSystem) context.getFileSystem();

            // get the avm path for the relativePath
            String avmPath = avmFileSystem.getAVMPath(relativePath);

            // the relative file paths
            String relativeFilePath = relativePath + "/" + fileName;
            String avmFilePath = avmFileSystem.getAVMPath(relativeFilePath);

            // check whether the file exists
            IFile file = context.getModelManager().getFile(context,
                    relativePath);

            // if the file exists
            if (file != null)
            {
                QName QNAME_TYPE_AVMPLAINCONTENT = QName.createQName(TYPE_AVMPLAINCONTENT);
                QName QNAME_ASPECT_FORMINSTANCEDATA = QName.createQName(ASPECT_FORMINSTANCEDATA);
                QName QNAME_PROP_PARENTFORMNAME = QName.createQName(PROP_PARENTFORMNAME);
                QName QNAME_PROP_ORIGINALPARENTPATH = QName.createQName(PROP_ORIGINALPARENTPATH);

                // do we have the forminstance data aspect
                boolean hasAspect = avmFileSystem.getAVMRemote().hasAspect(-1,
                        avmFilePath, QNAME_ASPECT_FORMINSTANCEDATA);
                if (!hasAspect)
                {
                    // add the aspect
                    avmFileSystem.getAVMRemote().addAspect(avmFilePath,
                            QNAME_ASPECT_FORMINSTANCEDATA);

                    // add parent form property
                    PropertyValue _parentFormName = new PropertyValue(
                            DataTypeDefinition.TEXT, formName);
                    avmFileSystem.getAVMRemote().setNodeProperty(avmFilePath,
                            QNAME_PROP_PARENTFORMNAME, _parentFormName);

                    // add original parent path property
                    String originalParentPath = avmPath;
                    PropertyValue _originalParentPath = new PropertyValue(
                            DataTypeDefinition.TEXT, originalParentPath);
                    avmFileSystem.getAVMRemote().setNodeProperty(avmFilePath,
                            QNAME_PROP_ORIGINALPARENTPATH, _originalParentPath);
                }

            }
        }
        catch (Exception ex)
        {
            // this is the failsafe catch
            // log the error but continue
            ex.printStackTrace();
        }
    }
}
