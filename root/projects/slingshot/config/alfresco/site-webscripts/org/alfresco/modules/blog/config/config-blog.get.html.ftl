<div id="${args.htmlid}-dialog" class="config-blog">
   <div class="hd">${msg("header.configBlog")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form"
            action="${url.context}/proxy/alfresco/api/blog/site/${args.siteId}/${args.containerId}?alf_method=PUT"
            method="POST">
            
         <div class="yui-g">
            <h2>${msg("section.type")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix">${msg("label.type")}</div>
            <div class="yui-u caret-fix">
               <select id="${args.htmlid}-blogType" name="blogType" tabindex="1">
                  <option value=""></option>
                  <option value="wordpress">wordpress</option>
                  <option value="typepad">typepad</option>
               </select>
            </div>
         </div>

         <div class="yui-g">
            <h2>${msg("section.info")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${args.htmlid}-id">${msg("label.id")}:</label></div>
            <div class="yui-u caret-fix">
               <input id="${args.htmlid}-blogid" type="text" name="blogId" tabindex="3" value="" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix">${msg("label.name")}:</div>
            <div class="yui-u caret-fix">
               <input id="${args.htmlid}-title" type="text" name="blogName" tabindex="5" value="" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix">${msg("label.description")}:</div>
            <div class="yui-u caret-fix">
               <textarea id="${args.htmlid}-description" name="blogDescription" rows="3" tabindex="7" value=""></textarea>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${args.htmlid}-url">${msg("label.url")}:</label></div>
            <div class="yui-u caret-fix">
               <input id="${args.htmlid}-url" type="text" name="blogUrl" tabindex="9" value="" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${args.htmlid}-username">${msg("label.username")}:</label></div>
            <div class="yui-u">
               <input id="${args.htmlid}-username" type="text" name="username" tabindex="11" value="" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${args.htmlid}-password">${msg("label.password")}:</label></div>
            <div class="yui-u caret-fix">
               <input id="${args.htmlid}-password" type="password" name="password" tabindex="13" value="" />
            </div>
         </div>
            
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="15"/>
            <input type="submit" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="17"/>
         </div>

      </form>

   </div>
</div>