/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport.impl;

import org.alfresco.enterprise.repo.sync.transport.CloudSyncContent;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public class CloudSyncContentNodeImpl implements CloudSyncContent 
{
	QName propName;
	String mimetype;
	String encoding;
	NodeRef nodeRef;
	ContentService contentService;
	
	/**
	 * 
	 * @param propName property name
	 * @param mimetype mimetype
	 * @param encoding encoding
	 * @param file file
	 */
	public CloudSyncContentNodeImpl(QName propName, NodeRef nodeRef, ContentService contentService)
	{
		this.propName = propName;
		this.nodeRef = nodeRef;
		this.contentService = contentService;
	}
	
	public QName getPropName()
	{
		return propName;
	}
	
	public String getMimetype()
	{
		return mimetype;
	}

	public String getEncoding()
	{
		return encoding;
	}
	
    /**
     * Does the content exists
     * @return true if the content exists.
     */
	public boolean exists()
	{
	    ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
		if (reader != null)
		{
			return reader.exists();
		}
		// reader is null 
		return false;
	}
	
	/**
	 * Open a content reader for this cloud sync content
	 */
	public ContentReader openReader()
	{
		return contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
	}
	
	public String toString()
	{
		return "CloudSyncContent : propName," + propName + ", mimetype," + mimetype + ", encoding," +encoding;
	}

}
