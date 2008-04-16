<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<table>
	<tbody>
		<tr>
			<th>results</th>
		</tr>

<#if resultset?exists && resultset?size &gt; 0>		
<#list resultset as doc>
	<#assign doc_url = doc.url>
	<#assign doc_path_temp  = doc_url?substring(doc_url?index_of("ROOT;")+4)>
	<#assign doc_path_temp2 = doc_path_temp?substring(0,doc_path_temp?index_of("/"))>
	<#assign file_path = doc_path_temp2?replace(";","/")>
	<#if file_path?ends_with(".xml")>


		<tr>
		       <td>${file_path}</td>
                </tr>

                
       	</#if>
</#list>
</#if>


	</tbody>
</table>