<#if field.control.params.rows?exists><#assign rows=field.control.params.rows><#else><#assign rows=8></#if>
<#if field.control.params.columns?exists><#assign columns=field.control.params.columns><#else><#assign columns=60></#if>
<#if field.control.params.editorAppearance?exists><#assign appearance=field.control.params.editorAppearance><#else><#assign appearance="default"></#if>
<#if field.control.params.editorHeight?exists><#assign height=field.control.params.editorHeight><#else><#assign height=100></#if>
<#if field.control.params.editorWidth?exists><#assign width=field.control.params.editorWidth><#else><#assign width=400></#if>
<#-- TODO: allow the button editor parameters to be configured -->

<#-- NOTE: content properties are not shown at all in view mode -->

<#if form.mode != "view">
<div class="form-field" id="${fieldHtmlId}-field">
   <script type="text/javascript">//<![CDATA[
   (function()
   {
      new Alfresco.RichTextControl("${fieldHtmlId}").setOptions(
      {
         <#if field.disabled>disabled: true,</#if>
         currentValue: "${field.value}",
         mandatory: ${field.mandatory?string},
         contentProperty: true,
         formMode: "${form.mode}",
         nodeRef: "${context.properties.nodeRef!""}",
         mimeType: "${context.properties.mimeType!""}",
         <#if field.control.params.plainMimeTypes??>plainMimeTypes: "${field.control.params.plainMimeTypes}",</#if>
         <#if field.control.params.richMimeTypes??>richMimeTypes: "${field.control.params.richMimeTypes}",</#if>
         <#if field.control.params.forceEditor??>forceEditor: ${field.control.params.forceEditor},</#if>
         editorParameters:
         {
            height: ${height},
            width: ${width},
            theme: 'advanced',
         <#if appearance == "full"> 
            theme_advanced_buttons1: "bold,italic,underline,strikethrough,separator,fontselect,fontsizeselect",
            theme_advanced_buttons2: "link,unlink,image,separator,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,separator,undo,redo,separator,forecolor,backcolor",
            theme_advanced_buttons3: null,
         <#elseif appearance == "plugins">
            theme_advanced_buttons1: "bold,italic,underline,strikethrough,separator,fontselect,fontsizeselect",
            theme_advanced_buttons2: "link,unlink,image,separator,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,separator,undo,redo,separator,forecolor,backcolor",
            plugins:"fullscreen,table,emotions",
            theme_advanced_buttons3: "fullscreen,table,emotions",
         <#elseif appearance == "explorer">
            plugins: "table",
            theme_advanced_buttons1_add: "fontselect,fontsizeselect",
            theme_advanced_buttons2_add: "separator,forecolor,backcolor",
            theme_advanced_buttons3_add_before: "tablecontrols,separator",
            theme_advanced_disable: "styleselect",
            extended_valid_elements: "a[href|target|name],font[face|size|color|style],span[class|align|style]",
         <#else>
            theme_advanced_buttons1: "bold,italic,underline,|,bullist,numlist,|,forecolor,backcolor,|,undo,redo,removeformat",
            theme_advanced_buttons2: null,
            theme_advanced_buttons3: null,
         </#if>
            theme_advanced_toolbar_location: "top",
            theme_advanced_toolbar_align: "left",
            theme_advanced_statusbar_location: "bottom",
            theme_advanced_resizing: true,
            theme_advanced_path: false
         }
      }).setMessages(
         ${messages}
      );
   })();
   //]]></script>
   
   <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
   <textarea id="${fieldHtmlId}" name="${field.name}" rows="${rows}" columns="${columns}"
             <#if field.description?exists>title="${field.description}"</#if>
             <#if field.control.params.styleClass?exists>class="${field.control.params.styleClass}"</#if>
             <#if field.disabled>disabled="true"</#if>></textarea>
</div>
</#if>