<div id="${args.htmlid}-form-title"></div>
<div class="editReplyForm">
   <div id="${args.htmlid}-replyto" class="replyTo hidden">
   </div>
   
   <div class="editReply">
      <form id="${args.htmlid}-form" name="replyForm" method="POST" action="">
         <div>
            <input type="hidden" id="${args.htmlid}-site"name="site" value="" />
            <input type="hidden" id="${args.htmlid}-container"name="container" value="" />
            <input type="hidden" id="${args.htmlid}-browseTopicUrl" name="browseTopicUrl" value="" />
            <textarea id="${args.htmlid}-content" rows="8" cols="80" name="content" class="yuieditor"></textarea>
            <div class="nodeFormAction">
               <input type="submit" id="${args.htmlid}-submit" />
               <input type="reset"  id="${args.htmlid}-cancel"  value="${msg('action.cancel')}" />
            </div>
         </div>
      </form>
   </div>
</div>