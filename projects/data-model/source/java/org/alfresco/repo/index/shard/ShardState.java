/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
package org.alfresco.repo.index.shard;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import org.alfresco.service.cmr.repository.StoreRef;

/**
 * Store shard state for auto discovery 
 * 
 * @author Andy
 *
 */
public class ShardState implements Serializable
{
    private static final long serialVersionUID = -5961621567026938963L;

    ShardInstance shardInstance;
        
    private boolean isMaster;
    
    private HashMap<String, Object> propertyBag = new HashMap<String, Object>();

    public ShardState()
    {
    }
    
    /**
     * @return the shardInstance
     */
    public ShardInstance getShardInstance()
    {
        return shardInstance;
    }

    /**
     * @param shardInstance the shardInstance to set
     */
    public void setShardInstance(ShardInstance shardInstance)
    {
        this.shardInstance = shardInstance;
    }

    /**
     * @return the isMaster
     */
    public boolean isMaster()
    {
        return isMaster;
    }

    /**
     * @param isMaster the isMaster to set
     */
    public void setMaster(boolean isMaster)
    {
        this.isMaster = isMaster;
    }

    /**
     * @return the propertyBag
     */
    public HashMap<String, Object> getPropertyBag()
    {
        return propertyBag;
    }

    /**
     * @param propertyBag the propertyBag to set
     */
    public void setPropertyBag(HashMap<String, Object> propertyBag)
    {
        this.propertyBag = propertyBag;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "ShardState [shardInstance=" + shardInstance + ", isMaster=" + isMaster + ", propertyBag=" + propertyBag + "]";
    }


    
}
