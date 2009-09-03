<#assign controlId = args.htmlid + "-cntrl">
<#assign pickerId = controlId + "-picker">

<div id="${pickerId}" class="picker document-picker">
   <div id="${pickerId}-head" class="hd">${msg("label.document-picker-header")}</div>

   <div id="${pickerId}-body" class="bd">
      <div class="yui-g">
         <div id="${pickerId}-left" class="yui-u first panel-left">
            <div class="picker-header">
               <div class="folder-up"><button id="${pickerId}-folderUp"></button></div>
               <div class="navigator">
                  <button id="${pickerId}-navigator"></button>
                  <div id="${pickerId}-navigatorMenu" class="yuimenu">
                     <div class="bd">
                        <ul id="${pickerId}-navigatorItems" class="navigator-items-list">
                           <li>&nbsp;</li>
                        </ul>
                     </div>
                  </div>
               </div>
            </div>
            <div id="${pickerId}-results" class="picker-items">
               
            </div>
         </div>
         <div id="${pickerId}-right" class="yui-u panel-right">
            <div id="${pickerId}-selectedHeader" class="picker-header"></div>
            <div id="${pickerId}-selectedItems" class="picker-items"></div>
         </div>
      </div>
      <div class="bdft">
         <button id="${controlId}-ok" tabindex="4" disabled>${msg("button.ok")}</button>
         <button id="${controlId}-cancel" tabindex="5">${msg("button.cancel")}</button>
      </div>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.DocumentPicker");
//]]></script>
