<#macro body>

   <#if templateConfig.rows?exists>   

      <table border="0" cellpadding="0" cellspacing="0" <#if templateConfig.width?exists && templateConfig.width?length &gt; 0> width="${templateConfig.width}%"</#if> >

      <#list templateConfig.rows as row>
      
         <tr id="${row.id}">
         
         <td>
         
            <table width="100%" cellspacing="0" cellpadding="0">

            <tr valign="top">

            <#list row.panels as col>
         
               <td id="${col.id}" width="${col.width}%" style="vertical-align: top">

                  <#if col.regions?exists>
	             <#list col.regions as r>
	          
	          <div id="${r.name}" <#if r.height?exists && r.height?length &gt; 0>height="${r.height}%"</#if> >
                     <@region id="${r.name}" scope="${r.scope}"/>
                  </div>
                  
		     </#list>
		  </#if>
	       </td>
	       
            </#list>
         
         </tr>
         
         </table>
         
         </td>
         
         </tr>
      
      </#list>
      
      </table>

   </#if>

</#macro>
