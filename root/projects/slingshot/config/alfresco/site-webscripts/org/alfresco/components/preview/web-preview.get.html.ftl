<#if (node?exists)>
<script type="text/javascript">//<![CDATA[
new Alfresco.WebPreview("${args.htmlid}").setOptions(
{
   nodeRef: "${node.nodeRef}",
   name: "${node.name?js_string}",
   icon: "${node.icon}",
   mimeType: "${node.mimeType}",
   previews: [<#list node.previews as p>"${p}"<#if (p_has_next)>, </#if></#list>],
   size: "${node.size}"
}).setMessages(
   ${messages}
      );
//]]></script>
</#if>
<div class="web-preview shadow">
   <div class="hd">
      <div class="title">
         <h4>
            <img id="${args.htmlid}-title-img" src="${url.context}/components/images/generic-file-32.png" alt="File" />
            <span id="${args.htmlid}-title-span"></span>            
         </h4>
      </div>
   </div>
   <div class="bd">
      <div id="${args.htmlid}-shadow-swf-div" class="preview-swf">
         <div id="${args.htmlid}-swfPlayerMessage-div">${msg("label.preparingPreviewer")}</div>
      </div>
   </div>
</div>
