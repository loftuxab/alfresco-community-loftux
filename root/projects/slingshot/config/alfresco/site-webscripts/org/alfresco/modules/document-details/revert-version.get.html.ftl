<div id="${args.htmlid}-dialog" class="revert-version">
   <div class="hd">
      <span id="${args.htmlid}-header-span"></span>
   </div>
   <div class="bd">
      <form id="${args.htmlid}-revertVersion-form" method="POST"
            action="${url.context}/proxy/alfresco/api/revert">
         <input type="hidden" id="${args.htmlid}-nodeRef-hidden" name="nodeRef" value=""/>
         <input type="hidden" id="${args.htmlid}-version-hidden" name="version" value=""/>

         <div id="${args.htmlid}-versionSection-div">
            <div class="yui-gd">
               <div class="yui-u first">
                  <label for="${args.htmlid}-minorVersion-radioButton">${msg("label.version")}</label>
               </div>
               <div class="yui-u">
                  <input id="${args.htmlid}-minorVersion-radioButton" type="radio" name="majorVersion" checked="checked" value="false"/> ${msg("label.minorVersion")}
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">&nbsp;
               </div>
               <div class="yui-u">
                  <input id="${args.htmlid}-majorVersion-radioButton" type="radio" name="majorVersion" value="true"/> ${msg("label.majorVersion")}
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  <label for="${args.htmlid}-description-textarea">${msg("label.comments")}</label>
               </div>
               <div class="yui-u">
                  <textarea id="${args.htmlid}-description-textarea" name="description" rows="4"></textarea>
               </div>
            </div>
         </div>

         <div class="bdft">
            <input id="${args.htmlid}-ok-button" type="button" value="${msg("button.ok")}" />
            <input id="${args.htmlid}-cancel-button" type="button" value="${msg("button.cancel")}" />
         </div>

      </form>

   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.RevertVersion");
//]]></script>
