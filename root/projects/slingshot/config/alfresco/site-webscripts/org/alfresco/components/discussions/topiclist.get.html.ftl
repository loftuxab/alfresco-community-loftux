<script type="text/javascript">//<![CDATA[
   new Alfresco.DiscussionsTopicList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!''}",
      containerId: "${args.container!'blog'}",
      initialFilter:
      {
         filterId: "${page.url.args.filterId!'new'}",
         filterOwner: "${page.url.args.filterOwner!'Alfresco.TopicListFilter'}",
         filterData: <#if page.url.args.filterData??>"${page.url.args.filterData}"<#else>null</#if>
      }
   }).setMessages(
      ${messages}
   );
//]]></script>
<div class="topiclist-infobar yui-gb">
   <div class="yui-u first">
      <div id="${args.htmlid}-listtitle" class="listTitle">
         ${msg("title.generic")}
      </div>
   </div>
   <div class="yui-u">
      <div id="${args.htmlid}-paginator" class="paginator"></div>
   </div>
   <div class="yui-u flat-button">
      <div class="simple-view">
         <button id="${args.htmlid}-simpleView-button" name="topiclist-simpleView-button">${msg("header.simpleList")}</button>
      </div>
   </div>
</div>
<div id="${args.htmlid}-topiclist" class="topiclist">
</div>
