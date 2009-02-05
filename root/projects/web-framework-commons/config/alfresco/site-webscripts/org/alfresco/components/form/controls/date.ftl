<!-- determine if the time should be shown too -->

<label>${item.label?html}:</label>

<#if form.mode == "view">
<span class="field"><#if item.value != "">${item.value?datetime("yyyy-mm-dd'T'HH:MM:ss")?string("EEEE, MMMM dd yyyy")}</#if></span>
<#else>

<input id="${item.id}" type="hidden" name="${item.name}" value="${item.value}" />
<input id="${item.id}-entry" type="text" <#if item.description?exists>title="${item.description}"</#if> <#if item.protectedField>disabled="true"</#if> />

<#if item.protectedField == false>
<a id="${item.id}-icon" class="datepicker-icon" href="javascript:${item.id}_cal.show();"><img src="${url.context}/components/form/images/calendar.png" /></a>
</#if>
<div id="${item.id}-cal" class="datepicker"></div>

<script type="text/javascript">
   function ${item.id}_selHandler(type,args,obj) 
   {
      var selected = args[0];
      var selDate = this.toDate(selected[0]);
      var entry = (selDate.getMonth() + 1) + "/" + selDate.getDate() + "/" + selDate.getFullYear();
      YAHOO.util.Dom.get("${item.id}").value = Alfresco.util.toISO8601(selDate);
      YAHOO.util.Dom.get("${item.id}-entry").value = entry;
      ${item.id}_cal.hide();
   };
   
   function ${item.id}_changed(event)
   {
      var changedDate = YAHOO.util.Dom.get("${item.id}-entry").value;
      if (changedDate.length > 0)
      {
         ${item.id}_cal.select(changedDate);
         var selectedDates = ${item.id}_cal.getSelectedDates();
         if (selectedDates.length > 0)
         {
            var firstDate = selectedDates[0];
            YAHOO.util.Dom.get("${item.id}").value = Alfresco.util.toISO8601(firstDate);
            ${item.id}_cal.cfg.setProperty("pagedate", (firstDate.getMonth()+1) + "/" + firstDate.getFullYear());
            ${item.id}_cal.render();
         }
      }
      else
      {
         YAHOO.util.Dom.get("${item.id}").value = "";
      }
   };

   // setup data
   var ${item.id}_date = Alfresco.util.fromISO8601("${item.value}");
   var ${item.id}_page = (${item.id}_date.getMonth() + 1) + "/" + ${item.id}_date.getFullYear();
   var ${item.id}_sel = (${item.id}_date.getMonth() + 1) + "/" + ${item.id}_date.getDate() + "/" + ${item.id}_date.getFullYear();
   <#if item.value?exists>
   YAHOO.util.Dom.get("${item.id}-entry").value = ${item.id}_sel;
   </#if>
   
   // setup calendar control
   var ${item.id}_cal = new YAHOO.widget.Calendar("${item.id}-cal", "${item.id}-cal", { title:"Choose a date:", close:true });
   ${item.id}_cal.cfg.setProperty("pagedate", ${item.id}_page);
   ${item.id}_cal.cfg.setProperty("selected", ${item.id}_sel);
   
   // setup events
   ${item.id}_cal.selectEvent.subscribe(${item.id}_selHandler, ${item.id}_cal, true);
   YAHOO.util.Event.addListener("${item.id}-entry", "change", ${item.id}_changed);
   
   // render the calendar control
   ${item.id}_cal.render();
</script>

</#if>
