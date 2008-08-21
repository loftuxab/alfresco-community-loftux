<script type="text/javascript">//<![CDATA[
new Alfresco.Preview("${args.htmlid}").setOptions({
   nodeRef: '${node.nodeRef}',
   name: '${node.name}',
   icon: '${node.icon}',
   mimeType: '${node.mimeType}',
   previews: [<#list node.previews as p>'${p}'<#if (p_has_next)>, </#if></#list>]
}).setMessages(
   ${messages}
      );
//]]></script>
<div class="preview">
   <div class="hd">
      <h4>
         <img id="${args.htmlid}-title-img" src=""/>
         <span id="${args.htmlid}-title-span"></span>
      </h4>
   </div>
   <div class="bd">
      <div id="${args.htmlid}-swfPlayer-div" class="preview-swf">
         <div id="${args.htmlid}-swfPlayerMessage-div"></div>
      </div>
   </div>
   <div class="ft">
      <input id="${args.htmlid}-previous-button" type="button" value="${msg("button.previous")}" />
      <span  id="${args.htmlid}-currentFrame-span">${msg("label.currentFrame")}</span>
      <input id="${args.htmlid}-next-button" type="button" value="${msg("button.next")}" />
      <span>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
      <span>${msg("label.jumpToPage")}</span>
      <input id="${args.htmlid}-jumpToPage-textfield" class="jumpToPage" type="text" />
      <span>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
      <input id="${args.htmlid}-fullPreview-button" type="button" value="${msg("button.fullPreview")}" />
   </div>
</div>
