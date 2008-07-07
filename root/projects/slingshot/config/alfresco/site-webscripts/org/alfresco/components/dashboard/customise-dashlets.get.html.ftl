<#--
      Note!

      This component uses key events. The component listens to key events for a
      specific element that must have focus to trigger events.
      Its possible to listen for global events, ie key events for the document
      but since several key listening components might live on the same page
      that can't be done.

      The browser gives focus to links or form elements, since the dashlets
      are represented by "li"-tags they will not get focus. To achieve this
      anyway a non visible "a"-tag is placed in each "li"-tag so we
      can get focus and thereafter listen to individual keyevents.

      Inside the a element is a transparent gif width width ant height of 100%
      to make the browsers focus indication borders go around the whole dashlet.

      Since the cursor is changed using css slectors on the currently selected
      element a div is in front of both the a element and the image is a div,
      to make sure it bvecomes the selected element.

   -->
<div id="${args.htmlid}" class="customise-dashlets">

   <script type="text/javascript">//<![CDATA[
   new Alfresco.CustomiseDashlets("${args.htmlid}").setMessages(
      ${messages}
   ).setOptions({noOfColumns: ${noOfColumns}});
   //]]></script>



   <div id="${args.htmlid}-instructions-div" class="instructions">

      <h2>${msg("header.dashlets")}</h2>
      <hr/>
      
      <div>
         <div class="text">${msg("label.instructions")}</div>
         <div class="buttons" id="${args.htmlid}-toggleDashletsButtonWrapper-div">
            <input id="${args.htmlid}-toggleDashlets-button" type="button" value="${msg("button.showDashlets")}" />
         </div>
      </div>

   </div>

   <div id="${args.htmlid}-available-div" class="available" style="display: none;">

      <div>
         <div class="text">
            <a class="closeLink" href="javascript: var nothing;" id="${args.htmlid}-closeAddDashlets-link">${msg("link.close")}</a>
            <h3>${msg("section.addDashlets")}</h3>
         </div>
         <ul id="${args.htmlid}-column-ul-0" class="availableList">
            <#list dashlets as dashlet>
               <li class="customisableDashlet available" id="${args.htmlid}-dashlet-li-0-${dashlet_index + 1}">
                  <a href="#"><img class="draggable" src="${url.context}/yui/assets/skins/default/transparent.gif"></a>
                  <span dashletId="${dashlet.id}">${dashlet.shortName}</span>
                  <div class="draggable" title="${dashlet.description}"></div>
               </li>
            </#list>
         </ul>
      </div>

   </div>

   <div class="used">

      <div id="${args.htmlid}-wrapper-div" class="noOfColumns${noOfColumns}">

         <#list columns as column>
            <div class="column" id="${args.htmlid}-column-div-${column_index + 1}" <#if (column_index >= noOfColumns)>style="display: none;"</#if>>
               <h3>Column ${column_index + 1}</h3>
               <ul id="${args.htmlid}-column-ul-${column_index + 1}" class="usedList">
                  <#list column as dashlet>
                     <li class="customisableDashlet used" id="${args.htmlid}-dashlet-li-${column_index + 1}-${dashlet_index + 1}">
                        <a href="#"><img class="draggable" src="${url.context}/yui/assets/skins/default/transparent.gif"></a>
                        <span dashletId="${dashlet.id}">${dashlet.shortName}</span>
                        <div class="draggable" title="${dashlet.description}"></div>
                     </li>
                  </#list>
               </ul>
            </div>
         </#list>

      </div>

   </div>

   <div class="actions">
      
      <hr/>
      <div>
         <div class="buttons">
            <input id="${args.htmlid}-done-button" type="button" value="${msg("button.done")}" />
            <input id="${args.htmlid}-cancel-button" type="button" value="${msg("button.cancel")}" />
         </div>
      </div>
   </div>


   <div style="display: none;">
      <!-- The shadow dashlet that is used during drag n drop to "make space" for the dragged dashlet -->
      <li class="shadow" id="${args.htmlid}-dashlet-li-shadow"></li>
   </div>

</div>
