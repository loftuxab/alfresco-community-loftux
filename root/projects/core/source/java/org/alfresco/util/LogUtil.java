/*
 * Copyright (C) 2005 Jesper Steen Møller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.util;

import org.alfresco.i18n.I18NUtil;
import org.apache.commons.logging.Log;

/**
 * Utility class to assist with I18N of log messages.
 * <p>
 * Calls to this class should still be wrapped with the appropriate log level checks:
 * <pre>
 * if (logger.isDebugEnabled())
 * {
 *     LogUtil.debug(logger, MSG_EXECUTING_STATEMENT, sql);
 * }
 * </pre>
 * 
 * @see org.alfresco.i18n.I18NUtil
 * @since 2.1
 * 
 * @author Derek Hulley
 */
public class LogUtil
{
    /**
     * Log an I18Nized message to DEBUG.
     * 
     * @param logger        the logger to use
     * @param messageKey    the message key
     * @param args          the required message arguments
     */
    public static final void debug(Log logger, String messageKey, Object ... args)
    {
        logger.debug(I18NUtil.getMessage(messageKey, args));
    }

    /**
     * Log an I18Nized message to INFO.
     * 
     * @param logger        the logger to use
     * @param messageKey    the message key
     * @param args          the required message arguments
     */
    public static final void info(Log logger, String messageKey, Object ... args)
    {
        logger.info(I18NUtil.getMessage(messageKey, args));
    }
    
    /**
     * Log an I18Nized message to WARN.
     * 
     * @param logger        the logger to use
     * @param messageKey    the message key
     * @param args          the required message arguments
     */
    public static final void warn(Log logger, String messageKey, Object ... args)
    {
        logger.warn(I18NUtil.getMessage(messageKey, args));
    }
    
    /**
     * Log an I18Nized message to ERROR.
     * 
     * @param logger        the logger to use
     * @param messageKey    the message key
     * @param args          the required message arguments
     */
    public static final void error(Log logger, String messageKey, Object ... args)
    {
        logger.error(I18NUtil.getMessage(messageKey, args));
    }
    
    /**
     * Log an I18Nized message to ERROR with a given source error.
     * 
     * @param logger        the logger to use
     * @param e             the exception cause of the issue
     * @param messageKey    the message key
     * @param args          the required message arguments
     */
    public static final void error(Log logger, Throwable e, String messageKey, Object ... args)
    {
        logger.error(I18NUtil.getMessage(messageKey, args), e);
    }
}
