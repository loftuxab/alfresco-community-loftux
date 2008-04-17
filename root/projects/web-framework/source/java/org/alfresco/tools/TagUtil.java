package org.alfresco.tools;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * Provides a simple helper class for executing tags in wrapped
 * HTTP objects
 * 
 * @author Uzquiano
 */
public class TagUtil
{
    public static String execute(Tag tag, HttpServletRequest request)
    {
        return execute(tag, request, null);
    }

    public static String execute(Tag tag, HttpServletRequest request, String bodyContentString)
    {
        ServletContext context = request.getSession().getServletContext();
        return execute(tag, context, request, bodyContentString);
    }

    public static String execute(Tag tag, ServletContext context,
            HttpServletRequest request)
    {
        return execute(tag, context, request, null);
    }
    
    public static String execute(Tag tag, ServletContext context,
            HttpServletRequest request, String bodyContentString)
    {
        // wrap the request
        WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest(
                request);

        // create a fake response (into which we will write)
        FakeHttpServletResponse fakeResponse = new FakeHttpServletResponse();

        try
        {
            //PrintWriter writer = new PrintWriter(response.getOutputStream());
            FakeJspWriter fakeJspWriter = new FakeJspWriter(
                    fakeResponse.getWriter());

            // create a fake page context
            FakeJspPageContext fakePageContext = new FakeJspPageContext(
                    context, wrappedRequest, fakeResponse, fakeJspWriter);

            // begin executing the tag
            tag.setPageContext(fakePageContext);
            int startTagReturn = tag.doStartTag();
            if(tag instanceof BodyTagSupport)
            {
                if(startTagReturn == tag.EVAL_BODY_INCLUDE)
                {   
                    BodyTagSupport support = ((BodyTagSupport)tag);
                    
                    BodyContent bc = fakePageContext.pushBody();
                    support.setBodyContent(bc);
                    
                    support.doInitBody();
                    support.doAfterBody();
                    
                    fakePageContext.popBody();
                }
            }
            
            if(bodyContentString != null)
            {
                fakeJspWriter.print(bodyContentString);
            }
            tag.doEndTag();
            tag.release();

            // render the output
            //jspWriter.flush();
            String output = fakeResponse.getContentAsString();
            return output;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}
