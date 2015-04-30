/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport.impl;

import java.io.File;

import org.alfresco.enterprise.repo.sync.transport.CloudSyncContent;
import org.alfresco.repo.content.filestore.FileContentReader;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.namespace.QName;

public class CloudSyncContentFileImpl implements CloudSyncContent
{
	QName propName;
	String mimetype;
	String encoding;
	File file;
	
	/**
	 * 
	 * @param propName property name
	 * @param mimetype mimetype
	 * @param encoding encoding
	 * @param file file
	 */
	public CloudSyncContentFileImpl(QName propName, String mimetype, String encoding, File file)
	{
		this.propName = propName;
		this.mimetype = mimetype;
		this.encoding = encoding;
		this.file = file;
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
        return file.exists();
	}
	
	/**
	 * Open a content reader for this cloud sync content
	 */
	public ContentReader openReader()
	{
        FileContentReader reader = new FileContentReader(file);
  
        // Encoding set
        reader.setEncoding(encoding);
        reader.setMimetype(mimetype);
        return reader;
	}
	
	public String toString()
	{
		return "CloudSyncContentFileImpl : propName," + propName + ", mimetype," + mimetype + ", encoding," +encoding;
	}
}
