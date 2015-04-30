/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport;

import java.io.File;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.filestore.FileContentReader;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Data Bean for cloud sync content
 * 
 * @author mrogers
 */
public interface CloudSyncContent 
{
	
	public QName getPropName();
	
	public String getMimetype();

	public String getEncoding();
	
    /**
     * Does the content exists
     * @return true if the content exists.
     */
	public boolean exists();
	
	/**
	 * Open a content reader for this cloud sync content
	 */
	public ContentReader openReader();
}
