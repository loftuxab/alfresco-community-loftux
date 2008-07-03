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

<#--
<button id="${args.htmlid}-edit-button" name="editButton">Edit</button>
<button id="${args.htmlid}-delete-button" name="deleteButton">Delete</button>

<br>
<br>
-->

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
      <@topicLib.topicViewHTML topic=item/>
   </div>
   <div id="${args.htmlid}-formDiv" <#if ! editMode>class="hidden"</#if> >
      <#-- only render form if the page is loaded in edit mode -->
      <#if editMode><@topicLib.topicFormHTML htmlId="${args.htmlid}" topic=item/></#if>
   </div>
</div>
