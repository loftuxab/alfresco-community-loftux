/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.connector;

/**
 * Interface for a Credential Vault
 * 
 * Credential vaults allow for the storage and retrieval of credentials by
 * credential id.
 * 
 * They can also be loaded and saved if they are backed by a persisted storage
 * location.
 * 
 * @author muzquiano
 */
public interface CredentialVault
{
    /**
     * Places the given credentials into the vault
     * 
     * @param credentials the credentials
     */
    public void store(Credentials credentials);

    /**
     * Retrieves credentials for a given endpoint id from the vault
     * 
     * @param endpointId the endpoint id
     * 
     * @return the credentials
     */
    public Credentials retrieve(String endpointId);
    
    /**
     * Removes credentials for a given endpoint id from the vault
     * @param endpointId
     */
    public void remove(String endpointId);
    
    /**
     * @return true if any credentials are stored for this endpoint id
     */
    public boolean hasCredentials(String endpointId);

    /**
     * Creates new credentials which are stored in this vault
     * 
     * @param endpointId
     * @return the credentials object
     */
    public Credentials newCredentials(String endpointId);
    
    /**
     * Returns the ids for stored credentials
     * 
     * @return
     */
    public String[] getStoredIds();
    
    /**
     * Tells the Credential Vault to load state from persisted store
     * 
     * @return whether the credential vault successfully loaded
     */
    public boolean load();

    /**
     * Tells the Credential Vault to write state to persisted store
     * 
     * @return whether the credential vault successfully saved
     */
    public boolean save();
}
