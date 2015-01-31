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
