<div class="dashlet">
   <div class="title">${msg("header.docSummary")}</div>
   <div class="body scrollableList">
      <#if docs.error?exists>
         <span class="error">${docs.error}</span>
      <#else>
         <#if docs.items?size == 0>
            <span>${msg("label.noItems")}</span>
         <#else>
            <#list docs.items as doc>
               <#assign modifiedBy><a href="${url.context}/page/user/${doc.modifiedByUser}/profile">${doc.modifiedBy}</a></#assign>
               <div class="detail-list-item">
                  <div>
                     <div class="icon">
                        <img src="${url.context}${doc.icon32}" alt="${doc.name?html}" />
                     </div>
                     <div class="details">
                        <h4><a rel="content" href="${url.context}/proxy/alfresco${doc.contentUrl}/${doc.name?url}">${doc.name?html}</a></h4>
                        <div>
                           ${msg("text.modified-by", modifiedBy)} ${msg("text.modified-on", doc.modifiedOn)}
                        </div>
                     </div>
                  </div>
               </div>
            </#list>
         </#if>
      </#if>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
(function()
{
   var links = YAHOO.util.Selector.query("a[rel]", "${args.htmlid}");
   for (var i = 0, len = links.length; i < len; ++i)
   {
      links[i].setAttribute("target", links[i].getAttribute("rel"));
   }
})();
//]]></script>