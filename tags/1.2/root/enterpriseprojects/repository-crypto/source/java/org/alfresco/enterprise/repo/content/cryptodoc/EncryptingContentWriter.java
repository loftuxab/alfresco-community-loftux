/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc;

import org.alfresco.service.cmr.repository.ContentWriter;

public interface EncryptingContentWriter extends ContentWriter
{
	
	long getTotalBytesBeforeEncrypt();
	long getTotalBytesAfterEncrypt();

}
