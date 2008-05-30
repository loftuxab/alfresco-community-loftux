<script type="text/javascript">//<![CDATA[
   new Alfresco.MySites("${args.htmlid}");
//]]></script>

<div class="dashlet">
  <div class="title">My Sites</div>
  <div class="toolbar">
    <a href="#" id="${args.htmlid}-createSite-button">Create site</a>
  </div>
  <div class="body scrollableList">
<#if sites?exists>
   <#list sites as site>
      <div class="text-list-item">
        <a href="${url.context}/page/collaboration-dashboard?site=${site.shortName}">${site.title}</a>
      </div>
   </#list>
<#else>
      <span>No items to display</span>
</#if>
  </div>
</div>

