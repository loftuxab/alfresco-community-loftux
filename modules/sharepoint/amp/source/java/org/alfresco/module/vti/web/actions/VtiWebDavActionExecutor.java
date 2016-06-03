package org.alfresco.module.vti.web.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.webdav.WebDAVMethod;
import org.alfresco.repo.webdav.WebDAVServerException;

/**
 * Provides hook for customisation of WebDAV execution logic - specifically within
 * {@link VtiWebDavAction}.
 * 
 * @author Matt Ward
 */
public interface VtiWebDavActionExecutor
{
    void execute(WebDAVMethod method,
                 HttpServletRequest request,
                 HttpServletResponse response) throws WebDAVServerException;
}
