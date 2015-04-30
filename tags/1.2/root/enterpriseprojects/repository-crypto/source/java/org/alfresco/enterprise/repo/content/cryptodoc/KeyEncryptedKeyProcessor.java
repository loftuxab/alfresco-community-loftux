/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc;

import java.io.IOException;
import java.security.Key;

import org.alfresco.encryption.MissingKeyException;
import org.alfresco.repo.domain.contentdata.ContentUrlKeyEntity;
import org.alfresco.repo.domain.contentdata.EncryptedKey;
import org.apache.commons.codec.DecoderException;

/**
 * 
 * @author sglover
 *
 */
public interface KeyEncryptedKeyProcessor
{
	EncryptedKey encryptSymmetricKey(Key key) throws IOException;
	Key decryptSymmetricKey(final EncryptedKey ekey) throws IOException, MissingKeyException;
	Key decryptSymmetricKey(final EncryptedKey ekey, final Key masterDecryptionKey)
			throws IOException, MissingKeyException;
	boolean reencryptSymmetricKey(ContentUrlKeyEntity contentUrlKey) throws IOException, MissingKeyException, DecoderException;
}
