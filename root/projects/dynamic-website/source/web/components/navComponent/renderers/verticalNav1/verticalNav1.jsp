<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.site.model.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%!
	public void makeMenu(RequestContext context, StringBuffer buffer, Page page, int index, int groupIndex, boolean doChildren, boolean topMenuItem)
	{
		String currentPageId = null;
		if(context.getPage() != null)
			currentPageId = context.getPage().getId();
		
		String id = context.getModel().newGUID();
		String href = context.getLinkBuilder().page(context, page.getId(), context.getFormatId());
		
		boolean selected = false;
		if(page.getId().equalsIgnoreCase(currentPageId))
			selected = true;
		
		if(topMenuItem)
		{
			buffer.append("<li class='yuimenuitem first-of-type'>");
			buffer.append("<a class='yuimenuitemlabel' href='" + href + "'>");
			if(selected)
				buffer.append("<B>");
			buffer.append(page.getTitle());
			if(selected)
				buffer.append("</B>");
			buffer.append("</a>");
		}
		else
		{
			buffer.append("<li class='yuimenuitem'>");
			buffer.append("<a class='yuimenuitemlabel' href='" + href + "'>");
			if(selected)
				buffer.append("<B>");
			buffer.append(page.getTitle());
			if(selected)
				buffer.append("</B>");
			buffer.append("</a>");
		}
		
		index = index + 1;
		
		if(doChildren)
		{
			Page[] childPages = page.getChildPages(context);
			if(childPages.length > 0)
			{
				String id2 = context.getModel().newGUID();
				buffer.append("<div id='"+id2+"' class='yuimenu'>");
				buffer.append("<div class='bd'>");
				buffer.append("<ul>");
				for(int i = 0; i < childPages.length; i++)
				{
					makeMenu(context, buffer, childPages[i], index, groupIndex, doChildren, false);
				}
				buffer.append("</ul>");
				buffer.append("</div>");
				buffer.append("</div>");
			}
		}
		
		buffer.append("</li>");
	}
%>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// get the configuration
	String style = (String) context.getRenderContext().get("style");
	if(style == null || "".equals(style))
		style="0";
	
	// get the root node
	Page rootPage = context.getRootPage();
	if(rootPage == null)
	{
		out.println("Unable to render navigation channel.  No root page has been supplied for this site.  Please check the site configuration.");
		return;
	}
	
	// Stylesheet
	String stylesheetUri = "/components/navComponent/renderers/verticalNav1/default/menu.css";
	stylesheetUri = URLUtil.browser(context, stylesheetUri);
	
%>
	<link rel="stylesheet" type="text/css" href="<%=stylesheetUri%>"/>

        <style type="text/css">

            div.yui-b p {
            
                margin: 0 0 .5em 0;
                color: #999;
            
            }
            
            div.yui-b p strong {
            
                font-weight: bold;
                color: #000;
            
            }
            
            div.yui-b p em {

                color: #000;
            
            }            
            
            h1 {

                font-weight: bold;
                margin: 0 0 1em 0;                
                padding: .25em .5em;
                background-color: #ccc;

            }

            #productsandservices {
                
                position: static;
                
            }

        </style>
        
	<script type="text/javascript">

            /*
                 Initialize and render the Menu when its elements are ready 
                 to be scripted.
            */

            YAHOO.util.Event.onContentReady("productsandservices", function () {

                /*
                     Instantiate a Menu:  The first argument passed to the 
                     constructor is the id of the element in the page 
                     representing the Menu; the second is an object literal 
                     of configuration properties.
                */

                var oMenu = new YAHOO.widget.Menu(
                                    "productsandservices", 
                                    {
                                        position: "static", 
                                        hidedelay: 750, 
                                        lazyload: true, 
                                        effect: { 
                                            effect: YAHOO.widget.ContainerEffect.FADE,
                                            duration: 0.25
                                        } 
                                    }
                                );

                /*
                     Call the "render" method with no arguments since the 
                     markup for this Menu instance is already exists in the page.
                */

                oMenu.render();            
            
            });

        </script>	


	<div class="yui-skin-sam">
		<div class="yuimenu" id="productsandservices">
			<div class="bd">
				<ul class="first-of-type">
<%
	StringBuffer buffer = new StringBuffer();
// style == 0 means that we show everything
if("0".equals(style))
{
	makeMenu(context, buffer, rootPage, 0, 0, false, true);

	Page[] children = rootPage.getChildPages(context);
	for(int i = 0; i < children.length; i++)
	{
		makeMenu(context, buffer, children[i], i+1, 0, true, true);
	}
}
// style == 1 means that we show everything under the selected node
if("1".equals(style))
{
	Page currentPage = context.getPage();
	if(currentPage != null)
	{
		makeMenu(context, buffer, currentPage, 0, 0, false, true);

		Page[] children = currentPage.getChildPages(context);
		for(int i = 0; i < children.length; i++)
		{
			makeMenu(context, buffer, children[i], i+1, 0, true, true);
		}
	}
}
	
	out.println(buffer.toString());
%>
				</ul>
			</div>
		</div>
        </div>

