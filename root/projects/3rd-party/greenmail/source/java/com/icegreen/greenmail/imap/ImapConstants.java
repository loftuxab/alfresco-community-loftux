/* -------------------------------------------------------------------
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 * This file has been modified by the copyright holder. Original file can be found at http://james.apache.org
 * -------------------------------------------------------------------
 * 
 * 2010 - Alfresco Software, Ltd.
 * Alfresco Software has modified source of this file
 * The details of changes as svn diff can be found in svn at location root/projects/3rd-party/src 
 */
package com.icegreen.greenmail.imap;

public interface ImapConstants {
    // Basic response types
    String OK = "OK";
    String NO = "NO";
    String BAD = "BAD";
    String BYE = "BYE";
    String UNTAGGED = "*";

    String SP = " ";
    String VERSION = "IMAP4rev1";
    String CAPABILITIES = "LITERAL+ UIDPLUS";

    String USER_NAMESPACE = "#mail";

    char HIERARCHY_DELIMITER_CHAR = '/';
    char NAMESPACE_PREFIX_CHAR = '#';
    String HIERARCHY_DELIMITER = String.valueOf(HIERARCHY_DELIMITER_CHAR);
    String NAMESPACE_PREFIX = String.valueOf(NAMESPACE_PREFIX_CHAR);

    String INBOX_NAME = "INBOX";
    
    String EIGHT_BIT_ENCODING = "ISO-8859-1";
}
