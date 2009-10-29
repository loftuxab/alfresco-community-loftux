/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
package org.alfresco.abdera.ext.cmis;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;


/**
 * CMIS Version: 0.61
 *
 * CMIS Object for the Abdera ATOM library.
 * 
 * @author davidc
 */
public class CMISObject extends ExtensibleElementWrapper
{
    /**
     * @param internal
     */
    public CMISObject(Element internal)
    {
        super(internal);
    }

    /**
     * @param factory
     */
    public CMISObject(Factory factory)
    {
        super(factory, CMISConstants.OBJECT);
    }

    /**
     * Gets all Properties for this CMIS Object
     * 
     * @return  properties
     */
    public CMISProperties getProperties()
    {
        Element child = getFirstChild(CMISConstants.PROPERTIES);
        if (child == null)
        {
            child = addExtension(CMISConstants.PROPERTIES); 
        }
        return (CMISProperties)child;
    }

    /**
     * Gets all Allowable Actions for this CMIS Object
     * 
     * @return  allowable actions
     */
    public CMISAllowableActions getAllowableActions()
    {
        Element child = getFirstChild(CMISConstants.ALLOWABLE_ACTIONS);
        if (child == null)
        {
            child = addExtension(CMISConstants.ALLOWABLE_ACTIONS); 
        }
        return (CMISAllowableActions)child;
    }

    /**
     * Gets name
     * 
     * @return  name property
     */
    public CMISProperty getName()
    {
        return getProperties().find(CMISConstants.PROP_NAME);
    }

    /**
     * Gets id
     * 
     * @return  id property 
     */
    public CMISProperty getObjectId()
    {
        return getProperties().find(CMISConstants.PROP_OBJECT_ID);
    }

    /**
     * Gets base type
     * 
     * @return  base type property
     */
    public CMISProperty getBaseTypeId()
    {
        return getProperties().find(CMISConstants.PROP_BASE_TYPE_ID);
    }

    /**
     * Gets object type
     * 
     * @return  object type property
     */
    public CMISProperty getObjectTypeId()
    {
        return getProperties().find(CMISConstants.PROP_OBJECT_TYPE_ID);
    }

    /**
     * Created By
     * 
     * @return  created by property
     */
    public CMISProperty getCreatedBy()
    {
        return getProperties().find(CMISConstants.PROP_CREATED_BY);
    }

    /**
     * Creation Date
     * 
     * @return  creation date property
     */
    public CMISProperty getCreationDate()
    {
        return getProperties().find(CMISConstants.PROP_CREATION_DATE);
    }

    /**
     * Last Modified By
     * 
     * @return  last modified by property
     */
    public CMISProperty getLastModifiedBy()
    {
        return getProperties().find(CMISConstants.PROP_LAST_MODIFIED_BY);
    }

    /**
     * Last Modified Date
     * 
     * @return  last modified date property
     */
    public CMISProperty getLastModificationDate()
    {
        return getProperties().find(CMISConstants.PROP_LAST_MODIFICATION_DATE);
    }

    /**
     * Is immutable?
     * 
     * @return  isImmutable property
     */
    public CMISProperty isImmutable()
    {
        return getProperties().find(CMISConstants.PROP_IS_IMMUTABLE);
    }

    /**
     * Gets Latest Version
     * 
     * @return  latest version property
     */
    public CMISProperty isLatestVersion()
    {
        return getProperties().find(CMISConstants.PROP_IS_LATEST_VERSION);
    }

    /**
     * Is Major Version?
     * 
     * @return  is major version property
     */
    public CMISProperty isMajorVersion()
    {
        return getProperties().find(CMISConstants.PROP_IS_MAJOR_VERSION);
    }

    /**
     * Is Latest Major Version?
     * 
     * @return  is latest major version property
     */
    public CMISProperty isLatestMajorVersion()
    {
        return getProperties().find(CMISConstants.PROP_IS_LATEST_MAJOR_VERSION);
    }

    /**
     * Version label
     * 
     * @return  version label property
     */
    public CMISProperty getVersionLabel()
    {
        return getProperties().find(CMISConstants.PROP_VERSION_LABEL);
    }

    /**
     * Version series id
     * 
     * @return  version series id property
     */
    public CMISProperty getVersionSeriesId()
    {
        return getProperties().find(CMISConstants.PROP_VERSION_SERIES_ID);
    }

    /**
     * Version Series Checked Out
     * 
     * @return  version series checked out property
     */
    public CMISProperty isVersionSeriesCheckedOut()
    {
        return getProperties().find(CMISConstants.PROP_IS_VERSION_SERIES_CHECKED_OUT);
    }
    
    /**
     * Version Series Checked Out By
     * 
     * @return  version series checked out by property
     */
    public CMISProperty getVersionSeriesCheckedOutBy()
    {
        return getProperties().find(CMISConstants.PROP_VERSION_SERIES_CHECKED_OUT_BY);
    }

    /**
     * Version Series Checked Out Id
     * 
     * @return  version series checked out id property
     */
    public CMISProperty getVersionSeriesCheckedOutId()
    {
        return getProperties().find(CMISConstants.PROP_VERSION_SERIES_CHECKED_OUT_ID);
    }

    /**
     * Checkin Comment
     * 
     * @return  checkin comment property
     */
    public CMISProperty getCheckinComment()
    {
        return getProperties().find(CMISConstants.PROP_CHECKIN_COMMENT);
    }
    
    /**
     * Content Stream Length
     * 
     * @return  content stream length property
     */
    public CMISProperty getContentStreamLength()
    {
        return getProperties().find(CMISConstants.PROP_CONTENT_STREAM_LENGTH);
    }

    /**
     * Content Stream Mimetype
     * 
     * @return  content stream mimetype property
     */
    public CMISProperty getContentStreamMimeType()
    {
        return getProperties().find(CMISConstants.PROP_CONTENT_STREAM_MIMETYPE);
    }

    /**
     * Content Stream Filename
     * 
     * @return  content stream filename property
     */
    public CMISProperty getContentStreamFilename()
    {
        return getProperties().find(CMISConstants.PROP_CONTENT_STREAM_FILENAME);
    }

    /**
     * Content Stream Id
     * 
     * @return  content stream id property
     */
    public CMISProperty getContentStreamId()
    {
        return getProperties().find(CMISConstants.PROP_CONTENT_STREAM_ID);
    }
    
    /**
     * Source Id (Relationship)
     * 
     * @return  source id property
     */
    public CMISProperty getSourceId()
    {
        return getProperties().find(CMISConstants.PROP_SOURCE_ID);
    }
    
    /**
     * Target Id (Relationship)
     * 
     * @return  target id property
     */
    public CMISProperty getTargetId()
    {
        return getProperties().find(CMISConstants.PROP_TARGET_ID);
    }
}
