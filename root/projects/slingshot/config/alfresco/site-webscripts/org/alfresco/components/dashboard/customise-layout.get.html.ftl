<div id="${args.htmlid}-dialog" class="customise-layout">

   <script type="text/javascript">//<![CDATA[
   new Alfresco.CustomiseLayout("${args.htmlid}").setOptions(
   {
      currentLayout: {id: "${currentLayout.id}", noOfColumns: ${currentLayout.noOfColumns}, description: "${currentLayout.description}", icon: "${url.context}/components/dashboard/images/${currentLayout.id}.png"},
      layouts: {<#list layouts as layout>"${layout.id}": { id: "${layout.id}", noOfColumns: ${layout.noOfColumns}, description: "${layout.description}", icon: "${url.context}/components/dashboard/images/${layout.id}.png"}<#if (layout_has_next)>, </#if></#list>}
   }
   ).setMessages(${messages});
   //]]></script>


   <h2>${msg("header.layout")}</h2>

   <div id="${args.htmlid}-currentLayout-div" class="currentLayout">

      <div>
         <h3>${msg("section.currentLayout")}</h3>
         <ul>                
            <li>
               <div class="layoutDescription">${currentLayout.description}</div>
               <div class="layoutBox">
                  <span>
                     <img class="layoutIcon" src="${url.context}/components/dashboard/images/${currentLayout.id}.png"/>
                     <input id="${args.htmlid}-change-button" type="button" value="${msg("button.showLayouts")}"/>
                  </span>
               </div>
            </li>
         </ul>
      </div>

   </div>

   <div id="${args.htmlid}-layouts-div" class="layouts hiddenComponents">

      <div>
         <h3>${msg("section.selectNewLayout")}</h3>
         <div class="text">${msg("label.layoutWarning")}</div>
         <input id="${args.htmlid}-useCurrent-button" type="button" value="${msg("button.useCurrent")}"/>

         <ul>
         <#list layouts as layout>
            <li id="${args.htmlid}-layout-li-${layout.id}" <#if (currentLayout.id == layout.id)>class="hiddenComponents"</#if>>
               <div class="layoutDescription">${layout.description}</div>               
               <div class="layoutBox">
                  <span>
                     <img class="layoutIcon" src="${url.context}/components/dashboard/images/${layout.id}.png"/>
                     <input id="${args.htmlid}-select-button-${layout.id}" type="button" value="${msg("button.select")}"/>
                  </span>
               </div>
            </li>
         </#list>
         </ul>
      </div>

   </div>

   <div class="clear"></div>

</div>
