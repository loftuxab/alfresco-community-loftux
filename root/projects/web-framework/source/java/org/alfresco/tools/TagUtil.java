package org.alfresco.tools;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
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
        ServletContext context = request.getSession().getServletContext();
        return execute(tag, context, request);
    }

    public static String execute(Tag tag, ServletContext context,
            HttpServletRequest request)
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

            // process the tag
            tag.setPageContext(fakePageContext);
            tag.doStartTag();
            tag.doEndTag();

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
