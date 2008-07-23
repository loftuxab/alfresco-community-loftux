<div id="${args.htmlid}-dialog" class="document-preview">
   <div class="hd">
      <img id="${args.htmlid}-title-img" src=""/>
      <span id="${args.htmlid}-title-span"></span>
   </div>
   <div class="bd">
      <div id="${args.htmlid}-swfPlayer-div" class="preview-document-swf">\!--style="width: 480px; height: 500px;"-->
         ${msg("label.noFlash")}
      </div>

      <div class="bdft">
         <input id="${args.htmlid}-previous-button" type="button" value="${msg("button.previous")}" />
         <span  id="${args.htmlid}-currentFrame-span" class="footerText">${msg("label.currentFrame")}</span>
         <input id="${args.htmlid}-next-button" type="button" value="${msg("button.next")}" />
         &nbsp;|&nbsp;
         <span class="footerText">${msg("label.jumpToPage")}</span>
         <input id="${args.htmlid}-jumpToPage-textfield" class="jumpToPage" type="text" />
      </div>

</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.DocumentPreview");
//]]></script>
