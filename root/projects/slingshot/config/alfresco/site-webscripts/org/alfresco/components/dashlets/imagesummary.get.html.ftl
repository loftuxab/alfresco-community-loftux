<#macro detailsUrl image label>
   <a href="${url.context}/page/site/${image.location.site}/document-details?nodeRef=${image.nodeRef}" class="theme-color-1">${label}</a>
</#macro>
<script type="text/javascript">//<![CDATA[
   new Alfresco.ImageSummary("${args.htmlid}");
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>
<div class="dashlet">
   <div class="title">${msg("header.title")}</div>
   <div id="${args.htmlid}-list" class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
<#if images.message?exists>
      <div class="detail-list-item first-item last-item">
         <div class="error">${images.message}</div>
      </div>
<#elseif images.items?size == 0>
      <div class="detail-list-item first-item last-item">
         <span>${msg("label.noitems")}</span>
      </div>
<#else>
   <#assign detailsmsg = msg("label.viewdetails")>
   <#list images.items as image>
      <div class="images">
         <div class="item">
            <div class="thumbnail">
               <a href="${url.context}/proxy/alfresco/${image.contentUrl}" rel="lightbox" title="${image.displayName?html} - ${msg("text.modified-by", image.modifiedBy)} ${image.modifiedOn?datetime("dd MMM yyyy HH:mm:ss 'GMT'Z '('zzz')'")?string("dd MMM, yyyy HH:mm:ss")}"><img src="${url.context}/proxy/alfresco/api/node/${image.nodeRef?replace('://','/')}/content/thumbnails/doclib?c=force"/></a>
            </div>
            <div class="details">
               <@detailsUrl image detailsmsg />
            </div>
         </div>
      </div>
   </#list>
</#if>
   </div>
</div>