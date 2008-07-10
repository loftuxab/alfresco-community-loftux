<div id="${args.htmlid}" class="customise-layout">

   <script type="text/javascript">//<![CDATA[
   new Alfresco.CustomiseLayout("${args.htmlid}").setOptions(
   {
      currentLayout:
      {
         id: "${currentLayout.id}",
         noOfColumns: ${currentLayout.noOfColumns},
         description: "${currentLayout.description}",
         icon: "${url.context}/components/dashboard/images/${currentLayout.id}.png"
      },
      layouts:
      {
<#list layouts as layout>
         "${layout.id}":
         {
            id: "${layout.id}",
            noOfColumns: ${layout.noOfColumns},
            description: "${layout.description}",
            icon: "${url.context}/components/dashboard/images/${layout.id}.png"
         }<#if (layout_has_next)>,</#if>
</#list>
      }
   }
   ).setMessages(${messages});
   //]]></script>


   <div id="${args.htmlid}-currentLayout-div" class="currentLayout">

      <h2>${msg("section.currentLayout")}
         <span id="${args.htmlid}-currentLayoutDescription-span">${currentLayout.description}</span>
      </h2>
      <hr/>

      <div>
         <img id="${args.htmlid}-currentLayoutIcon-img" class="layoutIcon"
              src="${url.context}/components/dashboard/images/${currentLayout.id}.png"/>
         <div id="${args.htmlid}-changeButtonWrapper-div" class="buttons">
            <input id="${args.htmlid}-change-button" type="button" value="${msg("button.showLayouts")}"/>
         </div>
      </div>

   </div>

   <div id="${args.htmlid}-layouts-div" class="layouts" style="display: none;">

      <h2>${msg("section.selectNewLayout")}</h2>
      <hr/>

      <div>
         <div class="text">${msg("label.layoutWarning")}</div>

         <ul>
         <#list layouts as layout>
            <li id="${args.htmlid}-layout-li-${layout.id}" <#if (currentLayout.id == layout.id)>style="display: none;"</#if>>
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

      <hr/>

      <div>
         <div class="buttons">
            <input id="${args.htmlid}-cancel-button" type="button" value="${msg("button.useCurrent")}"/>
         </div>
      </div>
      
   </div>


</div>
