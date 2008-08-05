<div id="${args.htmlid}-dialog" class="config-blog">
   <div class="hd">${msg("header.configBlog")}</div>
   <div class="bd">
      <form id="${args.htmlid}-configBlog-form"
            action="${url.context}/proxy/alfresco/api/blog/site/${site}/${container}?alf_method=PUT"
            method="POST">
            
         <div class="yui-g">
            <h2>${msg("section.type")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix">${msg("label.type")}</div>
            <div class="yui-u caret-fix">
               <select id="${args.htmlid}-blogType" name="blogType" tabindex="1">
                  <option value=""></option>
                  <option
                        value="wordpress"
                  <#if (item.type=="wordpress")>selected="selected"</#if>
                  >wordpress</option>
                  <option
                        value="typepad"
                  <#if (item.type=="typepad")>selected="selected"</#if>
                  >typepad</option>
               </select>
            </div>
         </div>

         <div class="yui-g">
            <h2>${msg("section.info")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${args.htmlid}-id">${msg("label.id")}:</label></div>
            <div class="yui-u caret-fix">
               <input id="${args.htmlid}-id" type="text" name="blogId" tabindex="3" value="${item.id?html}" />
               <!--  &nbsp;* -->
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix">${msg("label.name")}:</div>
            <div class="yui-u caret-fix">
               <input id="${args.htmlid}-title" type="text" name="blogName" tabindex="5" value="${item.name?html}" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix">${msg("label.description")}:</div>
            <div class="yui-u caret-fix">
               <textarea id="${args.htmlid}-description" name="blogDescription" rows="3" tabindex="7" value="${item.description?html}"></textarea>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${args.htmlid}-url">${msg("label.url")}:</label></div>
            <div class="yui-u caret-fix">
               <input id="${args.htmlid}-url" type="text" name="blogUrl" tabindex="9" value="${item.url?html}" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${args.htmlid}-username">${msg("label.username?html")}:</label></div>
            <div class="yui-u">
               <input id="${args.htmlid}-id" type="text" name="username" tabindex="11" value="${item.username}" />
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first caret-fix"><label for="${args.htmlid}-password">${msg("label.password?html")}:</label></div>
            <div class="yui-u caret-fix">
               <input id="${args.htmlid}-password" type="password" name="password" tabindex="13" value="${item.password}" />
            </div>
         </div>
            
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok-button" value="${msg("button.ok")}" tabindex="15"/>
            <input type="submit" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" tabindex="17"/>
         </div>

      </form>

   </div>
</div>