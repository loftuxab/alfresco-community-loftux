<script type="text/javascript">//<![CDATA[
   new Alfresco.CreateTopic("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!'discussions'}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<h1>${msg("formTitle")}</h1>
<div class="createTopicForm hidden" id ="${args.htmlid}-topic-create-div">
   <form id="${args.htmlid}-form" method="post" action="">
      <div>
         <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
         <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
         <input type="hidden" id="${args.htmlid}-browseTopicUrl" name="browseTopicUrl" value="" />
         
         <label>${msg("topicTitle")}:</label>
         <input type="text" id="${args.htmlid}-title" name="title" size="80" value=""/>
                
         <label>${msg("topicText")}:</label>
         <textarea rows="8" cols="80" id="${args.htmlid}-content" name="content" class="yuieditor"></textarea> 
         
         <label>${msg("tags")}:</label>
         <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
         <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />

      </div>
      <div class="nodeFormAction">
         <input type="submit" id="${args.htmlid}-submit" value="${msg('action.save')}" />
         <input type="reset" id="${args.htmlid}-cancel" value="${msg('action.cancel')}" />
      </div>
   </form>
</div>
