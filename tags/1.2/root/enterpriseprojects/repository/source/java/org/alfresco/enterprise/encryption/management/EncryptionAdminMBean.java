/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.enterprise.encryption.management;

import org.alfresco.encryption.MissingKeyException;

/**
 * MBean exposing repository encryption admin functionality.
 * 
 * @since 4.0
 *
 */
public interface EncryptionAdminMBean
{
	/**
	 * Change the key store used by the repository, triggering a re-encryption of encrypted properties.
	 * 
	 * Note: it is the responsibility of the end user to ensure that the keystore identified by these parameters is
	 * placed in the repository keystore directory and the keystore properties are overridden alfresco-global.properties.
	 * This can be done while the repository is running and it will be picked up automatically the next time the repository restarts.
	 *
	 * @param keyStoreLocation The new keystore passwords file location
	 * @param keyStoreMetadataFileLocation
	 * @param keyStoreType The new key store type
	 * @return
	 */
	public int reEncrypt() throws MissingKeyException;
}
