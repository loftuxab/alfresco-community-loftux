package org.alfresco.enterprise.repo.content.cryptodoc;

import java.util.HashMap;
import java.util.Map;

import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

/**
 * 
 * @author sglover
 *
 */
public class KeyInformation
{
	public static String[] attributeKeys = {"Key Alias", "Encryption Key Algorithm", "Decryption Key Algorithm",
		"Can Encrypt", "Can Decrypt", "Number of Symmetric Keys"};
	public static String[] attributeDescriptions = {"Key Alias", "Encryption Key Algorithm", "Decryption Key Algorithm",
		"Can Encrypt", "Can Decrypt", "Number of Symmetric Keys"};
	public static OpenType<?>[] attributeTypes = {SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.BOOLEAN,
		SimpleType.BOOLEAN, SimpleType.LONG};

	private String alias;
	private String encryptionKeyAlgorithm;
	private String decryptionKeyAlgorithm;
	private boolean canEncrypt;
	private boolean canDecrypt;
	private long numSymmetricKeys;

	public KeyInformation(String alias, String encryptionKeyAlgorithm, String decryptionKeyAlgorithm,
			boolean canEncrypt, boolean canDecrypt, long numSymmetricKeys)
	{
		this.alias = alias;
		this.encryptionKeyAlgorithm = encryptionKeyAlgorithm;
		this.decryptionKeyAlgorithm = decryptionKeyAlgorithm;
		this.canEncrypt = canEncrypt;
		this.canDecrypt = canDecrypt;
		this.numSymmetricKeys = numSymmetricKeys;
	}

	public String getAlias()
	{
		return alias;
	}

	public String getEncryptionKeyAlgorithm()
	{
		return encryptionKeyAlgorithm;
	}

	public String getDecryptionKeyAlgorithm()
	{
		return decryptionKeyAlgorithm;
	}

	public long getNumSymmetricKeys()
	{
		return numSymmetricKeys;
	}

	public boolean canEncrypt()
	{
		return canEncrypt;
	}

	public boolean canDecrypt()
	{
		return canDecrypt;
	}

	public Map<String, Object> getValues()
	{
		Map<String, Object> values = new HashMap<String, Object>();
		for(String title : attributeKeys)
		{
			Object value = null;
			if(title.equals("Key Alias"))
			{
				value = alias;
			}
			else if(title.equals("Encryption Key Algorithm"))
			{
				value = encryptionKeyAlgorithm;
			}
			else if(title.equals("Decryption Key Algorithm"))
			{
				value = decryptionKeyAlgorithm;
			}
			else if(title.equals("Can Encrypt"))
			{
				value = canEncrypt;
			}
			else if(title.equals("Can Decrypt"))
			{
				value = canDecrypt;
			}
			else if(title.equals("Number of Symmetric Keys"))
			{
				value = numSymmetricKeys;
			}
			else
			{
				throw new IllegalArgumentException();
			}
			values.put(title, value);
		}

		return values;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyInformation other = (KeyInformation) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		return true;
	}
}
