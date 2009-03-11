<script type="text/javascript">//<![CDATA[
   new Alfresco.CreateComment("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!"blog"}",
      height: ${args.editorHeight!250},
      width: ${args.editorWidth!538},
      editorConfig : 
      {
         <#--
         //YUI
         //             height: this.options.height + 'px',
         //             width: this.options.width + 'px',
         //             dompath: false, //Turns on the bar at the bottom
         //             animate: false, //Animates the opening, closing and moving of Editor windows
         //             markup: "xhtml",
         //             toolbar:  Alfresco.util.editor.getTextOnlyToolbarConfig(this._msg),
         -->
         //Tiny MCE
         height: ${args.editorHeight!250},
         width: ${args.editorWidth!538},
         theme:'advanced',
         theme_advanced_buttons1 : "bold,italic,underline,|,bullist,numlist,|,forecolor,backcolor,|,undo,redo,removeformat",
         theme_advanced_toolbar_location : "top",
         theme_advanced_toolbar_align : "left",
         theme_advanced_statusbar_location : "bottom",
         theme_advanced_resizing : true,
         theme_advanced_buttons2 : null,
         theme_advanced_buttons3 : null,
         language:'${locale?substring(0, 2)}'         
      }
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-form-container" class="addCommentForm hidden">
	<div class="commentFormTitle">
		<label for="${htmlid}-content">${msg("addComment")}:</label>
	</div>
	<div class="editComment">
		<form id="${htmlid}-form" method="post" action="">
		    <div>
            <input type="hidden" id="${args.htmlid}-nodeRef" name="nodeRef" value="" />
            <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
            <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
            <input type="hidden" id="${args.htmlid}-itemTitle" name="itemTitle" value="" />
            <input type="hidden" id="${args.htmlid}-page" name="page" value="" />
            <input type="hidden" id="${args.htmlid}-pageParams" name="pageParams" value="" />
            
			   <textarea id="${htmlid}-content" rows="8" cols="80" name="content"></textarea>
			</div>
			<div class="commentFormAction">
				<input type="submit" id="${htmlid}-submit" value="${msg('postComment')}" />
			</div>
		</form>
	</div>
</div>