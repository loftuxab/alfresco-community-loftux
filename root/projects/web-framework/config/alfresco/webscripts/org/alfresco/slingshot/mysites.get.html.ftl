<!-- Main gui -->

<div class="component">
  <div class="component-title">My Sites</div>
  <div class="component-links">
    <a href="javascript:alert('Not implemented');">Create site</a>
    <!-- showCreateSiteDialog(); -->
  </div>
  <div class="component-list">
<#list sites as site>
      <div>
        <!--<img src="/SOME-IMAGE-PROXY-PATH/${site.icon}">-->
        <a href="${url.context}/page/collaboration/dashboard?doc=${site.name}">${site.name}</href>
      </div>
</#list>
  </div>
</div>
<!-- Create new site form -->
<div id="createSiteDialog" style="visibility: hidden; position:absolute;">
  <div class="hd">Create</div>
  <div class="bd"></div>
  <div class="ft"></div>
</div>
