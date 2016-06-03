/*
 * #%L
 * qa-share
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
package org.alfresco.share.util.httpCore;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;


public class Headers {

    public static final Header CONTENT_TYPE_JSON = new BasicHeader("Content-type", "application/json");
    public static final Header CONTENT_TYPE_ATOM = new BasicHeader("Content-type", "application/atom+xml;type=entry");
    public static final Header CONTENT_TYPE_APP = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");
    public static final Header X_REQ_WITH_XML = new BasicHeader("X-Requested-With", "XMLHttpRequest");
    public static final Header X_REQ_WITH_APP = new BasicHeader("X-Requested-With", "application/x-www-form-urlencoded");
    //add to queries in HttpCore. Visible only in core package.
    static final Header ACCEPT_LANGUAGE = new BasicHeader("Accept-Language","en,ru;q=0.7,en-us;q=0.3");
    static final Header USER_AGENT = new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:19.0) Gecko/20100101 Firefox/19.0");
    static final Header ACCEPT = new BasicHeader("Accept", "en-us,en;q=0.5");
}
