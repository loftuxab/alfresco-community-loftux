<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.MySites("${args.htmlid}").setOptions(
   {
      sites: [
<#if sites??>
   <#list sites as site>
      {
         shortName: '${site.shortName?js_string}',
         title: '${site.title?js_string}',
         sitePreset: '${site.sitePreset?js_string}',
         isSiteManager: ${site.isSiteManager?string}
      }<#if (site_has_next)>,</#if>
   </#list>
</#if>
      ]
   });
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>

<div class="dashlet my-sites">
   <div class="title">${msg("header.myWorkspaces")}</div>    
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
<#if (numSites !=0)>
   <#list sites as site>
      <#if site.sitePreset == "document-workspace">
         <div id="${args.htmlid}-site-div-${site.shortName}" class="detail-list-item <#if site_index = 0>first-item<#elseif !site_has_next>last-item</#if>">
            <div>
               <div class="site">
                  <a href="${url.context}/page/site/${site.shortName}/dashboard">${site.title?html}</a>
               </div>
               <div class="actions">
                  <#if (site.isSiteManager)>
                  <span id="${args.htmlid}-delete-span-${site_index}" class="delete" title="${msg("link.deleteWorkspaces")}">&nbsp;</span>
                  </#if>
               </div>
            </div>
         <#if site.description?exists && site.description != "">
            <div class="description">${site.description?html}</div>
         <#else>
            <div class="clear"></div>
         </#if>
         </div>
      </#if>
   </#list>
<#else>
      <div class="detail-list-item first-item last-item">
         <span>${msg("label.noWorkspaces")}</span>
      </div>
</#if>
   </div>
</div>