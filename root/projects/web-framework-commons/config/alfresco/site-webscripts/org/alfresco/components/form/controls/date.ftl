<!-- determine if the time should be shown too -->

<#assign dpId=args.htmlid + "_" + field.id>
<#assign dpVar=dpId?replace("-", "_")>

<#if form.mode == "view">
<div class="viewmode-field">
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value"><#if field.value != "">${field.value?datetime("yyyy-mm-dd'T'HH:MM:ss")?string("EEEE, MMMM dd yyyy")}</#if></span>
</div>
<#else>

<label for="${dpId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">*</span></#if></label>
<input id="${dpId}" type="hidden" name="${field.name}" value="${field.value}" />
<input id="${dpId}-entry" type="text" <#if field.description?exists>title="${field.description}"</#if> <#if field.disabled>disabled="true"</#if> />

<#if field.disabled == false>
<a id="${dpId}-icon" class="datepicker-icon" href="javascript:${dpVar}_cal.show();"><img src="${url.context}/components/form/images/calendar.png" /></a>
</#if>
<div id="${dpId}-cal" class="datepicker"></div>

<script type="text/javascript">//<![CDATA[
   function ${dpVar}_selHandler(type,args,obj) 
   {
      var selected = args[0];
      var selDate = this.toDate(selected[0]);
      var entry = (selDate.getMonth() + 1) + "/" + selDate.getDate() + "/" + selDate.getFullYear();
      YAHOO.util.Dom.get("${dpId}").value = Alfresco.util.toISO8601(selDate);
      YAHOO.util.Dom.get("${dpId}-entry").value = entry;
      ${dpVar}_cal.hide();
   };
   
   function ${dpVar}_changed(event)
   {
      var changedDate = YAHOO.util.Dom.get("${dpId}-entry").value;
      if (changedDate.length > 0)
      {
         ${dpVar}_cal.select(changedDate);
         var selectedDates = ${dpVar}_cal.getSelectedDates();
         if (selectedDates.length > 0)
         {
            var firstDate = selectedDates[0];
            YAHOO.util.Dom.get("${dpId}").value = Alfresco.util.toISO8601(firstDate);
            ${dpVar}_cal.cfg.setProperty("pagedate", (firstDate.getMonth()+1) + "/" + firstDate.getFullYear());
            ${dpVar}_cal.render();
         }
      }
      else
      {
         YAHOO.util.Dom.get("${dpId}").value = "";
      }
   };

   // setup data
   var ${dpVar}_date = Alfresco.util.fromISO8601("${field.value}");
   var ${dpVar}_page = (${dpVar}_date.getMonth() + 1) + "/" + ${dpVar}_date.getFullYear();
   var ${dpVar}_sel = (${dpVar}_date.getMonth() + 1) + "/" + ${dpVar}_date.getDate() + "/" + ${dpVar}_date.getFullYear();
   <#if field.value?exists>
   YAHOO.util.Dom.get("${dpId}-entry").value = ${dpVar}_sel;
   </#if>
   
   // setup calendar control
   var ${dpVar}_cal = new YAHOO.widget.Calendar("${dpId}-cal", "${dpId}-cal", { title:"Choose a date:", close:true });
   ${dpVar}_cal.cfg.setProperty("pagedate", ${dpVar}_page);
   ${dpVar}_cal.cfg.setProperty("selected", ${dpVar}_sel);
   
   // setup events
   ${dpVar}_cal.selectEvent.subscribe(${dpVar}_selHandler, ${dpVar}_cal, true);
   YAHOO.util.Event.addListener("${dpId}-entry", "change", ${dpVar}_changed);
   
   // render the calendar control
   ${dpVar}_cal.render();
//]]></script>

</#if>
