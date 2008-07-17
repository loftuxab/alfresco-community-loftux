<div id="${args.htmlid}" class="customise-pages">

   <script type="text/javascript">//<![CDATA[
   new Alfresco.CustomisePages("${args.htmlid}").setOptions(
   {
      pages:{
      <#list pages as page>
         "${page.pageId}":{
            pageId: "${page.pageId}",
            title: "${page.title}",
            description: "${page.description}",
            originallyInUse: ${page.originallyInUse?string}
         }<#if (page_has_next)>,</#if>
      </#list>
      }
   }).setMessages(${messages});
   //]]></script>

   <div id="${args.htmlid}-currentPages-div" class="currentPages">

      <h2>${msg("section.currentPages")}</h2>
      <hr/>
      <div>
         <ul>
            <li id="${args.htmlid}-currentPages-empty-li" class="empty" class="display: none;">
                ${msg("label.noPagesSelected")}
            </li>
            <#list pages as page>
            <li id="${args.htmlid}-currentPage-li-${page.pageId}" <#if (page.originallyInUse)>style="display: none;"</#if>>
               <div class="pageTitle"><h4>${page.title}</h4></div>
               <div class="pageIcon">
                  <img src="${url.context}/components/dashboard/images/dashboard-1-column.png"/>
               </div>
               <div class="pageActions">
                  <input id="${args.htmlid}-info-button-${page.pageId}" type="button" value="${msg("button.info")}"/>
                  <input id="${args.htmlid}-remove-button-${page.pageId}" type="button" value="${msg("button.remove")}"/>
               </div>
            </li>
            </#list>
         </ul>
      </div>

      <div>
         <div class="buttons" id="${args.htmlid}-addPages-div">
            <input id="${args.htmlid}-addPages-button" type="button" value="${msg("button.addPages")}" />
         </div>
      </div>

   </div>

   <div id="${args.htmlid}-pages-div" class="pages" style="display: none;">

      <div class="text">
         <a class="closeLink" href="javascript: var nothing;" id="${args.htmlid}-closeAddPages-link">${msg("link.close")}</a>
         <h3 class="padded">${msg("section.selectNewPages")}</h3>
      </div>
      <div>
         <ul>
            <li id="${args.htmlid}-pages-empty-li" class="empty" class="display: none;">
                ${msg("label.noPagesLeft")}
            </li>
            <#list pages as page>
               <li id="${args.htmlid}-page-li-${page.pageId}" <#if (!page.originallyInUse)>style="display: none;"</#if>>
               <div class="pageIcon">
                  <img src="${url.context}/components/dashboard/images/dashboard-1-column.png"/>                  
               </div>
               <div class="pageActions">
                  <input id="${args.htmlid}-select-button-${page.pageId}" type="button" value="${msg("button.select")}"/>
               </div>
               <div class="pageBox">
                  <div class="pageTitle"><h3>${page.title}</h3></div>
                  <div class="pageDescription">${page.description}</div>
               </div>
               </li>
            </#list>
         </ul>
      </div>

   </div>

   <div>
      <hr/>
      <div class="buttons">
         <input id="${args.htmlid}-save-button" type="button" value="${msg("button.save")}"/>
         <input id="${args.htmlid}-cancel-button" type="button" value="${msg("button.cancel")}"/>
      </div>
   </div>



</div>
