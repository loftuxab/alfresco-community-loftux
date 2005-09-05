package org.alfresco.jcr.server;

import javax.jcr.Repository;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class RepositoryStartupServlet extends HttpServlet
{
    private static final long serialVersionUID = -4763518135895358778L;

    private static InitialContext jndiContext;
    
    private final static String repositoryName = "Alfresco.Repository";

    
    /**
     * Initializes the servlet
     * @throws ServletException
     */
    public void init()
        throws ServletException
    {
        super.init();
        initRepository();
    }

    /**
     * destroy the servlet
     */
    public void destroy()
    {
        super.destroy();

        if (jndiContext != null)
        {
            try
            {
                jndiContext.unbind(repositoryName);
            }
            catch (NamingException e)
            {
                // Note: Itentionally ignore... TODO:
            }
        }
    }

    /**
     * Creates a new Repository based on configuration
     * @throws ServletException
     */
    private void initRepository()
        throws ServletException
    {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        Repository repository = (Repository)context.getBean("JCR.Repository");
        
        try
        {
            jndiContext = new InitialContext();
            jndiContext.bind(repositoryName, repository);
        }
        catch (NamingException e)
        {
            throw new ServletException(e);
        }
    }


}
