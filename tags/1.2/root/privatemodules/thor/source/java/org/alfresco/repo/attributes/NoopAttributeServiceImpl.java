/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.repo.attributes;

import java.io.Serializable;

import org.alfresco.service.cmr.attributes.AttributeService;

/**
 * A No-op implementation of the Attribute Service
 */
public class NoopAttributeServiceImpl implements AttributeService
{
    public NoopAttributeServiceImpl()
    {
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(Serializable ... keys)
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable getAttribute(Serializable ... keys)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void getAttributes(final AttributeQueryCallback callback, Serializable ... keys)
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void setAttribute(Serializable value, Serializable ... keys)
    {
    }

    /**
     * {@inheritDoc}
     */
    public void createAttribute(Serializable value, Serializable... keys)
    {
    }

    /**
     * {@inheritDoc}
     */
    public void updateOrCreateAttribute(
            Serializable keyBefore1,
            Serializable keyBefore2,
            Serializable keyBefore3,
            Serializable keyAfter1,
            Serializable keyAfter2,
            Serializable keyAfter3)
    {
    }

    /**
     * {@inheritDoc}
     */
    public void removeAttribute(Serializable ... keys)
    {
    }

    /**
     * {@inheritDoc}
     */
    public void removeAttributes(Serializable ... keys)
    {
    }
}
