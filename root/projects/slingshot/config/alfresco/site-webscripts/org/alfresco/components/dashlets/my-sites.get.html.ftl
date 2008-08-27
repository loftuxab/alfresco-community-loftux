<script type="text/javascript">//<![CDATA[
new Alfresco.MySites("${args.htmlid}").setOptions({
   sites: [
   <#list sites as site>
      {
         shortName: '${site.shortName}',
         title: '${site.title}'
      }<#if (site_has_next)>,</#if>
   </#list>
   ]
});
//]]></script>

<div class="dashlet my-sites">
   <div class="title">${msg("header.mySites")}</div>
   <div class="toolbar">
      <a href="#" id="${args.htmlid}-createSite-button">${msg("link.createSite")}</a>
   </div>
   <div class="body scrollableList">
      <#if sites??>
         <#list sites as site>
            <div id="${args.htmlid}-site-div-${site.shortName}" class="detail-list-item">
               <div class="site">
                  <a href="${url.context}/page/site/${site.shortName}/dashboard">${site.title}</a>
               </div>
               <div class="actions">
                  <span id="${args.htmlid}-delete-span-${site_index}"
                     class="delete"
                     href=""
                     title="${msg("link.deleteSite")}"/>
               </div>
            </div>
         </#list>
         <#else>
            <span>${msg("label.noSites")}</span>
      </#if>
   </div>
</div>

