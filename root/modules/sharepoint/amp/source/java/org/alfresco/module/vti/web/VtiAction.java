
package org.alfresco.module.vti.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* <p>
* VtiAction is an adapter between the contents of an incoming
* HTTP request and the corresponding business logic that should be executed to
* process this request. The controller ({@link VtiRequestDispatcher}) will select an
* appropriate Action for each request and call the <code>execute</code> method.</p>   
* 
* @author Stas Sokolovsky
*/
public interface VtiAction
{
    /**
    * <p>Process the specified HTTP request, and create the corresponding HTTP response.</p> 
    *
    * @param request HTTP request
    * @param response HTTP response
    */
    public void execute(HttpServletRequest request, HttpServletResponse response);
}
