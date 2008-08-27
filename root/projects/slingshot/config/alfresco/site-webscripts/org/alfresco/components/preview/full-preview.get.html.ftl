<script type="text/javascript">//<![CDATA[
new Alfresco.FullPreview("${args.htmlid}").setOptions({
   nodeRef: '${node.nodeRef}',
   name: '${node.name}',
   icon: '${node.icon}',
   mimeType: '${node.mimeType}',
   previews: [<#list node.previews as p>'${p}'<#if (p_has_next)>, </#if></#list>]
}).setMessages(
   ${messages}
      );
//]]></script>
<div id="${args.htmlid}-fullPreview-panel" class="full-preview">
   <div class="bd">
      <div id="${args.htmlid}-hd-div" class="bdhd" style="visibility: hidden;">
         <div class="title">
            <img id="${args.htmlid}-title-img" src=""/>
            <span id="${args.htmlid}-title-span"></span>
         </div>
         <div class="close">
            <input id="${args.htmlid}-close-button" type="button" value="${msg("button.close")}" />
         </div>
         <div id="${args.htmlid}-controls-div" class="controls" style="visibility: hidden;">
            <input id="${args.htmlid}-previous-button" type="button" value="${msg("button.previous")}" />
            <span  id="${args.htmlid}-currentFrame-span">${msg("label.currentFrame")}</span>
            <input id="${args.htmlid}-next-button" type="button" value="${msg("button.next")}" />
            <span>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;</span>
            <span>${msg("label.jumpToPage")}</span>
            <input id="${args.htmlid}-jumpToPage-textfield" class="jumpToPage" type="text" />
         </div>
      </div>
      <div class="preview-swf-wrapper">
         <div id="${args.htmlid}-swfPlayer-div" class="preview-swf">
            <div id="${args.htmlid}-swfPlayerMessage-div"></div>
         </div>
      </div>
   </div>
</div>
