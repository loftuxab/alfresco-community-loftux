<script type="text/javascript">//<![CDATA[
   new Alfresco.MySites("${args.htmlid}").setOptions(
   {
      sites: [
<#if sites??>
   <#list sites as site>
      {
         shortName: '${site.shortName?js_string}',
         title: '${site.title?js_string}',
         isFavourite: ${site.isFavourite?string},
         isImapFavourite: ${site.isImapFavourite?string},
         isSiteManager: ${site.isSiteManager?string}
      }<#if (site_has_next)>,</#if>
   </#list>
</#if>
      ]
   }).setMessages(
      ${messages}
   );
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>

<div class="dashlet my-sites">
   <div class="title">${msg("header.mySites")}</div>
   <div class="toolbar">
      <a href="#" id="${args.htmlid}-createSite-button" class="theme-color-1">${msg("link.createSite")}</a>
   </div>
   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
<#if sites??>
   <#list sites as site>
      <div id="${args.htmlid}-site-div-${site.shortName}" class="detail-list-item <#if site_index = 0>first-item<#elseif !site_has_next>last-item</#if>">
         <div>
            <div class="my-actions">
               <span id="${args.htmlid}-favourite-span-${site_index}" class="favourite <#if (site.isFavourite)>enabled</#if>" title="${msg("link.favouriteSite")}">&nbsp;</span>
               <#if imapServerStatus = "enabled">
                  <span id="${args.htmlid}-imap-favourite-span-${site_index}" class="imap-favourite <#if (site.isImapFavourite)>enabled</#if>" title="${msg("link.imap_favouriteSite")}">&nbsp;</span>
               </#if>
            </div>
            <div class="site">
               <a href="${url.context}/page/site/${site.shortName}/dashboard" class="theme-color-1">${site.title?html}</a>
            </div>
            <div class="actions">
               <#if (site.isSiteManager)>
               <span id="${args.htmlid}-delete-span-${site_index}" class="delete" title="${msg("link.deleteSite")}">&nbsp;</span>
               </#if>
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