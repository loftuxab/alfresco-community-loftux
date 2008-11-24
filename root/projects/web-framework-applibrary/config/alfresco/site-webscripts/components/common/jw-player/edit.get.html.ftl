<#import "/components/include/component-editor.ftl" as editor />

<table class="AlfrescoComponentEditor">

  <@editor.properties />
  
  <@editor.spacer />
  
  <@editor.source />
  
  <@editor.spacer />

  <!-- Custom Properties -->
  <tr>
    <td class="label">Preview Image URL</td>
    <td>
    	<input name="${previewImageUrl.id}" value="${previewImageUrl.value}" />
    </td>
  </tr>
</table>
