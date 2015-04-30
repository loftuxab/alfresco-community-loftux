/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.spring.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.encryption.StringEncryptor;

/**
 * Alfresco Public Spring Private Property Value Encryptor
 * <p>
 * Class that uses a private key to encrypt a string value for use in the alfresco spring context files.
 * <p>
 * Alfresco Implementation of org.jasypt.encryption.StringEncryptor which is used by the 
 * org.jasypt.spring3.properties.EncryptablePropertyPlaceholderConfigurer bean 
 * alfresco.jasypt.encryptedPropertyConfigurer
 * 
 * @see org.jasypt.spring3.properties.EncryptablePropertyPlaceholderConfigurer
 * 
 */
public class PublicPrivateKeyStringEncryptor extends PublicPrivateKeyStringEncryptorBase 
implements org.jasypt.encryption.StringEncryptor
{
    static Log log = LogFactory.getLog(PublicPrivateKeyStringEncryptor.class);
    
    @Override
    void info(String msg)
    {
        if(log.isInfoEnabled())
        {
            log.info(msg);
        }
    }
   	
	/**
	 * init spring public private properties
	 */
	public void init() 
	{
		/**
		 *  Read the private key file off the classpath and set privateKey in superclass
		 */
		URL privateKeyURL = this.getClass().getResource(PRIKEYPATH);
		if (privateKeyURL==null) 
		{
			log.warn("Private Key File: " + PRIKEYPATH + " Does not exist");
			return;
		}
		File privateKeyFile;
		try 
		{
			privateKeyFile = new File(privateKeyURL.toURI());
			if (privateKeyFile.canRead()) 
			{
				ObjectInputStream is;
				try 
				{
					is = new ObjectInputStream(new FileInputStream(privateKeyFile));
					privateKey = (PrivateKey) is.readObject();
				} 
				catch (ClassNotFoundException e) 
				{
				    // TODO MER - should we be throwing here?
				    log.error("Could not instantiate Private Key", e);
				} 
				catch (IOException e) 
				{
					log.error("Could not instantiate Private Key", e);
				}
			} 
			else 
			{
				log.error("Private Key File: " + privateKeyFile.getAbsolutePath() + " Cannot be read");
			}		
		} 
		catch (URISyntaxException e) 
		{
			log.error("Could not instantiate Private Key", e);
		}
	}	
}
