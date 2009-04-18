<div class="viewmode-field">
   <span class="viewmode-label">${msg("form.control.size.label")}:</span>
   <span id="${args.htmlid}_${field.id}" class="viewmode-value"></span>
</div>

<script type="text/javascript">//<![CDATA[
YAHOO.util.Dom.get("${args.htmlid}_${field.id}").innerHTML = Alfresco.util.formatFileSize(${field.value});
//]]></script>
