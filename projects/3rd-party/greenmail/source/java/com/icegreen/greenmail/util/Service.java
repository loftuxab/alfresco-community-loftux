/*
 * #%L
 * Alfresco greenmail implementation
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
/*
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 *
 */
package com.icegreen.greenmail.util;

/**
 * A class that facilitate service implementation
 *
 * @author Wael Chatila
 * @version $id: $
 * @since 2005
 */
abstract public class Service extends Thread {
    public abstract void run();

    public abstract void quit();

    private volatile boolean keepRunning = false;

    //---------
    public void init(Object obj) {
        //empty
    }

    public void destroy(Object obj) {
        //empty
    }

    final protected boolean keepOn() {
        return keepRunning;
    }

    public synchronized void startService(Object obj) {
        if (!keepRunning) {
            keepRunning = true;
            init(obj);
            start();
        }
    }

    /**
     * Stops the service. If a timeout is given and the service has still not
     * gracefully been stopped after timeout ms the service is stopped by force.
     *
     * @param obj
     * @param millis value in ms
     */
    public synchronized final void stopService(Object obj, Long millis) {
        boolean doDestroy = keepRunning;
        try {
            if (keepRunning) {
                keepRunning = false;
                interrupt();
                quit();
                if (null == millis) {
                    join();
                } else {
                    join(millis.longValue());
                }
            }
        } catch (InterruptedException e) {
            //its possible that the thread exits between the lines keepRunning=false and intertupt above
        } finally {
            if (doDestroy) {
                destroy(obj);
            }
        }
    }

    public final void stopService(Object obj) {
        stopService(obj, null);
    }

    public final void stopService(Object obj, long millis) {
        stopService(obj, new Long(millis));
    }
}

