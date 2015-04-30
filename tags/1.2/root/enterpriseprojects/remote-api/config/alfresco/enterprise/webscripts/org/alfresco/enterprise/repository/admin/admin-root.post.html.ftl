<!-- DEBUG OUTPUT PAGE -->
<style type="text/css">
   table {
   	font-family: verdana,arial,sans-serif;
   	font-size: 11px;
   	color: #333;
   	border-width: 1px;
   	border-color: #666;
   	border-collapse: collapse;
   }
   table td {
   	border-width: 1px;
   	padding: 4px;
   	border-style: solid;
   	border-color: #666;
   }
</style>

<table>
<#list params as p>
   <tr><td>${p.name?html}</td><td>${p.value?html}</td></tr>
</#list>
</table>