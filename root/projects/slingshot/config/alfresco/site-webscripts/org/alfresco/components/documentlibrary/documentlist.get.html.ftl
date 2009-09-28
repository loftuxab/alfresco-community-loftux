<#macro initialFilter>
   <#assign filterId = page.url.args["filter"]!"path">
      initialFilter:
      {
         filterId: "${filterId}"
      },
</#macro>
<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/yui/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"documentLibrary"}",
      initialPath: "${page.url.args["path"]!""}",
      <@initialFilter />
      usePagination: ${(args.pagination!false)?string},
      showFolders: ${(preferences.showFolders!false)?string},
      simpleView: ${(preferences.simpleView!false)?string},
      highlightFile: "${page.url.args["file"]!""}",
      vtiServer: ${vtiServer}
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="doclist">
   <div id="${args.htmlid}-doclistBar" class="yui-gc doclist-bar flat-button">
      <div class="yui-u first">
         <div class="file-select">
            <button id="${args.htmlid}-fileSelect-button" name="doclist-fileSelect-button">${msg("menu.select")}</button>
            <div id="${args.htmlid}-fileSelect-menu" class="yuimenu">
               <div class="bd">
                  <ul>
                     <li><span><span class="selectDocuments">${msg("menu.select.documents")}</span></span></li>
                     <li><span><span class="selectFolders">${msg("menu.select.folders")}</span></span></li>
                     <li><span><span class="selectAll">${msg("menu.select.all")}</span></span></li>
                     <li><span><span class="selectInvert">${msg("menu.select.invert")}</span></span></li>
                     <li><span><span class="selectNone">${msg("menu.select.none")}</span></span></li>
                  </ul>
               </div>
            </div>
         </div>
         <div id="${args.htmlid}-paginator" class="paginator"></div>
      </div>
      <div class="yui-u align-right">
         <button id="${args.htmlid}-showFolders-button" name="doclist-showFolders-button">${msg("button.folders.show")}</button>
         <span class="separator">&nbsp;</span>
         <button id="${args.htmlid}-simpleView-button" name="doclist-simpleView-button">${msg("button.view.simple")}</button>
      </div>
   </div>

   <div id="${args.htmlid}-documents" class="documents"></div>

   <div id="${args.htmlid}-doclistBarBottom" class="yui-gc doclist-bar doclist-bar-bottom flat-button">
      <div class="yui-u first">
         <div class="file-select">&nbsp;</div>
         <div id="${args.htmlid}-paginatorBottom" class="paginator"></div>
      </div>
   </div>

   <!-- Action Sets -->
   <div style="display:none">
      <!-- Action Set "More..." container -->
      <div id="${args.htmlid}-moreActions">
         <div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"><span>${msg("actions.more")}</span></a></div>
         <div class="more-actions hidden"></div>
      </div>

      <!-- Action Set Templates -->
<#list actionSets?keys as key>
   <#assign actionSet = actionSets[key]>
      <div id="${args.htmlid}-actionSet-${key}" class="action-set">
   <#list actionSet as action>
         <div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="${action.type}" title="${msg(action.label)}"><span>${msg(action.label)}</span></a></div>
   </#list>
      </div>
</#list>
   </div>

   <div id="${args.htmlid}-customize" class="customize">
      <div class="hd">${msg("customize.title")}</div>
      <div class="bd">
         <form id="${args.htmlid}-customize-form" action="#" method="post">
            <div class="yui-g">
               <h2>${msg("customize.header.actions")}</h2>
            </div>
            <div class="bdft">
               <input type="button" id="${args.htmlid}-customize-ok" value="${msg("button.ok")}" tabindex="1" />
               <input type="button" id="${args.htmlid}-customize-cancel" value="${msg("button.cancel")}" tabindex="2" />
            </div>
         </form>
      </div>
   </div>

</div>