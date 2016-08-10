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
/* -------------------------------------------------------------------
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 * This file has been modified by the copyright holder. Original file can be found at http://james.apache.org
 * -------------------------------------------------------------------
 */
package com.icegreen.greenmail.store;

import javax.mail.Flags;


/**
 * The set of flags associated with a message.
 * TODO - why not use javax.mail.Flags instead of having our own.
 * <p/>
 * <p>Reference: RFC 2060 - para 2.3
 *
 * @author <a href="mailto:charles@benett1.demon.co.uk">Charles Benett</a>
 * @version 0.1 on 14 Dec 2000
 */
public class MessageFlags {
    public static final Flags ALL_FLAGS = new Flags();

    static {
        ALL_FLAGS.add(Flags.Flag.ANSWERED);
        ALL_FLAGS.add(Flags.Flag.DELETED);
        ALL_FLAGS.add(Flags.Flag.DRAFT);
        ALL_FLAGS.add(Flags.Flag.FLAGGED);
        ALL_FLAGS.add(Flags.Flag.RECENT);
        ALL_FLAGS.add(Flags.Flag.SEEN);
    }

    public static final String ANSWERED = "\\ANSWERED";
    public static final String DELETED = "\\DELETED";
    public static final String DRAFT = "\\DRAFT";
    public static final String FLAGGED = "\\FLAGGED";
    public static final String SEEN = "\\SEEN";

    /**
     * Returns IMAP formatted String of MessageFlags for named user
     */
    public static String format(Flags flags) {
        StringBuffer buf = new StringBuffer();
        buf.append("(");
        if (flags.contains(Flags.Flag.ANSWERED)) {
            buf.append("\\Answered ");
        }
        if (flags.contains(Flags.Flag.DELETED)) {
            buf.append("\\Deleted ");
        }
        if (flags.contains(Flags.Flag.DRAFT)) {
            buf.append("\\Draft ");
        }
        if (flags.contains(Flags.Flag.FLAGGED)) {
            buf.append("\\Flagged ");
        }
        if (flags.contains(Flags.Flag.RECENT)) {
            buf.append("\\Recent ");
        }
        if (flags.contains(Flags.Flag.SEEN)) {
            buf.append("\\Seen ");
        }
        // Remove the trailing space, if necessary.
        if (buf.length() > 1) {
            buf.setLength(buf.length() - 1);
        }
        buf.append(")");
        return buf.toString();
    }
}

