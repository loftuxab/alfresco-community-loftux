<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.site.model.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%!
	public void makeMenu(RequestContext context, StringBuffer buffer, Page page, int index, int groupIndex, boolean doChildren, boolean topMenuItem)
	{
		String currentPageId = null;
		if(context.getCurrentPage() != null)
			currentPageId = context.getCurrentPage().getId();
		
		String id = context.getModel().newGUID();
		String href = context.getLinkBuilder().page(context, page.getId(), context.getCurrentFormatId());
		
		boolean selected = false;
		if(page.getId().equalsIgnoreCase(currentPageId))
			selected = true;
		
		if(topMenuItem)
		{
			buffer.append("<li index='" + index + "' groupindex='" + groupIndex + "' id='" + id + "' class='yuimenubaritem first-of-type yuimenubaritem-hassubmenu'>");
			buffer.append("<a class='yuimenubaritemlabel yuimenubaritemlabel-hassubmenu' href='" + href + "' onmouseover=\"this.style.cursor='hand'\">");
			if(selected)
				buffer.append("<B>");
			buffer.append(page.getName());
			if(selected)
				buffer.append("</B>");
			buffer.append("</a>");
		}
		else
		{
			buffer.append("<li class='yuimenuitem'>");
			buffer.append("<a class='yuimenuitemlabel' href='"+href+"' onmouseover=\"this.style.cursor='hand'\">");
			if(selected)
				buffer.append("<B>");
			buffer.append(page.getName());
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
				buffer.append("<div style='z-index: 1; position: absolute; visibility: hidden;' id='"+id2+"' class='yuimenu yui-module yui-overlay'>");
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
	
	// config values
	String componentId = (String) context.getRenderContext().get("component-id");
	String altText = (String) context.getRenderContext().get("alt");
	if(altText == null)
		altText = "Navigation";		
	String orientation = (String) context.getRenderContext().get("orientation");
	if(orientation == null || "".equals(orientation))
		orientation = "horizontal";
	
	// get the root page
	Page rootPage = context.getRootPage();
	if(rootPage == null)
	{
		out.println("Unable to render navigation channel.  No root page has been supplied for this site.  Please check the site configuration.");
		return;
	}
	
	// Stylesheet
	String stylesheetUri = "/components/navComponent/renderers/horizontalNav1/default/menu.css";
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

		#nav-menu {

			margin: 0 0 10px 0;

		}

	</style>

	<script type="text/javascript">

		YAHOO.util.Event.onContentReady("nav-menu", function () 
		{

			var Dom = YAHOO.util.Dom,
				oAnim;  // Animation instance


			function onSubmenuBeforeShow(p_sType, p_sArgs) 
			{
				var oBody,
					oShadow,
					oUL;


				if (this.parent) {

					/*
						 Get a reference to the Menu's shadow element and
						 set its "height" property to "0px" to syncronize
						 it with the height of the Menu instance.
					*/

					oShadow = this.element.lastChild;
					oShadow.style.height = "0px";


					/*
						Stop the Animation instance if it is currently
						animating a Menu.
					*/

					if (oAnim && oAnim.isAnimated()) {

						oAnim.stop();
						oAnim = null;

					}


					/*
						Set the body element's "overflow" property to
						"hidden" to clip the display of its negatively
						positioned <ul> element.
					*/

					oBody = this.body;


					/*
						There is a bug in gecko-based browsers where
						an element whose "position" property is set to
						"absolute" and "overflow" property is set to
						"hidden" will not render at the correct width when
						its offsetParent's "position" property is also
						set to "absolute."  It is possible to work around
						this bug by specifying a value for the width
						property in addition to overflow.
					*/

					if (this.parent &&
						!(this.parent instanceof YAHOO.widget.MenuBarItem) &&
						YAHOO.env.ua.gecko) {

						Dom.setStyle(oBody, "width", (oBody.clientWidth + "px"));

					}


					Dom.setStyle(oBody, "overflow", "hidden");


					/*
						Set the <ul> element's "marginTop" property
						to a negative value so that the Menu's height
						collapses.
					*/

					oUL = oBody.getElementsByTagName("ul")[0];

					Dom.setStyle(oUL, "marginTop", ("-" + oUL.offsetHeight + "px"));

				}

			}


			function onTween(p_sType, p_aArgs, p_oShadow) 
			{
				if (this.cfg.getProperty("iframe")) {

					this.syncIframe();

				}

				if (p_oShadow) {

					p_oShadow.style.height = this.element.offsetHeight + "px";

				}
			}


			function onAnimationComplete(p_sType, p_aArgs, p_oShadow) 
			{
				var oBody = this.body,
					oUL = oBody.getElementsByTagName("ul")[0];

				if (p_oShadow) {

					p_oShadow.style.height = this.element.offsetHeight + "px";

				}

				Dom.setStyle(oUL, "marginTop", "");
				Dom.setStyle(oBody, "overflow", "");


				if (this.parent &&
					!(this.parent instanceof YAHOO.widget.MenuBarItem) &&
					YAHOO.env.ua.gecko) {

					Dom.setStyle(oBody, "width", "");

				}

			}


			function onSubmenuShow(p_sType, p_sArgs) 
			{
				var oElement,
					oShadow,
					oUL;

				if (this.parent) {

					oElement = this.element;
					oShadow = oElement.lastChild;
					oUL = this.body.getElementsByTagName("ul")[0];


					/*
						 Animate the <ul> element's "marginTop" style
						 property to a value of 0.
					*/

					oAnim = new YAHOO.util.Anim(oUL,
						{ marginTop: { to: 0 } },
						.5, YAHOO.util.Easing.easeOut);


					oAnim.onStart.subscribe(function () {

						oShadow.style.height = "100%";

					});


					oAnim.animate();


					/*
						Subscribe to the Anim instance's "tween" event for
						IE to syncronize the size and position of a
						submenu's shadow and iframe shim (if it exists)
						with its changing height.
					*/

					if (YAHOO.env.ua.ie) {

						oShadow.style.height = oElement.offsetHeight + "px";


						/*
							Subscribe to the Anim instance's "tween"
							event, passing a reference Menu's shadow
							element and making the scope of the event
							listener the Menu instance.
						*/

						oAnim.onTween.subscribe(onTween, oShadow, this);

					}


					/*
						Subscribe to the Anim instance's "complete" event,
						passing a reference Menu's shadow element and making
						the scope of the event listener the Menu instance.
					*/

					oAnim.onComplete.subscribe(onAnimationComplete, oShadow, this);

				}

			}


			var oMenuBar = new YAHOO.widget.MenuBar("nav-menu", {
				autosubmenudisplay: true,
				hidedelay: 750,
				lazyload: true });

			oMenuBar.subscribe("beforeShow", onSubmenuBeforeShow);
			oMenuBar.subscribe("show", onSubmenuShow);

			oMenuBar.render();

		});
		
	</script>


	<div class="yui-skin-sam">
		<div style="z-index: 0; position: static; display: block; visibility: visible;" id="nav-menu" class="yuimenubar yuimenubarnav yui-module yui-overlay visible">
			<div class="bd">
				<ul class="first-of-type">
<%
	StringBuffer buffer = new StringBuffer();
	
	makeMenu(context, buffer, rootPage, 0, 0, false, true);
	Page[] children = rootPage.getChildPages(context);
	for(int i = 0; i < children.length; i++)
	{
		makeMenu(context, buffer, children[i], i+1, 0, true, true);
	}
	
	out.println(buffer.toString());
%>
				</ul>
			</div>
		</div>
        </div>

