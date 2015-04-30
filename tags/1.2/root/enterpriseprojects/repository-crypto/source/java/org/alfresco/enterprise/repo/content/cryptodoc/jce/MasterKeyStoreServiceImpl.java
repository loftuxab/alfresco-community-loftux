/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc.jce;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.alfresco.encryption.MissingKeyException;
import org.alfresco.enterprise.repo.content.cryptodoc.CryptoException;
import org.alfresco.enterprise.repo.content.cryptodoc.KeyGenerationService;
import org.alfresco.enterprise.repo.content.cryptodoc.KeyInformation;
import org.alfresco.enterprise.repo.content.cryptodoc.KeyReference;
import org.alfresco.enterprise.repo.content.cryptodoc.MasterKeyPair;
import org.alfresco.enterprise.repo.content.cryptodoc.MasterKeystoreService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.domain.contentdata.ContentDataDAO;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.transaction.TransactionServiceImpl;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Brian Long
 * @author sglover
 *
 */
public class MasterKeyStoreServiceImpl implements MasterKeystoreService
{
    private static Log logger = LogFactory.getLog(MasterKeyStoreServiceImpl.class);

    private final QName vetoName = QName.createQName(NamespaceService.APP_MODEL_1_0_URI, "MasterKeyStore");

    private String keystoreType;
    private File keystoreFile;
    private String keystorePassword;
    private Set<KeyReference> keyrefs;

    /*
     * TODO we could probably get away with just storing the keys in a local data structure
     * because they are immutable. For now, let's leave this as a clustered cache.
     */
    private SimpleCache<String, MasterKeyPair> masterKeyCache;
    private SimpleCache<String, MasterKeyPair> encryptingMasterKeyCache;

    private Random random;

    private KeyGenerationService keyGenerationService;
    private ContentDataDAO contentDataDAO;
    private TransactionServiceImpl transactionService;

    private String defaultSymmetricAlgorithm = "AES";
    private int defaultSymmetricKeySize = 256;

    private String[] keyAliases;
    private String[] keyPasswords;

    private String providerName;
    private Provider provider;
    private String keyStorePath;
    private KeyStore keystore;

    private KeyStoreLoader keyStoreLoader;

    private SymmetricKeyReencryptor symmetricKeyReencryptor;

