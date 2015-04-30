/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.enterprise.repo.cluster.core;

import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

/**
 * Simple MembershipListener that we can use for testing, to check that the instance has
 * been notified of cluster start up.
 * 
 * @author Matt Ward
 */
public class TestMembershipListener implements MembershipListener
{
    private boolean listenerNotified;
    
    @Override
    public void memberAdded(MembershipEvent arg0)
    {
        listenerNotified = true;
    }

    @Override
    public void memberRemoved(MembershipEvent arg0)
    {
        // NOOP - if memberAdded has already been called, that's good enough for us to know that
        // Hazelcast is routing membership events to an instance of this class.
    }

    /**
     * Check whether this listener has been invoked, i.e. {@link #memberAdded(MembershipEvent)}
     * has been called at least once.
     * 
     * @return true if notified
     */
    public boolean isListenerNotified()
    {
        return listenerNotified;
    }
}
