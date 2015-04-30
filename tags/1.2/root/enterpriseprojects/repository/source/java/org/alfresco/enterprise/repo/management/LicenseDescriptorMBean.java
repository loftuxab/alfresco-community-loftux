/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Date;

/**
 * A Management Interface exposing properties of an Alfresco Enterprise license.
 * 
 * @author dward
 */
public interface LicenseDescriptorMBean
{
    /**
     * Gets the date on which the license was issued.
     * 
     * @return the issue date
     */
    public Date getIssued();

    /**
     * Gets the date until the license is valid.
     * 
     * @return valid until date (or the epoch if no time limit)
     */
    public Date getValidUntil();

    /**
     * Gets the length (in days) of license validity.
     * 
     * @return length in days of license validity (or -1, if no time limit)
     */
    public int getDays();

    /**
     * Ges the number of remaining days left on license.
     * 
     * @return remaining days (or -1, if no time limit)
     */
    public int getRemainingDays();

    /**
     * Gets the subject of the license.
     * 
     * @return the subject
     */
    public String getSubject();

    /**
     * Gets the holder of the license.
     * 
     * @return the holder
     */
    public String getHolder();

    /**
     * Gets the issuer of the license.
     * 
     * @return the issuer
     */
    public String getIssuer();

    /**
     * Does this license allow the heart beat to be disabled?.
     * 
     * @return <code>true</code> if this license allow the heart beat to be disabled
     */
    public boolean isHeartBeatDisabled();
    
    /**
     * The maximum number of documents 
     * @return the maximum number of documents or <code>null</code>
     */
    public Long getMaxDocs();
    
    /**
     * The maximum number of users
     * @return the maximum number of documents or <code>null</code>
     */
    public Long getMaxUsers();
    
    /**
     * The licenseMode
     * @return the licenseMode
     */
    public String getLicenseMode();
    
    /**
     * Attempt to load any new license.
     */
    public String loadLicense();
    
    /**
     * Is the cloud sync key available - we don't display the key itself because its supposed to be hidden
     */
    public boolean isCloudSyncKeyAvailable();
    
    /**
     * Is clustering allowed in the license
     */
    public boolean isClusterEnabled();
    
    /**
     * Is cryptodoc (encrypted file content store) allowed in the license
     */
    public boolean isCryptodocEnabled();
    
    /**
     * The current number of documents 
     * @return the current number of documents or <code>null</code>
     */
    public Long getCurrentDocs();
    
    /**
     * The current number of users
     * @return the current number of documents or <code>null</code>
     */
    public Long getCurrentUsers();
}
