<script type="text/javascript">//<![CDATA[
   new Alfresco.CreateTopic("${args.htmlid}").setOptions(
   {
      topicId: "${page.url.args.topicId!''}",
      siteId: "${page.url.templateArgs.site!''}",
      containerId: "${page.url.args.containerId!'discussions'}",
      editorConfig: 
      {
         width: '700',
         height: '180',
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
      },     
      <#if (page.url.args.topicId! == "")>
      editMode: false
      <#else>
      editMode: true
      </#if>

   }).setMessages(
      ${messages}
   );
//]]></script>
<div class="createTopicForm">
   <h1><#if (page.url.args.topicId! == "")>${msg("header.create")}<#else>${msg("header.edit")}</#if></h1>
   <hr/>
</div>
<div class="createTopicForm hidden" id ="${args.htmlid}-topic-create-div">
   <form id="${args.htmlid}-form" method="post" action="">
      <div>
         <input type="hidden" id="${args.htmlid}-topicId" name="topic" value="" />
         <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
         <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
         <input type="hidden" id="${args.htmlid}-page" name="page" value="discussions-topicview" />
         
         <label for="${args.htmlid}-title">${msg("topicTitle")}:</label>
         <input type="text" id="${args.htmlid}-title" name="title" size="80" value=""/>
                
         <label for="${args.htmlid}-content">${msg("topicText")}:</label>
         <textarea rows="8" cols="80" id="${args.htmlid}-content" name="content" class="yuieditor"></textarea>
         
         <label for="${htmlid}-tag-input-field">${msg("tags")}:</label>
         <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
         <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />

      </div>
      <div class="nodeFormAction">
         <input type="submit" id="${args.htmlid}-submit" value="${msg('action.save')}" />
         <input type="reset" id="${args.htmlid}-cancel" value="${msg('action.cancel')}" />
      </div>
   </form>
</div>
