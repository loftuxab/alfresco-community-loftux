<script type="text/javascript">//<![CDATA[
   new Alfresco.MySites("${args.htmlid}");
//]]></script>

<div class="component">
  <div class="component-title">My Sites</div>
  <div class="component-links">
    <span class="mysites-createSite-button"><a href="#">Create site &gt;</a></span>
  </div>
  <div class="component-list">
<#list sites as site>
      <div>
        <a href="${url.context}/page/collaboration/dashboard?site=${site.shortName}">${site.title}</a>
      </div>
</#list>
  </div>
</div>

<div id="${args.htmlid}-createSite"></div>
