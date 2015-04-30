/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.enterprise.repo.management;

import java.util.List;

import org.alfresco.repo.action.ActionStatistics;
import org.alfresco.repo.action.RunningAction;

/**
 * JMX MXBean interface for exposing information about Alfresco actions.
 *
 * @author Alex Miller
 */
public interface ActionsMXBean
{
    /**
     * @return Information about the currently running actions.
     */
    List<RunningAction> getRunningActions(); 
    
    /**
     * @return The number of currently running actions.
     */
    int getRunningActionCount();
    
    /**
     * @return Accumulated statistics about individual actions.
     */
    List<ActionStatistics> getActionStatistics();
}
