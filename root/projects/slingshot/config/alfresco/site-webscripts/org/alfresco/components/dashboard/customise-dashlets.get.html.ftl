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

      The javascript class expects each "li"-dashlet-tag to have a "a"-tag as
      its first child.
   -->
<div id="${args.htmlid}" class="customise-dashlets">

   <script type="text/javascript">//<![CDATA[
   new Alfresco.CustomiseDashlets("${args.htmlid}").setMessages(
      ${messages}
   );
   //]]></script>



   <div id="${args.htmlid}-instructions-div" class="instructions">

      <h2>${msg("header.dashlets")}</h2>
      <hr/>

      <div>
         <div class="text">${msg("label.instructions")}</div>
         <input id="${args.htmlid}-toggleDashlets-button" type="button" value="${msg("button.showDashlets")}" />
      </div>

   </div>

   <div id="${args.htmlid}-dashlets-div" class="dashlets" style="display: none;">

      <div>
         <ul id="${args.htmlid}-column-ul-0" class="dashletList">
            <#list dashlets as dashlet>
               <li class="dashlet-item" id="${args.htmlid}-dashlet-li-0-${dashlet_index + 1}">
                  <a href="#"><img title="${dashlet.description}" src="${url.context}/yui/assets/skins/default/transparent.gif"></a>
                  <span dashletId="${dashlet.id}">${dashlet.shortName}</span>
               </li>
            </#list>
         </ul>
      </div>

   </div>

   <div class="columns">

      <div id="${args.htmlid}-wrapper-div" class="noOfColumns${columns?size}">

         <#list columns as column>
            <div id="${args.htmlid}-column-div-${column_index + 1}">
               <h3>Column ${column_index + 1}</h3>
               <ul id="${args.htmlid}-column-ul-${column_index + 1}" class="columnList">
                  <#list column as dashlet>
                     <li class="dashlet-item" id="${args.htmlid}-dashlet-li-${column_index + 1}-${dashlet_index + 1}">
                        <a href="#"><img title="${dashlet.description}" src="${url.context}/yui/assets/skins/default/transparent.gif"></a>
                        <span dashletId="${dashlet.id}">${dashlet.shortName}</span>
                     </li>
                  </#list>
               </ul>
            </div>
         </#list>

      </div>

   </div>

   <div id="${args.htmlid}-buttons-div" class="buttons">
      
      <hr/>
      <div>
         <input id="${args.htmlid}-done-button" type="button" value="${msg("button.done")}" />
         <input id="${args.htmlid}-cancel-button" type="button" value="${msg("button.cancel")}" />
      </div>
      
      <#--
      <img id="${args.htmlid}-trashcan-img" src="${url.context}/images/icons/trashcan_large.gif"/>
      -->
   </div>


   <div style="display: none;">
      <!-- The shadow dashlet that is used during drag n drop to "make space" for the dragged dashlet -->
      <li class="dashlet-shadow" id="${args.htmlid}-dashlet-li-shadow"></li>
   </div>

</div>
