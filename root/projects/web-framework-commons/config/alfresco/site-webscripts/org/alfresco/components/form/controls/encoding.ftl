<#-- TODO: Allow other content properties to be used, configure via params -->
<#-- <#assign content=form.data["prop_cm_content"]> -->
<#-- <#assign mtBegIdx=content?index_of("encoding=")+9> -->
<#-- <#assign mtEndIdx=content?index_of("|", mtBegIdx)> -->
<#-- <#assign encoding=content?substring(mtBegIdx, mtEndIdx)> -->
<#assign encoding=field.value>

<#if form.mode == "view">
<div class="viewmode-field">
   <span class="viewmode-label">${msg("form.control.encoding.label")}:</span>
   <span class="viewmode-value">${getEncodingLabel("${encoding}")}</span>
</div>
<#else>
<label for="${fieldHtmlId}">${msg("form.control.encoding.label")}:</label>
<#-- TODO: Make this control make an AJAX callback to get list of encodings OR use dataTypeParamters structure -->
<select id="${fieldHtmlId}" name="${field.name}" 
        <#if field.control.params.styleClass?exists>class="${field.control.params.styleClass}"</#if>>
   <option value="">${msg("form.control.encoding.unknown")}</option>
   <@encodingOption enc="ISO-8859-1" />
   <@encodingOption enc="MacRoman" />
   <@encodingOption enc="Shift_JIS" />
   <@encodingOption enc="US-ASCII" />
   <@encodingOption enc="UTF-8" />
   <@encodingOption enc="UTF-16" />
   <@encodingOption enc="UTF-32" />
</select>
</#if>

<#function getEncodingLabel enc>
   <#if enc=="UTF-8">
      <#return "UTF-8">
   <#elseif enc=="UTF-16">
      <#return "UTF-16">
   <#elseif enc=="UTF-32">
      <#return "UTF-32">
   <#elseif enc=="ISO-8859-1">
      <#return "ISO-8859-1">
   <#elseif enc=="US-ASCII">
      <#return "US-ASCII">
   <#elseif enc=="MacRoman">
      <#return "MacRoman">
   <#elseif enc=="Shift_JIS">
      <#return "Shift_JIS">
   <#else>
      <#return msg("form.control.encoding.unknown")>
   </#if>
</#function>

<#macro encodingOption enc>
   <option value="${enc}"<#if encoding==enc> selected="selected"</#if>>${getEncodingLabel("${enc}")}</option>
</#macro>
              