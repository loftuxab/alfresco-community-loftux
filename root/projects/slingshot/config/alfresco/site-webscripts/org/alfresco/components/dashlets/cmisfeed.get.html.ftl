<#import "/org/alfresco/utils/feed.utils.ftl" as feedLib/>
<#assign DISPLAY_ITEMS = 5>
<#assign target="_blank">

<div class="dashlet cmis">
   <div class="title">${msg("label.title")}</div>
   <div class="body scrollableList" id="${args.htmlid}-scrollableList">
   
   <div>${msg("label.body")} <a href="${msg("label.link")}" target="${target}">${msg("label.linkText")}</a></div>
   <hr/>
   <div><a href="${remote}${msg("label.repolink")}" target="${target}">${msg("label.repolinkText")}</a></div>
   <hr/>
   <div>${msg("label.blogTitle")}</div><hr/>
   
   <#if items?exists && items?size &gt; 0>
      <#list items as item>
         <#if item_index &lt; limit?number><@feedLib.renderItem item=item target=target/><#else><#break></#if>
      </#list>
   <#else>
      <em>${msg("label.notFound")}.</em>
   </#if>
   </div>
</div>