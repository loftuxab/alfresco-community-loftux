package org.alfresco.enterprise.repo.content.cryptodoc;

import java.security.Key;
import java.security.KeyStoreException;
import java.util.Map;

import org.alfresco.encryption.MissingKeyException;

/**
 * This service defines a master keystore.
 * 
 * In order to support the changing of a master keystore version or
 * implementation, a method is provided to allow support for depreciated
 * implementations.  The key references retrieved from this keystore are only
 * references.  The actual key is not available for security reasons. 
 * 
 * @author sglover
 * @author Brian Long
 *
 */
public interface MasterKeystoreService
{
	/**
	 * This method retrieves the unique ID of this master keystore service.
	 * 
	 * This ID is stored alongside the key reference so that the system knows
	 * how to decrypt the data in the future.
	 * 
	 * @return A master keystore service implementation unique ID
	 */
	String getId();
	
	/**
	 * This method checks to see if the implementation supports the previously
	 * used ID.
	 * 
	 * This is used to make sure the service knows how to decrypt depreciated
	 * master keystores.
	 * 
	 * @param id A master keystore service implementation unique ID
	 * @return true if it is supported; false otherwise
	 */
	boolean supportsId(String id);
	
	/**
	 * This method retrieves the next key reference available in the master
	 * keystore service implementation.
	 * 
	 * If only one key exists or should be used in the keystore, then this will
	 * return a reference to the one key over and over again.
	 * 
	 * @return The next master key; null if keys are provided during encryption
	 */
	MasterKeyPair getNextEncryptionKey();

	/**
	 * This method retrieves the specified key (for decryption) when given the
	 * key reference.  A decryption key is typically the private key or secret
	 * key.
	 * 
	 * The alias will be searched in this keystore and other keystores where
	 * MasterKeystoreService.supportsId(java.lang.String) returns true.
	 * 
	 * @param keyref A reference to a key
	 * @return A key
	 */
	Key getDecryptionKey(KeyReference keyref) throws MissingKeyException;

	/**
	 * Revoke a master key, make it unavailable for encryption but still available for decryption.
	 * 
	 * Note: this will revoke the master key from the in-memory data structures only i.e. will
	 * not persist across reboots.
	 * 
	 * @param masterKeyRef
	 * @throws KeyStoreException
	 * @throws MissingKeyException
	 */
	void revokeMasterKey(KeyReference masterKeyRef) throws KeyStoreException, MissingKeyException;

	/**
	 * Cancel master key revocation, making it available for encryption again
	 * 
	 * @param masterKeyRef
	 * @throws KeyStoreException
	 * @throws MissingKeyException
	 */
	void cancelRevocation(KeyReference masterKeyRef) throws KeyStoreException, MissingKeyException;

	/**
	 * Get master keys meta data.
	 * 
	 * @return
	 */
	Map<String, KeyInformation> getMasterKeys();
	
	/**
	 * Re-encrypt symmetric keys encrypted with the given master key using a new master key.
	 * 
	 * @param masterKeyRef
	 * @throws MissingKeyException
	 */
	void reEncryptSymmetricKeys(KeyReference masterKeyRef) throws MissingKeyException;
}
