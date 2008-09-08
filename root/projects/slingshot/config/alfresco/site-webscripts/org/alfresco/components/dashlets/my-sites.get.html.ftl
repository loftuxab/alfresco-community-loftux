<script type="text/javascript">//<![CDATA[
new Alfresco.MySites("${args.htmlid}").setOptions({
   sites: [
   <#list sites as site>
      {
         shortName: '${site.shortName?js_string}',
         title: '${site.title?js_string}'
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
            <div id="${args.htmlid}-site-div-${site.shortName}" class="detail-list-item <#if (!site_has_next)>last</#if>">
               <div>
                  <div class="site">
                     <a href="${url.context}/page/site/${site.shortName}/dashboard">${site.title?html}</a>
                  </div>
                  <div class="actions">
                     <span id="${args.htmlid}-delete-span-${site_index}" class="delete" title="${msg("link.deleteSite")}">&nbsp;</span>
                  </div>
               </div>
            <#if site.description?exists && site.description != "">
               <div class="description">${site.description?html}</div>
            <#else>
               <div class="clear"></div>
            </#if>
            </div>
         </#list>
         <#else>
            <span>${msg("label.noSites")}</span>
      </#if>
   </div>
</div>