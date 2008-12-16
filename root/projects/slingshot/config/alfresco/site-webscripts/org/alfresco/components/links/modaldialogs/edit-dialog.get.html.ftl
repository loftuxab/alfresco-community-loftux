<div id="${args.htmlid}-editdlg" class="edit-dialog">
	<div id="${args.htmlid}-editdlg-header" class="hd"></div> 
    <div id="${args.htmlid}-editdlg-body" class="bd"> 
            <form id="${args.htmlid}-form" action="" method="POST">
                <div class="yui-gd">
            	<div class="yui-u first">${msg("dialog.body.title")}</div>
            	<div class="yui-u"><input id="${args.htmlid}-editdlg-title" type="text" name="title" tabindex="1"/> * </div>
	         </div>
	         
	         <div class="yui-gd">
	            <div class="yui-u first">${msg("dialog.body.description")}</div>
	            <div class="yui-u"><textarea id="${args.htmlid}-editdlg-description" type="textarea" rows="3" name="description" tabindex="2"></textarea></div>
	         </div>
	         
	         <div class="yui-gd">
	            <div class="yui-u first">${msg("dialog.body.url")}</div>
	            <div class="yui-u"><input id="${args.htmlid}-editdlg-url" type="text" name="url" tabindex="3"/> * </div>
                <div class="yui-u first">${msg("dialog.body.isinternal")}</div>
                <div class="yui-u"><input id="${args.htmlid}-editdlg-url-checkbox" type="checkbox" name="isinternal"/></div>
             </div>
            <!-- tags -->
              <div class="yui-gd">
                 <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
                 <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />
              </div>
            <!-- end tags -->
 		      <div class="bdft">
	             <input type="submit" id="${args.htmlid}-ok-button" value="${msg("button.ok")}" tabindex="6"/>
	             <input type="button" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" tabindex="7"/>
         	  </div>
            <!-- hidden input-->
              <div class="yui-hidden">
                 <input type="hidden" id="${args.htmlid}-isUpdate" name="isUpdated">    
              </div>
        </form> 
    </div>       
</div>
<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.LinksEditDialog");
//]]></script>