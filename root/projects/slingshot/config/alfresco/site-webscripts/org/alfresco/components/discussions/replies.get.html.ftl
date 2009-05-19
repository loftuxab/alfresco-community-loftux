
<script type="text/javascript">//<![CDATA[
   new Alfresco.TopicReplies("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      containerId: "${template.properties.container!'discussions'}",
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
         width: '538',
         height: '250',
         theme:'advanced',
         theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect,forecolor,backcolor",         
         theme_advanced_buttons2 :"bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,removeformat",
         theme_advanced_toolbar_location : "top",
         theme_advanced_toolbar_align : "left",
         theme_advanced_statusbar_location : "bottom",
         theme_advanced_path : false,         
         theme_advanced_resizing : true,
         theme_advanced_buttons3 : null,
         language:'${locale?substring(0, 2)}'         
      }
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-replies-root" class="indented hidden">

</div>
