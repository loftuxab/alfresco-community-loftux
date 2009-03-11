<script type="text/javascript">//<![CDATA[
   new Alfresco.CommentList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!"blog"}",
      height: ${args.editorHeight!180},
      width: ${args.editorWidth!700},
      editorConfig:
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
         height: ${args.editorHeight!180},
         width: ${args.editorWidth!700},
         theme: 'advanced',
         theme_advanced_buttons1: "bold,italic,underline,|,bullist,numlist,|,forecolor,backcolor,|,undo,redo,removeformat",
         theme_advanced_toolbar_location: "top",
         theme_advanced_toolbar_align: "left",
         theme_advanced_statusbar_location: "bottom",
         theme_advanced_resizing: true,
         theme_advanced_buttons2: null,
         theme_advanced_buttons3: null,
         theme_advanced_path: false,
         language: '${locale?substring(0, 2)}'
      }      
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="comment-list" style="display:none;">
   <div class="postlist-infobar">
      <div id="${args.htmlid}-title" class="commentsListTitle"></div>
      <div id="${args.htmlid}-paginator" class="paginator"></div>
   </div>
   <div class="clear"></div>
   <div id="${args.htmlid}-comments"></div>
</div>