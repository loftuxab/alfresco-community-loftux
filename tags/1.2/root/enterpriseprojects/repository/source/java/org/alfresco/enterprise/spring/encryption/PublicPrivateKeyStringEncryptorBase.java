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
import java.io.UnsupportedEncodingException;
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

import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.jasypt.encryption.StringEncryptor;

/**
 *  Alfresco Encrypted Properties Management Tool
 *  <p>
 *  Provides a command line interface to create keystores, encrypt and decrypt properties
 */
public class PublicPrivateKeyStringEncryptorBase implements StringEncryptor 
{
	static final int KEY_SIZE = 512;
	protected String keyAlgorithm	= "RSA";
	protected String encryptionAlgorithm = "RSA/ECB/PKCS1PADDING";
	
	protected PrivateKey privateKey	= null;
	protected PublicKey publicKey	= null;
	static final String KEYNAME		="alfrescoSpringKey";
	static final String PUBKEYNAME	=KEYNAME + ".pub";
	static final String PRIKEYNAME	=KEYNAME + ".pri";
	
	static final String KEY_PACKAGE = "/alfresco/extension/enterprise";
	static final String PRIKEYPATH	= KEY_PACKAGE + "/" + PRIKEYNAME;
	static final String PUBKEYPATH	=KEY_PACKAGE +  "/" + PUBKEYNAME;

	static final String CMD_INITKEY 	= "initkey";
	static final String CMD_ENCRYPT 	= "encrypt";
	static final String CMD_VALIDATE 	= "validate";
	static final String USAGE=	"USAGE : " + PublicPrivateKeyStringEncryptor.class.getName() +  " " + CMD_INITKEY + " | " + CMD_ENCRYPT + " | " + CMD_VALIDATE + " <shared dir> [args]";
	static final String USAGE_2= " <shared dir> is where you put your alfresco-global.properties file e.g.   c:/tomcat/shared/classes";
	static final String USAGE_3= " initkey : initialise the public and private keystores";
	static final String USAGE_4= " encrypt : encrypt a value ";
	static final String USAGE_5= " validate : compare an encrypted value with a value to see if they match";

	static final String USAGE_INITKEY="USAGE : " + PublicPrivateKeyStringEncryptor.class.getName() +  " " + CMD_INITKEY + " <shared dir> ";
	static final String USAGE_ENCRYPT="USAGE : " + PublicPrivateKeyStringEncryptor.class.getName() +  " " + CMD_ENCRYPT + " <shared dir> [value to encrypt]";
	static final String USAGE_DECRYPT="USAGE : " + PublicPrivateKeyStringEncryptor.class.getName() +  " " + CMD_VALIDATE + " <shared dir> encrypted_value [value]";

	private static final int USAGE_EXIT_CODE = 255;
    private static final int ERROR_EXIT_CODE = 1;
    private static final int SUCCESS_EXIT_CODE = 0;
    
	public void setKeyAlgorithm(String algorithm) 
	{
		this.keyAlgorithm = algorithm;
	}
	
	public void setEncryptionAlgorithm(String algorithm) 
	{
	    this.encryptionAlgorithm = algorithm;
	}
	
	/**
     * Notify an information message
     * @param msg the message
     */
    void info(String msg)
    {
        System.out.println(msg);
    }
	
