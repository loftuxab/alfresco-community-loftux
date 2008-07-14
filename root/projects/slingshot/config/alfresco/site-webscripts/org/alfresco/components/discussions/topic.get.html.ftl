<#import "/org/alfresco/modules/discussions/topic.lib.ftl" as topicLib/>

<script type="text/javascript">//<![CDATA[
   new Alfresco.DiscussionsTopic("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      mode: "<#if editMode>edit<#else>view</#if>",
      topicId: "${item.name}",
      topicRef: "${item.nodeRef}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="discussionsBlogHeader2">
	<div class="leftDiscussionBlogHeader listTitle">
		<span class="backLink">
			<a href="${url.context}/page/site/${page.url.templateArgs.site}/discussions-topiclist">
				${msg("header.back")}
			</a>
		</span>
	</div>
</div>

<div id="${args.htmlid}-topic">
   <div id="${args.htmlid}-viewDiv" <#if editMode>class="hidden"</#if> >
      <@topicLib.topicViewHTML htmlid=args.htmlid topic=item/>
   </div>
   <div id="${args.htmlid}-formDiv" <#if ! editMode>class="hidden"</#if> >
      <#-- only render form if the page is loaded in edit mode -->
      <#if editMode><@topicLib.topicFormHTML htmlid=args.htmlid topic=item/></#if>
   </div>
</div>
