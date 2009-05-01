<#-- TODO: Allow other content properties to be used, configure via params -->
<#-- <#assign content=form.data["prop_cm_content"]> -->
<#-- <#assign sizeBegIdx=content?index_of("size=")+5> -->
<#-- <#assign sizeEndIdx=content?index_of("|", sizeBegIdx)> -->
<#-- <#assign size=content?substring(sizeBegIdx, sizeEndIdx)> -->
<#assign size=field.value>

<div class="viewmode-field">
   <span class="viewmode-label">${msg("form.control.size.label")}:</span>
   <span id="${args.htmlid}_${field.id}" class="viewmode-value"></span>
</div>

<script type="text/javascript">//<![CDATA[
YAHOO.util.Dom.get("${args.htmlid}_${field.id}").innerHTML = Alfresco.util.formatFileSize(${size});
//]]></script>
