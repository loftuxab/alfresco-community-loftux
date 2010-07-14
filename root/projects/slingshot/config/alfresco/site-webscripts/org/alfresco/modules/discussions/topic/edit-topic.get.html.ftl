<div class="editNodeForm">
   <form id="${args.htmlid}-form" method="post" action="">
      <div>
         <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
         <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
         <input type="hidden" id="${args.htmlid}-page" name="page" value="discussions-topicview" />
         
         <label for="${args.htmlid}-title">${msg("label.title")}:</label>
         <input type="text" id="${args.htmlid}-title" name="title" size="80" value=""/>
                
         <label for="${args.htmlid}-content">${msg("topicText")}:</label>
         <textarea rows="8" cols="80" id="${args.htmlid}-content" name="content" class="yuieditor"></textarea> 
         
         <label for="${args.htmlid}-tag-input-field">${msg("label.tags")}:</label>
         <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
         <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />

      </div>
      <div class="nodeFormAction">
         <input type="submit" id="${args.htmlid}-submit" value="${msg('action.save')}" />
         <input type="reset" id="${args.htmlid}-cancel" value="${msg('action.cancel')}" />
      </div>
   </form>
</div>