<script type="text/javascript">//<![CDATA[
    var sp = new Alfresco.SitePage("${args.htmlid}").setOptions(
	{
		siteId: "${page.url.templateArgs.site!''}"
	});
	sp.setMessages(
      ${messages}
    );
//]]></script>

   <div id="${args.htmlid}-site-page-titleBar" class="site-page-titlebar" >
	   <div id="${args.htmlid}-listtitle" class="list-title">
            <button id="${args.htmlid}-refresh-button" name="generic-page-refresh-button">${msg("button.refresh")}</button>
       </div>
   </div>

   <div id="${args.htmlid}-site-page-infoBar" class="site-page-infobar" >
       <div id="${args.htmlid}-paginator" class="paginator"></div>
   </div>

    <div id="${args.htmlid}-body" class="site-page-body" >
	    <div  id="${args.htmlid}-site-page"> </div>
    </div>
    <div id="${args.htmlid}-prop"></div>
<p/>
<p/>
<p/>