/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.module.mediawikiintegration;

import org.alfresco.service.namespace.QName;

/**
 * MediaWiki integration model constants 
 * 
 * @author Roy Wetherall
 */
public interface Constants 
{
    /** Namespace details */
    public static final String NAMESPACE = "http://www.alfresco.org/model/mediawikiintegration/1.0";
    public static final String CONFIG_NAMESPACE = "http://www.alfresco.org/model/mediawikiintegrationconfigproperty/1.0";
    public static final String PREFIX = "mw";
    public static final String CONFIG_PREFIX = "mwcp";
    
    /** MediaWiki Type */
    public static final QName TYPE_MEDIAWIKI = QName.createQName(NAMESPACE, "mediaWiki");
    public static final QName ASSOC_CONFIG = QName.createQName(NAMESPACE, "config");
    
    /** MediaWiki Config Type */
    public static final QName TYPE_MEDIAWIKI_CONFIG = QName.createQName(NAMESPACE, "mediaWikiConfig");
    public static final QName PROP_SITENAME         = QName.createQName(CONFIG_NAMESPACE, "wgSitename");
    public static final QName PROP_DB_TYPE          = QName.createQName(CONFIG_NAMESPACE, "wgDBtype");
    public static final QName PROP_DB_SERVER        = QName.createQName(CONFIG_NAMESPACE, "wgDBserver");
    public static final QName PROP_DB_NAME          = QName.createQName(CONFIG_NAMESPACE, "wgDBname");
    public static final QName PROP_DB_USER          = QName.createQName(CONFIG_NAMESPACE, "wgDBuser");
    public static final QName PROP_DB_PASSWORD      = QName.createQName(CONFIG_NAMESPACE, "wgDBpassword");
    public static final QName PROP_DB_PORT          = QName.createQName(CONFIG_NAMESPACE, "wgDBport");
    public static final QName PROP_DB_PREFIX        = QName.createQName(CONFIG_NAMESPACE, "wgDBprefix");
    public static final QName PROP_SQL_DROP_TABLES  = QName.createQName(NAMESPACE, "sqlDropTables");
    
}
