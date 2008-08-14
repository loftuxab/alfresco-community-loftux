<div class="editNodeForm">
   <form id="${args.htmlid}-form" method="post" action="">
      <div>
         <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
         <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
         <input type="hidden" id="${args.htmlid}-browseTopicUrl" name="browseTopicUrl" value="" />
         
         <label>${msg("topic.form.topicTitle")}:</label>
         <input type="text" id="${args.htmlid}-title" name="title" size="80" value=""/>
                
         <label>${msg("topic.form.topicText")}:</label>
         <textarea rows="8" cols="80" id="${args.htmlid}-content" name="content" class="yuieditor"></textarea> 
         
         <label>${msg("topic.tags")}:</label>
         <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
         <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />

      </div>
      <div class="nodeFormAction">
         <input type="submit" id="${args.htmlid}-submit" value="${msg('topic.form.save')}" />
         <input type="reset" id="${args.htmlid}-cancel" value="${msg('topic.form.cancel')}" />
      </div>
   </form>
</div>