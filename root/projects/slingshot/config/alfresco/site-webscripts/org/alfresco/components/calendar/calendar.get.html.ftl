<script type="text/javascript">//<![CDATA[
   new Alfresco.Calendar("${args.htmlid}").setSiteId(
	"${page.url.templateArgs.site!""}"
	).setMessages(
		${messages}
	);
//]]></script>

<div id="${args.htmlid}-body">
   <div id="calendar"></div>
   <div>
      <div id="${args.htmlid}-viewButtons" class="doclist-viewButtons"><a href="#" id="${args.htmlid}-addEvent-button">${msg("button.add-event")}</a></div>
      </div>
      <div id="${args.htmlid}-addEvent"></div>
   </div>
</div>