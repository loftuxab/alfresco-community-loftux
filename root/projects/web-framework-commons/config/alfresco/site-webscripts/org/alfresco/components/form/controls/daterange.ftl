<#assign viewFormat>${msg("form.control.date-picker.view.date.format")}</#assign>

<div class="form-field">
   <#assign controlId = fieldHtmlId + "-cntrl">
   
   <script type="text/javascript">//<![CDATA[
   (function()
   {
      new Alfresco.DateRange("${controlId}", "${fieldHtmlId}").setMessages(
         ${messages}
      );
   })();
   //]]></script>
   
   <label for="${controlId}-date">${field.label?html}:</label>
   
   <input id="${fieldHtmlId}" type="hidden" name="${field.name}-daterange" value="" />
   
   <div id="${controlId}" style="width:16em">
      <div class="yui-gc">
         <div class="yui-u first">
            <span>${msg("form.control.date-range.from")}:</span>
            <div style="width:12em">
               <#-- from date -->
               <input id="${controlId}-date-from" name="-" type="text" class="date-entry" <#if field.description??>title="${field.description}"</#if> tabindex="0" />
               <a id="${controlId}-icon-from"><img src="${url.context}/components/form/images/calendar.png" class="datepicker-icon"/></a>
               <div id="${controlId}-from" class="datepicker"></div>
               
               <div class="format-info">
                  <span class="date-format">${msg("form.control.date-picker.display.date.format")}</span>
               </div>
            </div>
         </div>
         <div class="yui-u">
            <span>${msg("form.control.date-range.to")}:</span>
            <div style="width:12em">
               <#-- to date -->
               <input id="${controlId}-date-to" name="-" type="text" class="date-entry" <#if field.description??>title="${field.description}"</#if> tabindex="0" />
               <a id="${controlId}-icon-to"><img src="${url.context}/components/form/images/calendar.png" class="datepicker-icon"/></a>
               <div id="${controlId}-to" class="datepicker"></div>
               
               <div class="format-info">
                  <span class="date-format">${msg("form.control.date-picker.display.date.format")}</span>
               </div>
            </div>
         </div>
      </div>
   </div>
   
</div>