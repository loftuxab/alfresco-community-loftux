<#macro body>

   <#if templateConfig.rows?exists>   

      <table border="0" cellpadding="0" cellspacing="0">

      <#list templateConfig.rows as row>
      
         <tr id="${row.id}" height="${row.height}" style="height: ${row.height}">
         
         <#list row.panels as col>
         
            <td id="${col.id}" height="${col.height}" width="${col.width}" style="height: ${col.height}; width: ${col.width}; vertical-align: top">
            
            	<#if col.regions?exists>
			<#list col.regions as r>

				<@region id="${r.name}" scope="${r.scope}" />

			</#list>
		</#if>
            
            </td>
            
         </#list>
         
         </tr>
      
      </#list>
      
      </table>

   </#if>

</#macro>
