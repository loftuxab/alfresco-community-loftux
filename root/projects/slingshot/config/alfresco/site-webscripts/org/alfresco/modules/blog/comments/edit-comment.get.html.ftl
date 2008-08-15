<div class="editCommentForm">
   <div class="commentFormTitle">
      ${msg("editComment")}
   </div>
   <div class="editComment">
      <form id="${args.htmlid}-form" method="POST" action="">
         <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
         <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
         <input type="hidden" id="${args.htmlid}-itemTitle" name="itemTitle" value="" />
         <input type="hidden" id="${args.htmlid}-browseItemUrl" name="browseItemUrl" value="" />
         
         <textarea id="${args.htmlid}-content" rows="8" cols="80" name="content"></textarea>
         
         <div class="commentFormAction">
            <input type="submit" id="${args.htmlid}-submit"  value="${msg('action.update')}" />
            <input type="reset"  id="${args.htmlid}-cancel" value="${msg('action.cancel')}" />
         </div>
      </form>
   </div>
</div>
