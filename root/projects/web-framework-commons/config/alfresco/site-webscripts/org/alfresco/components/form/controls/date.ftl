
<#-- TODO: Try and find a way to share as much as possible between all instances of control on page -->

<#if field.control.params.showTime?exists && field.control.params.showTime == "true"><#assign showTime=true><#else><#assign showTime=false></#if>
<#if showTime><#assign viewFormat>${msg("form.view.time.format")}</#assign><#else><#assign viewFormat>${msg("form.view.date.format")}</#assign></#if>

<#assign dpId=args.htmlid + "_" + field.id>
<#assign dpVar=dpId?replace("-", "_")>

<#if form.mode == "view">
<div class="viewmode-field">
   <#if field.mandatory && field.value == "">
      <span class="incomplete-warning"><img src="${url.context}/components/form/images/warning-16.png" title="${msg("form.incomplete.field")}" /><span>
   </#if>
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value"><#if field.value != "">${field.value?datetime("yyyy-MM-dd'T'HH:mm:ss")?string(viewFormat)}</#if></span>
</div>
<#else>

<label for="${dpId}-date">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
<input id="${dpId}" type="hidden" name="${field.name}" value="${field.value}" />
<input id="${dpId}-date" type="text" class="date-entry" <#if field.description?exists>title="${field.description}"</#if> <#if field.disabled>disabled="true"</#if> />

<#if field.disabled == false>
<a id="${dpId}-icon" href="javascript:${dpVar}_cal.show();"><img src="${url.context}/components/form/images/calendar.png" class="datepicker-icon"/></a>
</#if>

<div id="${dpId}-cal" class="datepicker"></div>

<#if showTime>
<input id="${dpId}-time" type="text" class="time-entry" <#if field.description?exists>title="${field.description}"</#if> <#if field.disabled>disabled="true"</#if> />
</#if>

<div class="format-info">
<span class="date-format">${msg("form.display.date.format")}</span>
<#if showTime><span class="time-format<#if field.disabled>-disabled</#if>">${msg("form.display.time.format")}</span></#if>
</div>

<script type="text/javascript">//<![CDATA[
   
   function ${dpVar}_selHandler(type,args,obj) 
   {
      var selected = args[0];
      var selDate = this.toDate(selected[0]);
      var dateEntry = selDate.toString("${msg("form.entry.date.format")}");
      YAHOO.util.Dom.get("${dpId}-date").value = dateEntry;
      
      <#if showTime>
      var time = YAHOO.util.Dom.get("${dpId}-time").value;
      if (time.length > 0)
      {
         var dateTime = YAHOO.util.Dom.get("${dpId}-date").value + " " + time;
         var dateTimePattern = "${msg("form.entry.date.format")}" + " " + "${msg("form.entry.time.format")}";
         selDate = Date.parseExact(dateTime, dateTimePattern);
      }
      </#if>
      
      if (selDate != null)
      {
         YAHOO.util.Dom.get("${dpId}").value = Alfresco.util.toISO8601(selDate, {"milliseconds":true});
      }
      else
      {
         alert("${msg("form.invalid.time")}");
      }
      
      // hide the popup calendar
      ${dpVar}_cal.hide();
   };
   
   function ${dpVar}_changed(event)
   {
      // update calendar control
      var changedDate = YAHOO.util.Dom.get("${dpId}-date").value;
      if (changedDate.length > 0)
      {
         // convert to format expected by YUI
         var parsedDate = Date.parseExact(changedDate, "${msg("form.entry.date.format")}");
         if (parsedDate != null)
         {
            ${dpVar}_cal.select((parsedDate.getMonth() + 1) + "/" + parsedDate.getDate() + "/" + parsedDate.getFullYear());
            var selectedDates = ${dpVar}_cal.getSelectedDates();
            if (selectedDates.length > 0)
            {
               var firstDate = selectedDates[0];
               ${dpVar}_cal.cfg.setProperty("pagedate", (firstDate.getMonth()+1) + "/" + firstDate.getFullYear());
               ${dpVar}_cal.render();
            }
         }
         else
         {
            alert("${msg("form.invalid.date")}");
         }
      }
   };

   // setup data
   var ${dpVar}_date = Alfresco.util.fromISO8601("${field.value}");
   var ${dpVar}_page = (${dpVar}_date.getMonth() + 1) + "/" + ${dpVar}_date.getFullYear();
   var ${dpVar}_sel = (${dpVar}_date.getMonth() + 1) + "/" + ${dpVar}_date.getDate() + "/" + ${dpVar}_date.getFullYear();   
   var ${dpVar}_dev = ${dpVar}_date.toString("${msg("form.entry.date.format")}");
   var ${dpVar}_tev = ${dpVar}_date.toString("${msg("form.entry.time.format")}");
   <#if field.value?exists && field.value != "">
   YAHOO.util.Dom.get("${dpId}-date").value = ${dpVar}_dev;
      <#if showTime>
      YAHOO.util.Dom.get("${dpId}-time").value = ${dpVar}_tev;
      </#if>
   </#if>
   
   // setup calendar control
   var ${dpVar}_cal = new YAHOO.widget.Calendar("${dpId}-cal", "${dpId}-cal", { title:"Choose a date:", close:true });
   ${dpVar}_cal.cfg.setProperty("pagedate", ${dpVar}_page);
   ${dpVar}_cal.cfg.setProperty("selected", ${dpVar}_sel);
   
   // setup events
   ${dpVar}_cal.selectEvent.subscribe(${dpVar}_selHandler, ${dpVar}_cal, true);
   YAHOO.util.Event.addListener("${dpId}-date", "change", ${dpVar}_changed);
   YAHOO.util.Event.addListener("${dpId}-time", "change", ${dpVar}_changed);
   
   // render the calendar control
   ${dpVar}_cal.render();
//]]></script>

</#if>
