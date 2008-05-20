<script type="text/javascript">//<![CDATA[
   new Alfresco.MySites("${args.htmlid}");
//]]></script>

<div class="dashlet">
  <div class="title">My Sites</div>
  <div class="menu">
    <span id="${args.htmlid}-createSite-button"><a href="#">Create site &gt;</a></span>
  </div>
  <div class="body scrollableList">
<#if sites?exists>
   <#list sites as site>
      <div>
        <a href="${url.context}/page/collaboration-dashboard?site=${site.shortName}">${site.title}</a>
      </div>
   </#list>
<#else>
      <div>
        <p>No sites yet - why don&apos;t you create one?</p>
      </div>
</#if>
  </div>
</div>

