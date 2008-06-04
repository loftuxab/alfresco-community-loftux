<div class="create-folder">
   <div class="hd">New Folder</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="${url.context}/proxy/alfresco/slingshot/doclib/action" method="post">
         <input type="hidden" name="action" value="createFolder" />
         <input type="hidden" name="site" value="" />
         <input type="hidden" name="componentId" value="" />
         <input type="hidden" name="path" value="" />

         <div class="yui-g section-title">
            <h2>New folder details:</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-name">Name:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-name" type="text" name="name"/>&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-title">Title:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="title"/></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">Description:</label></div>
            <div class="yui-u"><textarea id="${args.htmlid}-description" name="description" rows="3" cols="20"></textarea></div>
         </div>
         <div class="yui-g">
            <input type="submit" id="${args.htmlid}-ok" value="OK" />
            <input type="button" id="${args.htmlid}-cancel" value="Cancel" />
         </div>
      </form>
   </div>
</div>