	/**
	 * createKeyFiles
	 * @param alfresoSharedDir
	 * @throws RuntimeException 
	 */
	public void createKeyFiles(String alfrescoSharedDir) 
	{
		File sharedDir = new File(alfrescoSharedDir);
		
		File enterpriseDir = getEnterpriseDir(alfrescoSharedDir);	
				
		boolean ret = false;
		
		if(!sharedDir.exists())
		{
			throw new RuntimeException("alfresco shared dir does not exist : " + sharedDir);
		}
		
		if(!enterpriseDir.exists())
		{
		    throw new RuntimeException("alfresco enterprise dir does not exist : " + enterpriseDir);
		}
		
		File publicKeyFile = new File(enterpriseDir, PUBKEYNAME);
		File privateKeyFile = new File(enterpriseDir, PRIKEYNAME);
		try 
		{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyAlgorithm);
		    keyGen.initialize(KEY_SIZE);
		    KeyPair key = keyGen.generateKeyPair();
		    
		    try (ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));)
		    {
		        publicKeyOS.writeObject(key.getPublic());
		        
		        info("public key created file: "+ publicKeyFile.getPath());
		    }
			catch (IOException e) 
			{
			    throw new RuntimeException("unable to create public key file", e);
			} 
		    
		    try ( ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));)
		    {
		        privateKeyOS.writeObject(key.getPrivate());  
		        
		        info("private key created file:" + privateKeyFile.getPath());
		       
		    }
		    catch (IOException e) 
			{
		        throw new RuntimeException("unable to create private key file", e);
			} 		    
		} 
		catch (NoSuchAlgorithmException e) 
		{
		    throw new RuntimeException("Unable to generate public/private key", e);
		}	
	}
	
	/**
	 * 
	 * @param alfrescoSharedDir
	 * @throws RuntimeException
	 */
	public void initPublic(String alfrescoSharedDir) 
	{
		File enterpriseDir = getEnterpriseDir(alfrescoSharedDir);		
		
		File publicKeyFile = new File(enterpriseDir, PUBKEYNAME);
	
		if (publicKeyFile.canRead()) 
		{
			try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(publicKeyFile));)
			{
				publicKey = (PublicKey) is.readObject();
			} 
			catch (ClassNotFoundException e) 
			{
				throw new RuntimeException("Could not instantiate Public Key", e);
			} 
			catch (IOException e) 
			{
				throw new RuntimeException("Could not open Public Key", e);
			}
		} 
		else 
		{
			throw new RuntimeException("Public Key File Not Found :" + publicKeyFile.getPath());
		}
	}
	
	/**
	 * @param alfrescoSharedDir
	 * @throws RuntimeException
	 */
	public void initPrivate(String alfrescoSharedDir) 
	{
		File enterpriseDir = getEnterpriseDir(alfrescoSharedDir);
		
		File privateKeyFile = new File(enterpriseDir, PRIKEYNAME);
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
				throw new RuntimeException("Could not instantiate Private Key", e);
			} 
			catch (IOException e) 
			{
				throw new RuntimeException("Could not find Private Key", e);
			}
		} 
		else 
		{
			throw new RuntimeException("Private Key File Not Found :" + privateKeyFile.getPath());
		}
	}
	
	private File getEnterpriseDir(String alfrescoSharedDir)
	{
		File sharedDir = new File(alfrescoSharedDir);	
		File alfrescoDir = new File(sharedDir, "alfresco");
		File extensionsDir = new File(alfrescoDir, "extension");
		File enterpriseDir = new File(extensionsDir, "enterprise");
		return enterpriseDir;
	}
		
	@Override
	public String encrypt(String message) 
	{
		Cipher cipher;
		byte[] cipherText = new byte[0];
		String retval = message;
		if(publicKey == null)
		{
			return retval;
		}
		
		try 
		{    
			cipher = Cipher.getInstance(encryptionAlgorithm);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
			// Number of bytes in the key - 11 for padding
			int chunkSize = (KEY_SIZE / 8) - 11;   
			
			// bytes to encrypt
			byte[] messageBytes = message.getBytes("UTF-8");
			
			if(messageBytes.length > chunkSize)
			{
			    // yes we need multiple chunks 
			    byte[] cipherChunk = new byte[0]; // chunk of encrypted stuff
			    byte[] buffer = new byte[chunkSize]; // working buffer
			
			    for (int i = 0; i < messageBytes.length; i++)
			    {
		            // if we filled our buffer array we have our block ready for encryption
		            if ((i > 0) && (i % chunkSize == 0))
		            {
		                //execute the encryption operation
		                cipherChunk = cipher.doFinal(buffer);
		                cipherText = append(cipherText, cipherChunk);
		            
		                // here we calculate the length of the next buffer required
		                int newlength = chunkSize;

		                // if newlength would be longer than remaining bytes in the bytes array we shorten it.
		                if (i + chunkSize > messageBytes.length) 
		                {
		                    newlength = messageBytes.length - i;
		                }
		                // clean the buffer array
		                buffer = new byte[newlength];
		            }
		            // copy byte into our buffer.
		            buffer[i % chunkSize] = messageBytes[i];
		        } // for each byte in message
			
			    // Any remaining bytest in buffer
		        cipherChunk = cipher.doFinal(buffer);
		        cipherText = append(cipherText, cipherChunk);
			}
			else
			{
			    // we don't need multiple chunks
                cipherText = cipher.doFinal(messageBytes);
			}

			retval = new String(Base64.encodeBase64(cipherText));
		} 
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
		catch (NoSuchPaddingException e) 
		{
			throw new RuntimeException("Could not encrypt value", e);
		} 
		catch (NoSuchAlgorithmException  e) 
		{
			throw new RuntimeException("Could not encrypt value", e);
		} 
		catch (InvalidKeyException e) 
		{
			throw new RuntimeException("Could not encrypt value", e);
		} 
		catch (IllegalBlockSizeException  e) 
		{
			throw new RuntimeException("Could not encrypt value", e);
		} 
		catch (BadPaddingException e) 
		{
			throw new RuntimeException("Could not encrypt value", e);
		}
		return retval;
	}

	@Override
	public String decrypt(String encryptedMessage) 
	{
		Cipher cipher;
		String retval = encryptedMessage;
		
		byte[] plainText = new byte[0];
		
		if (privateKey == null)
		{
			throw new RuntimeException("Unable to decrypt value,  private key not found");
		}
		try 
		{
			cipher = Cipher.getInstance(encryptionAlgorithm);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			// Chunk size is Number of bytes in the key
			int chunkSize = (KEY_SIZE / 8); 
			
		     // bytes to decrypt
			byte[] messageBytes = Base64.decodeBase64(encryptedMessage.getBytes("UTF-8"));
        
            // encrypted chunk
            byte[] cipherChunk = new byte[0];
            // Working buffer
            byte[] buffer = new byte[chunkSize];
            
            for (int i = 0; i < messageBytes.length; i++)
            {
                // if we filled our buffer array we have our block ready for encryption
                if ((i > 0) && (i % chunkSize == 0))
                {
                    //execute the operation
                    cipherChunk = cipher.doFinal(buffer);
                    // add the result to our total result.
                    plainText = append(plainText, cipherChunk);
                    
                    // here we calculate the length of the next buffer required
                    int newlength = chunkSize;

                    // if newlength would be longer than remaining bytes in the bytes array we shorten it.
                    if (i + chunkSize > messageBytes.length) 
                    {
                         newlength = messageBytes.length - i;
                    }
                    // clean the buffer array
                    buffer = new byte[newlength];
                }
                // copy byte into our buffer.
                buffer[i % chunkSize] = messageBytes[i];
            }
            
            // Any remaining buffer
            cipherChunk = cipher.doFinal(buffer);
            plainText = append(plainText, cipherChunk);

			retval =  new String(plainText, "UTF-8");
		}
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
		catch (NoSuchPaddingException e) 
		{
			throw new RuntimeException("Could not decrypt value", e);
		} 
		catch (NoSuchAlgorithmException  e) 
		{
			throw new RuntimeException("Could not decrypt value", e);
		} 
		catch (InvalidKeyException e) 
		{
			throw new RuntimeException("Could not decrypt value", e);
		} 
		catch (IllegalBlockSizeException  e) 
		{
			throw new RuntimeException("Could not decrypt value", e);
		} 
		catch ( BadPaddingException e) 
		{
			throw new RuntimeException("Could not decrypt value", e);
		}
		return retval;
	}
	
	public String getPasswordFromConsole() 
	{
		String enteredPassword=null;
		String verifyPassword=null;
		boolean firstOne=true;
		do 
		{
			if (!firstOne) 
			{
				System.console().writer().println("Please enter the same value twice to verify your encrypted value");
				System.console().writer().flush();
			}
			firstOne=false;
			System.console().writer().print("Please Enter Value: ");
			System.console().writer().flush();
			enteredPassword = new String(System.console().readPassword());
			System.console().writer().print("Please Repeat Value: ");
			System.console().writer().flush();
			verifyPassword =  new String(System.console().readPassword());
		} 
		while (enteredPassword == null || enteredPassword.length() < 1 ||  !enteredPassword.equals(verifyPassword));
		return enteredPassword;
	}
	
	/**
	 * append two byte arrays together 
	 * @param prefix
	 * @param suffix
	 * @return a byte array containing the contents of prefix + suffix
	 */
	private static byte[] append(byte[] prefix, byte[] suffix)
	{
        byte[] toReturn = new byte[prefix.length + suffix.length];
        for (int i=0; i< prefix.length; i++)
        {
            toReturn[i] = prefix[i];
        }
        for (int i=0; i< suffix.length; i++)
        {
            toReturn[i+prefix.length] = suffix[i];
        }
        return toReturn;
 }
	
	/**
	 * Main command line program
	 * 
	 * @return 0 success
	 * @return 1 error
	 * @return 255 usage failure
	 * 
	 * @param args
	 */
	public static void main(String args[]) 
	{	
		if (args.length < 1) 
		{
		    System.out.println("Alfresco Encrypted Properties Management Tool");
			System.err.println(USAGE);
			System.err.println("");
			System.err.println(USAGE_2);
			System.err.println(USAGE_3);
			System.err.println(USAGE_4);
			System.err.println(USAGE_5);
			
			System.exit(USAGE_EXIT_CODE);
		}
		PublicPrivateKeyStringEncryptorBase enc = new PublicPrivateKeyStringEncryptorBase();
		String alfrescoExtensionsDirectory=null;
		if (args.length > 1) 
		{
			alfrescoExtensionsDirectory=args[1];
			if (!args[0].equalsIgnoreCase(CMD_INITKEY)) 
			{
				enc.initPublic(alfrescoExtensionsDirectory);
				enc.initPrivate(alfrescoExtensionsDirectory);
				
				if (enc.publicKey == null || enc.privateKey == null) 
				{
					System.err.println("Please run " + CMD_INITKEY + " before encrypting or validating passwords");
					System.exit(USAGE_EXIT_CODE);
				}
			}
		}
		if (args[0].equalsIgnoreCase(CMD_INITKEY)) 
		{
			if (args.length < 2) 
			{
				System.err.println(USAGE_INITKEY);
				System.exit(USAGE_EXIT_CODE);				
			}
			try
			{
			    enc.createKeyFiles(alfrescoExtensionsDirectory);
			    
			    System.out.println("The key files have been generated, please set permissions on the private key to keep it protected.");
			    System.exit(SUCCESS_EXIT_CODE);
			    
			}
			catch (Throwable t)
			{
			    System.err.println("unable to initialise keys");
			    t.printStackTrace(System.err);
			    System.exit(ERROR_EXIT_CODE);    
			}
		} 
		else if (args[0].equalsIgnoreCase(CMD_ENCRYPT)) 
		{
			if (args.length < 2) 
			{
				System.err.println(USAGE_ENCRYPT);
				System.exit(USAGE_EXIT_CODE);			
			}
			String password = null;
			if (args.length > 2) 
			{
				password = args[2];
			} 
			else 
			{
				password = enc.getPasswordFromConsole();
			}
			
			try
			{
		        System.out.println(enc.encrypt(password));
		        System.exit(SUCCESS_EXIT_CODE);
			}
			catch(Throwable t)
			{
			    System.err.println("Error : Unable to encrypt : "+t.getMessage());
			    t.printStackTrace(System.err);
			    System.exit(ERROR_EXIT_CODE); 
			}
		} 
		else if (args[0].equalsIgnoreCase(CMD_VALIDATE)) 
		{
			if (args.length < 3) 
			{
				System.err.println(USAGE_DECRYPT);
				System.exit(USAGE_EXIT_CODE);			
			}
			String password = null;
			if (args.length > 3) 
			{
				password = args[3];
			} 
			else 
			{
				password = enc.getPasswordFromConsole();
			}
			String encryptedValue = args[2];
					
			try
			{
			   String decryptedValue = enc.decrypt(encryptedValue);
		       if(decryptedValue.equals(password))
		       {
		           System.out.println("The value and encrypted value MATCH");
		           System.exit(SUCCESS_EXIT_CODE);
		       }
		       else
		       {
		           System.out.println("The value and encrypted value DO NOT MATCH");
		           System.exit(ERROR_EXIT_CODE);
		       }
			}
			catch (Throwable t)
			{
			    System.err.println("Error : Unable to validate :" + t.getMessage());
			    t.printStackTrace(System.err);
			    System.exit(ERROR_EXIT_CODE); 
			}
		} 
		else 
		{
			System.err.println(USAGE);
			System.err.println("");
			System.err.println(USAGE_2);
			System.err.println(USAGE_3);
			System.err.println(USAGE_4);
			System.err.println(USAGE_5);
			System.err.println("");
			System.err.println("BAD COMMAND: "+ args[0]);
			System.exit(USAGE_EXIT_CODE);
		}
		
		// Don't expect to get here
		System.exit(ERROR_EXIT_CODE);
	}
}

