/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc;

import java.io.Serializable;
import java.security.Key;

/**
 * Represents a master key pair, include whether they can be used for encryption/decryption.
 * 
 * @author sglover
 *
 */
public class MasterKeyPair implements Serializable
{
    private static final long serialVersionUID = -2467103984250949811L;

    private final String alias;
    private final String password;
    private final Key encryptionKey;
    private final Key decryptionKey;

    /*
     * Copy constructor
     */
    public MasterKeyPair(MasterKeyPair pair)
    {
        super();
        this.alias = pair.getAlias();
        this.password = pair.getPassword();
        this.encryptionKey = pair.getEncryptionKey();
        this.decryptionKey = pair.getDecryptionKey();
    }

    public MasterKeyPair(String alias, String password, Key encryptionKey, Key decryptionKey)
    {
        super();
        this.alias = alias;
        this.password = password;
        this.encryptionKey = encryptionKey;
        this.decryptionKey = decryptionKey;
    }

    public String getAlias()
    {
        return alias;
    }

    public String getPassword()
    {
        return password;
    }

    public Key getEncryptionKey()
    {
        return encryptionKey;
    }

    public Key getDecryptionKey()
    {
        return decryptionKey;
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
        MasterKeyPair other = (MasterKeyPair) obj;
        if (alias == null) {
            if (other.alias != null)
                return false;
        } else if (!alias.equals(other.alias))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "MasterKeyPair [alias=" + alias
                + ", password=" + password
                + ", encryptionKey=" + encryptionKey
                + ", decryptionKey=" + decryptionKey
                + "]";
    }
}