    public void setTransactionService(TransactionServiceImpl transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setMasterKeyCache(SimpleCache<String, MasterKeyPair> masterKeyCache)
    {
        this.masterKeyCache = masterKeyCache;
    }

    public void setEncryptingMasterKeyCache(SimpleCache<String, MasterKeyPair> encryptingMasterKeyCache)
    {
        this.encryptingMasterKeyCache = encryptingMasterKeyCache;
    }

    public void setSymmetricKeyReencryptor(SymmetricKeyReencryptor symmetricKeyReencryptor)
    {
        this.symmetricKeyReencryptor = symmetricKeyReencryptor;
    }

    public void setProviderName(String providerName) 
    {
        this.providerName = StringUtils.trimToNull(providerName);
    }

    protected String getProviderName()
    {
        return this.providerName;
    }

    public void setKeyGenerationService(KeyGenerationService keyGenerationService)
    {
        this.keyGenerationService = keyGenerationService;
    }

    public void setContentDataDAO(ContentDataDAO contentDataDAO)
    {
        this.contentDataDAO = contentDataDAO;
    }

    public void setDefaultSymmetricAlgorithm(String defaultSymmetricAlgorithm)
    {
        this.defaultSymmetricAlgorithm = StringUtils.trimToNull(defaultSymmetricAlgorithm);
    }

    public void setDefaultSymmetricKeySize(int defaultSymmetricKeySize)
    {
        this.defaultSymmetricKeySize = defaultSymmetricKeySize;
    }

    public int getDefaultSymmetricKeySize()
    {
        return this.defaultSymmetricKeySize;
    }

    public void setKeyStoreLoader(KeyStoreLoader keyStoreLoader)
    {
        this.keyStoreLoader = keyStoreLoader;
    }

    public void setKeystoreType(String keystoreType)
    {
        this.keystoreType = StringUtils.trimToNull(keystoreType);
    }

    protected String getKeystoreType()
    {
        return keystoreType;
    }

    public void setKeystorePath(String keyStorePath)
    {
        this.keyStorePath = keyStorePath;
    }

    protected String getKeystorePath()
    {
        return this.keystoreFile == null ? null : this.keystoreFile.getAbsolutePath();
    }

    protected File getKeystoreFile()
    {
        return this.keystoreFile;
    }

    public void setKeystorePassword(String keystorePassword) 
    {
        this.keystorePassword = StringUtils.trimToNull(keystorePassword);
    }

    public void setKeyAliases(String keyAliasesUnparsed) throws KeyStoreException
    {
        this.keyAliases = StringUtils.split(keyAliasesUnparsed, ",");
    }

    public void setKeyPasswords(String keyPasswordsUnparsed)
    {
        this.keyPasswords = StringUtils.split(keyPasswordsUnparsed, ",");
    }

    private void cancelRevocation(final String masterKeyAlias) throws KeyStoreException, MissingKeyException
    {
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                // add master key to encrypting master key cache so that it can be used for encryption
                MasterKeyPair pair = masterKeyCache.get(masterKeyAlias);
                if(pair != null)
                {
                    MasterKeyPair newPair = new MasterKeyPair(pair);
                    encryptingMasterKeyCache.put(masterKeyAlias, newPair);
                }

                return null;
            }
        }, false, true);
    }

    private void makeReadOnly()
    {
        if(this.transactionService.getAllowWrite())
        {
            this.transactionService.setAllowWrite(false, vetoName);
            logger.warn("Encrypted content store making the repository read only");
        }
    }

    private void makeReadWrite()
    {
        if(!this.transactionService.getAllowWrite())
        {
            this.transactionService.setAllowWrite(true, vetoName);
            logger.info("Encrypted content store making the repository read/write");
        }
    }

    /**
     * validate 
     * 
     * @throws IllegalArgumentException
     */
    private void validate() 
    {
        if (this.keystoreFile != null && !this.keystoreFile.exists())
        {
            throw new IllegalArgumentException("The keystore path '" + this.keystoreFile.getAbsolutePath() + "' does not exist, but must exist");
        }

        if (this.keyrefs != null)
        {
            for (KeyReference keyref : this.keyrefs)
            {
                if (keyref.getAlias() == null)
                {
                    throw new IllegalArgumentException("All master keys must have an alias; maybe there are more master key passwords than aliases?");
                }
            }
        }

        // test generate a sync key to see if it works properly
        try
        {
            keyGenerationService.generateSymmetricKey(this.defaultSymmetricAlgorithm, this.defaultSymmetricKeySize);
        } 
        catch (NoSuchAlgorithmException nsae) 
        {
            throw new IllegalArgumentException("The algorithm/keysize '"
                    + this.defaultSymmetricAlgorithm + "/" + this.defaultSymmetricKeySize + "' is not supported");
        }
    }

    public void init() throws KeyStoreException
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("init");
        }
        
        this.random = new Random(System.currentTimeMillis());

        try
        {
            this.validate();
            this.initProvider();
            this.initKeystore();
            this.initKeys();

            makeReadWrite();
        }
        catch (RuntimeException e)
        {
            logger.warn("Unexpected exception initialising the master key service: content"
                    + " cannot be read or written until this is fixed", e);
            makeReadOnly();
        }
        catch (GeneralSecurityException | IOException e)
        {
            logger.warn("Unexpected exception initialising the master key service: content"
                    + " cannot be read or written until this is fixed", e);
            makeReadOnly();
        }
    }

    private void initProvider()
    {
        if (this.providerName == null)
        {
            return;
        }

        this.provider = Security.getProvider(this.providerName);
        if (this.provider != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Found '" + this.providerName + "' provider: " + this.provider.getClass().getName());
            }
        }
        else
        {
            logger.error("Could not find provider: " + this.providerName);

            List<String> providerNames = new LinkedList<String>();
            for (Provider provider : Security.getProviders())
            {
                providerNames.add(provider.getName());
            }
            if (logger.isInfoEnabled())
            {
                logger.info("Available providers: " + providerNames);
            }
            throw new IllegalArgumentException("The '" + providerName + "' provider does not exist; valid providers: " + providerNames);
        }
    }

    private boolean canEncrypt(String masterKeyAlias)
    {
        boolean canEncrypt = encryptingMasterKeyCache.contains(masterKeyAlias);
        return canEncrypt;
    }

    private <T> List<T> enumeration2list(Enumeration<T> evalues)
    {
        List<T> lvalues = new LinkedList<T>();
        while (evalues.hasMoreElements())
            lvalues.add(evalues.nextElement());
        return lvalues;
    }

    private void initKeystore() throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException
    {
        if(keystoreType != null && keyStorePath != null && keyStorePath.length() > 0)
        {
            this.keystore = this.provider == null
                    ? KeyStore.getInstance(this.keystoreType)
                            : KeyStore.getInstance(this.keystoreType, this.provider);
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Using Keystore Type: " + this.keystore.getType());
                    }

                    char[] keystorePassword = this.keystorePassword == null ? new char[0] : this.keystorePassword.toCharArray();

                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Using Keystore File: " + this.keystoreFile);
                    }

                    if(!this.keyStoreLoader.loadKeyStore(keystore, keyStorePath, keystorePassword))
                    {
                        throw new RuntimeException("Unable to initialise encrypted content store, keystore not found " + keyStorePath);
                    }

                    if (logger.isDebugEnabled())
                    {
                        Enumeration<String> keyAliasesEnum = this.keystore.aliases();
                        List<String> keyAliasesList = enumeration2list(keyAliasesEnum);
                        logger.debug("Using keystore with aliases: " + keyAliasesList);
                    }
        }
    }

    private Key getDecryptionKey(KeyStore keystore, KeyReference keyref) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException
    {
        char[] keyPassword = keyref.getPassword() == null ? new char[0] : keyref.getPassword().toCharArray();
        return keystore.getKey(keyref.getAlias(), keyPassword);
    }

    private Key getEncryptionKey(KeyStore keystore, KeyReference keyref) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException
    {
        Certificate certificate = keystore.getCertificate(keyref.getAlias());
        if (certificate != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("The '" + keyref.getAlias() + "' alias references a certificate: " + certificate);
            }
            return certificate.getPublicKey();
        }
        else
        {
            char[] keyPassword = keyref.getPassword() == null ? new char[0] : keyref.getPassword().toCharArray();
            Key key = keystore.getKey(keyref.getAlias(), keyPassword);
            if (logger.isDebugEnabled())
            {
                logger.debug("The '" + keyref.getAlias() + "' alias references a symmetric key: " + key.getAlgorithm());
            }
            return key;
        }
    }

    private int initKeys()
    {
        return initKeysImpl();
    }

    private int initKeysImpl()
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("initKeysImpl");
        }
        
        if(keyAliases.length != keyPasswords.length)
        {
            throw new RuntimeException("Number of aliases and passwords must match");
        }

        if (this.keyrefs == null)
        {
            this.keyrefs = new HashSet<KeyReference>(keyAliases.length);
        }

        // we need to make sure each master key that has outstanding encrypted symmetric keys
        // is still on the alias list. Get symmetric key counts per master key.
        Map<String, Integer> counts = contentDataDAO.countSymmetricKeysForMasterKeys();
        
        if(logger.isDebugEnabled())
        {
            logger.debug("expecting " + counts + " symmetric keys");
        }

        for (int i = 0; i < keyAliases.length; i++)
        {
            String keyAlias = keyAliases[i];

            // we can remove from the symmetric key counts map those aliases that
            // are still present
            counts.remove(keyAlias);

            KeyReference key = new KeyReference();
            key.setAlias(keyAlias);
            key.setPassword(keyPasswords[i]);
            keyrefs.add(key);
        }

        if(counts.size() > 0)
        {
            // any remaining master keys in the count map have been removed from the aliases list
            // but are still being used
            throw new RuntimeException("Cannot start encrypted content subsystem: cannot remove master keys "
                    + counts.keySet()
                    + " symmetric keys still use them");
        }

        // cache manipulation, make sure we're in a txn
        int numKeys = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Integer>()
        {
            @Override
            public Integer execute() throws Throwable
            {
                // remove any cached keys that have been removed from the aliases list
                Collection<String> keysToRemove = new HashSet<>(masterKeyCache.getKeys());
                Set<String> aliasesSet = new HashSet<>(Arrays.asList(keyAliases));
                keysToRemove.removeAll(aliasesSet);
                for(String keyAlias : keysToRemove)
                {
                    encryptingMasterKeyCache.remove(keyAlias);
                    if(logger.isDebugEnabled())
                    {
                        logger.debug("remove key from master key cache" + keyAlias);
                    }
                    masterKeyCache.remove(keyAlias);
                }

                // load in keys from keystore
                for (KeyReference keyref : MasterKeyStoreServiceImpl.this.keyrefs)
                {
                    try
                    {
                        if (!keystore.isKeyEntry(keyref.getAlias()))
                        {
                            Enumeration<String> keyAliasesEnum = keystore.aliases();
                            List<String> keyAliasesList = enumeration2list(keyAliasesEnum);
                            logger.warn("The '" + keyref.getAlias() + "' alias does not exist in the keystore; valid aliases: " + keyAliasesList);
                            masterKeyCache.remove(keyref.getAlias());
                            encryptingMasterKeyCache.remove(keyref.getAlias());
                        }
                        else
                        {
                            Key encryptionKey = getEncryptionKey(MasterKeyStoreServiceImpl.this.keystore, keyref);
                            Key decryptionKey = getDecryptionKey(MasterKeyStoreServiceImpl.this.keystore, keyref);
                            MasterKeyPair masterKeyPair = new MasterKeyPair(keyref.getAlias(), keyref.getPassword(),
                                    encryptionKey, decryptionKey);
                            if(logger.isDebugEnabled())
                            {
                                logger.debug("add key to master key cache" + keyref.getAlias());
                            }
                            masterKeyCache.put(keyref.getAlias(), masterKeyPair);
                            MasterKeyPair masterKeyPair1 = new MasterKeyPair(keyref.getAlias(), keyref.getPassword(),
                                    encryptionKey, decryptionKey);
                            encryptingMasterKeyCache.put(keyref.getAlias(), masterKeyPair1);
                        }
                    }
                    catch (NoSuchAlgorithmException nsae)
                    {
                        throw new RuntimeException("The keystore's key algorithm is not supported: " + nsae.getMessage(), nsae);
                    }
                    catch (UnrecoverableKeyException uke)
                    {
                        throw new RuntimeException("The key password is not valid for key : "
                                + keyref.getAlias()
                                + uke.getMessage(), uke);
                    }
                    catch (KeyStoreException e)
                    {
                        throw new RuntimeException("Exception reading the keystore for key alias : "
                                + keyref.getAlias()
                                + e.getMessage(), e);
                    }
                }

                return masterKeyCache.getKeys().size();
            }
        }, false, true);

        if(masterKeyCache.getKeys().size() == 0)
        {
            throw new RuntimeException("No master keys loaded from " + keyStorePath);
        }

        return numKeys;
    }

    public Provider getProvider()
    {
        return this.provider;
    }

    @Override
    public String getId()
    {
        return this.provider == null ? "jce" : "jce-" + this.provider.getName();
    }

    @Override
    public boolean supportsId(String id)
    {
        if (id.startsWith("jce-"))
        {
            String providerName = id.substring(4);
            if (this.provider != null)
            {
                return this.provider.getName().equals(providerName);
            }
            else
            {
                Provider provider = Security.getProvider(providerName);
                return provider != null;
            }
        }
        else if (id.equals("jce"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public MasterKeyPair getNextEncryptionKey()
    {
        MasterKeyPair masterKeyPair = null;

        // TODO this is not particularly nice. Find a better way to select a random
        // value from an Alfresco cache
        Collection<String> keys = encryptingMasterKeyCache.getKeys();
        if(keys != null && keys.size() > 0)
        {
            int idx = random.nextInt(keys.size());
            Iterator<String> it = keys.iterator();
            String key = null;
            do
            {
                if(it.hasNext())
                {
                    key = it.next();
                }
            }
            while(it.hasNext() && idx-- > 0);

            if(key == null)
            {
                throw new AlfrescoRuntimeException("Unexpected null master key");
            }
    
            masterKeyPair = encryptingMasterKeyCache.get(key);
        }

        return masterKeyPair;
    }

    @Override
    public Key getDecryptionKey(KeyReference keyRef) throws MissingKeyException
    {
        Key decryptionKey = null;

        MasterKeyPair masterKeyPair = masterKeyCache.get(keyRef.getAlias());
        if(masterKeyPair != null)
        {
            decryptionKey = masterKeyPair.getDecryptionKey();
        }

        return decryptionKey;
    }

    @Override
    public void revokeMasterKey(final KeyReference masterKeyRef) throws KeyStoreException, MissingKeyException
    {
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                if(!masterKeyCache.contains(masterKeyRef.getAlias()))
                {
                    throw new MissingKeyException("Invalid master key");
                }
    
                if(encryptingMasterKeyCache.getKeys().size() <= 1)
                {
                    // can't remove last encrypting master key
                    throw new CryptoException("Cannot remove last encrypting master key");
                }
    
                // remove master key so that it can't be used for encryption
                encryptingMasterKeyCache.remove(masterKeyRef.getAlias());

                return null;
            }
        }, false, true);
    }

    @Override
    public Map<String, KeyInformation> getMasterKeys()
    {
        Map<String, KeyInformation> masterKeys = new HashMap<>();
        for(String alias : masterKeyCache.getKeys())
        {
            MasterKeyPair masterKeyPair = masterKeyCache.get(alias);
            Key encryptingKey = masterKeyPair.getEncryptionKey();
            Key decryptingKey = masterKeyPair.getDecryptionKey();

            String encryptionKeyAlgorithm = null;
            String decryptionKeyAlgorithm = null;
            boolean canEncrypt = canEncrypt(masterKeyPair.getAlias());
            if(canEncrypt)
            {
                encryptionKeyAlgorithm = encryptingKey.getAlgorithm();
            }

            // can always decrypt
            boolean canDecrypt = true;
            if(canDecrypt)
            {
                decryptionKeyAlgorithm = decryptingKey.getAlgorithm();
            }

            long numSymmetricKeys = contentDataDAO.countSymmetricKeysForMasterKeyAlias(alias);
            KeyInformation keyInformation = new KeyInformation(alias, encryptionKeyAlgorithm,
                    decryptionKeyAlgorithm, canEncrypt, canDecrypt, numSymmetricKeys);
            masterKeys.put(alias, keyInformation);
        }

        return masterKeys;
    }

    @Override
    public void reEncryptSymmetricKeys(final KeyReference masterKeyRef) throws MissingKeyException
    {
        Key masterDecryptionKey = getDecryptionKey(masterKeyRef);
        if(masterDecryptionKey == null)
        {
            throw new AlfrescoRuntimeException("Invalid master key " + masterKeyRef.getAlias());
        }
        symmetricKeyReencryptor.reEncryptSymmetricKeys(masterKeyRef, masterDecryptionKey);
    }

    @Override
    public void cancelRevocation(KeyReference masterKeyRef) throws KeyStoreException, MissingKeyException
    {
        cancelRevocation(masterKeyRef.getAlias());
    }
}
