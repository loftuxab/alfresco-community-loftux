package filters;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public final class AVMfilter implements Filter 
{
    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException 
    { 
        this.filterConfig = filterConfig; 
    } 

    public void destroy() 
    {
        this.filterConfig = null; 
    }


    public void doFilter( ServletRequest   request, 
                          ServletResponse  response,
                          FilterChain      chain
                        ) throws  IOException, ServletException 
    {

        response.setContentType("text/html");

        String avm_data = "This is a fake jsp  <%= new java.util.Date().toString() %><br> Excellent? ";

        PrintWriter out = response.getWriter();
        response.setContentLength( avm_data.length() );
        out.write( avm_data );

    }

}

