<script type="text/javascript">//<![CDATA[
new Alfresco.MySites("${args.htmlid}").setOptions(
{
   sites: [
<#if sites??>
   <#list sites as site>
   {
      shortName: '${site.shortName?js_string}',
      title: '${site.title?js_string}'
   }<#if (site_has_next)>,</#if>
   </#list>
</#if>
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
      <div id="${args.htmlid}-site-div-${site.shortName}" class="detail-list-item <#if site_index = 0>first-item<#elseif !site_has_next>last-item</#if>">
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
      <div class="detail-list-item first-item last-item">
         <span>${msg("label.noSites")}</span>
      </div>
</#if>
   </div>
</div>