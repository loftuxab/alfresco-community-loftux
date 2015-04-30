package org.alfresco.enterprise.repo.content.cryptodoc;

import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

/**
 * This service defines key generation principals.
 * 
 * @author Brian Long
 */
public interface KeyGenerationService
{

	/**
	 * This method retrieves the default symmetric algorithm.
	 * 
	 * @return The name of an algorithm (e.g. AES or DESede)
	 */
	String getDefaultSymmetricAlgorithm();
	
	/**
	 * This method retrieves the default key size of the default symmetric
	 * algorithm.
	 * 
	 * @return The bit length of the key (e.g. 128 or 162)
	 */
	int getDefaultSymmetricKeySize();
	
	/**
	 * This method generates a symmetric key using the default symmetric
	 * algorithm and key size.
	 * 
	 * @return A secret key
	 */
	SecretKey generateSymmetricKey();
	
	/**
	 * This method generates a symmetric key using the specified algorithm
	 * and key size.
	 * 
	 * @param algorithm The name of an algorithm
	 * @param keySize The bit length of the key to be generated
	 * @return A secret key
	 * @throws NoSuchAlgorithmException The specified algorithm does not exist or is not supported
	 */
	SecretKey generateSymmetricKey(String algorithm, int keySize) throws NoSuchAlgorithmException;

}
