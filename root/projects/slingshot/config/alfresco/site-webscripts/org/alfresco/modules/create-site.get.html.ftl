<div id="${args.htmlid}-dialog" class="create-site">
   <div class="hd">${msg("header.createSite")}</div>
   <div class="bd">
      <div class="yui-t1">
         <form id="${args.htmlid}-form" action="${url.context}/proxy/alfresco/api/sites" method="POST">
            <input type="hidden" id="${args.htmlid}-isPublic" name="isPublic" value="true"/>

            <div class="yui-g section-title">
               <h2>${msg("section.info")}</h2>               
            </div>
            <div class="yui-gd">
               <div class="yui-u first">${msg("label.name")}</div>
               <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="title" tabindex="1"/></div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first"><label for="${args.htmlid}-shortName">${msg("label.shortName")}</label></div>
               <div class="yui-u"><input id="${args.htmlid}-shortName" type="text" name="shortName" tabindex="2"/>&nbsp;*</div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">${msg("label.description")}</div>
               <div class="yui-u"><textarea id="${args.htmlid}-description" name="description" rows="3" tabindex="3"></textarea></div>
            </div>
            <div class="yui-g section-title">
               <h2>${msg("section.type")}</h2>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">${msg("label.type")}</div>
               <div class="yui-u">
                  <select id="${args.htmlid}-sitePreset" name="sitePreset" tabindex="4"><option value="extranet">Extranet</option></select>
               </div>
            </div>
            <!--
         <div class="yui-g">
            <h2>Logo:</h2>
            <hr/>
         </div>
         -->
            <div class="yui-g section-buttons">
               <input type="submit" id="${args.htmlid}-ok-button" value="${msg("button.ok")}" tabindex="5"/>
               <input type="submit" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" tabindex="6"/>
            </div>

         </form>
      </div>
   </div>
</div>