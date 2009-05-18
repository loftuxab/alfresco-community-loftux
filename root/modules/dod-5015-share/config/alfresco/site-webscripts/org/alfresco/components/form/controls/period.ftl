<#assign period="|">
<#if field.value?exists><#assign period=field.value></#if>
<#assign sepIdx=period?index_of("|")>
<#assign when=period?substring(0, sepIdx)>
<#assign frequency=period?substring(sepIdx+1)?number>

<#if form.mode == "view">
<div class="viewmode-field">
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value"><#if when != "immediately" && when != "none">Every</#if><#if frequency &gt; 1> ${frequency?c}</#if> ${getWhenLabel("${when}")}<#if frequency &gt; 1>s</#if></span>
</div>
<#else>
<label for="${fieldHtmlId}-when">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
<input id="${fieldHtmlId}" name="${field.name}" type="hidden" value="${field.value}"/>
<div id="${fieldHtmlId}-container" class="period">
   <#assign periodVar=fieldHtmlId?replace("-", "_")>
   <select id="${fieldHtmlId}-when" name="-" onchange="javascript:${periodVar}_changed();">
      <@whenOption val="fyend" />
      <@whenOption val="immediately" />
      <@whenOption val="month" />
      <@whenOption val="monthend" />
      <@whenOption val="none" />
      <@whenOption val="quarterend" />
      <@whenOption val="week" />
      <@whenOption val="year" />
      <@whenOption val="yearend" />
   </select>
   <span>Frequency</span><input id="${fieldHtmlId}-freq" name="-" type="text" value="${frequency}" onkeyup="javascript:${periodVar}_changed();" />
</div>

<script type="text/javascript">//<![CDATA[
   function ${periodVar}_changed(event)
   {
      var when = YAHOO.util.Dom.get("${fieldHtmlId}-when").value;
      var freq = YAHOO.util.Dom.get("${fieldHtmlId}-freq").value;
      
      if (freq.length == 0)
      {
         freq = 0;
      }
      
      if (when == "immediately" || when == "none")
      {
         freq = 0;
         YAHOO.util.Dom.get("${fieldHtmlId}-freq").value = 0;
      }
      
      if (!isNaN(freq))
      {
         YAHOO.util.Dom.get("${fieldHtmlId}").value = when + "|" + freq;
      }
   }
//]]></script>

</#if>

<#function getWhenLabel val>
   <#if val=="immediately">
      <#return "Immediately">
   <#elseif val=="none">
      <#return "None">
   <#elseif val=="fyend">
      <#return "Financial Year End">
   <#elseif val=="month">
      <#return "Month">
   <#elseif val=="monthend">
      <#return "Month End">
   <#elseif val=="quarterend">
      <#return "Quarter End">
   <#elseif val=="week">
      <#return "Week">
   <#elseif val=="year">
      <#return "Year">
   <#elseif val=="yearend">
      <#return "Year End">
   <#else>
      <#return "Unknown">
   </#if>
</#function>

<#macro whenOption val>
   <option value="${val}"<#if when==val> selected="selected"</#if>>${getWhenLabel("${val}")}</option>
</#macro>
              