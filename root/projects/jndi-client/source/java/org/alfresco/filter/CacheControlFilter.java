/*-----------------------------------------------------------------------------
*  Copyright 2006 Alfresco Inc.
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
 * http://www.alfresco.com/legal/licensing"*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    CacheControlFilter.java
*----------------------------------------------------------------------------*/

package org.alfresco.filter;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.context.ApplicationContext;

/**
*   Sets "Cache-Control" http header values in response 
*   depending upon which virtual server is being accessed.
*   
*/
public class CacheControlFilter implements Filter 
{
    protected FilterConfig      config   = null;
    protected static String  [] CacheControlHeader_;
    protected static Pattern [] HostPattern_;

    public void init(FilterConfig config) throws ServletException 
    {
    	this.config = config;

        // Because this filter is in $VIRTUAL_TOMCAT_HOME/conf/web.xml,
        // it's pushed into every webapp.  While a different Filter
        // instances is created for each virtual webapp, every filter
        // shares the same immutable configuration.   Thus, perform
        // a 1-time init for the class if this hasn't happened yet.

        if ( HostPattern_ == null ) { Init(); } 
    }

    protected static void Init()
    {
        // A normal filter would fetch config from the  
        // $VIRTUAL_TOMCAT_HOME/conf/web.xml file like this:
        //
        //        String value = null;
        //        try 
        //        { 
        //            value    = config.getInitParameter("max-age"); 
        //            max_age_ = Long.valueOf(value).longValue();
        //        } 
        //        catch (NumberFormatException e) 
        //        {
        //            max_age_= -1;
        //            config.getServletContext().log(
        //                "Invalid format for max-age initParam; expected integer (seconds)");
        //        }
        //        catch (Throwable t) { max_age_= -1; }
        //
        // 
        // However, this filter applies the same rules to all virtual webapps
        // and draws its rule set from a Spring application context. 

        ApplicationContext springContext =
            org.alfresco.jndi.AVMFileDirContext.GetSpringApplicationContext();

        CacheControlFilterInfoBean cacheControlInfo = 
         (CacheControlFilterInfoBean) springContext.getBean("cacheControlInfo");

        Set<Map.Entry<String,String>> cacheControlRulesEntrySet =  
            cacheControlInfo.getCacheControlRulesEntrySet();

        int len = cacheControlRulesEntrySet.size();
        CacheControlHeader_ = new String[ len ];
        HostPattern_        = new Pattern [ len ];

        int i=0;
        for ( Map.Entry<String,String> e : cacheControlRulesEntrySet )
        {
            String key = e.getKey();
            try 
            {
                CacheControlHeader_[i] = e.getValue();
                HostPattern_[i]        = Pattern.compile( 
                                             key, Pattern.CASE_INSENSITIVE );
            }
            catch (java.util.regex.PatternSyntaxException pe)
            {
                // TODO:  create logging for webapp that won't get in the way of
                //        class unloading. Log a message along the lines of:
                //        "Bad cacheControlRule regex: " + key)
                //            
                //        For now, just propagate the exception.
                //              
                throw pe;
            }
            i++;
        }
    }


    public void doFilter( ServletRequest request, 
                          ServletResponse response,
                          FilterChain chain
                        ) throws IOException, ServletException 
    {
        // HttpServletRequest req  = (HttpServletRequest)  request;
        HttpServletResponse   res  = (HttpServletResponse) response;

        String serverName  = request.getServerName();

        for (int i=0; i< HostPattern_.length; i++)
        {
            // Use find(), not match().
            // We don't want implicit '^' and '$' anchors in regex.
           
            if ( HostPattern_[i].matcher( serverName  ).find() )
            {
                res.setHeader("Cache-Control", CacheControlHeader_[i] );
                break;
            }
        }

        chain.doFilter(request, response); 
    }
    public void destroy() { }
}
