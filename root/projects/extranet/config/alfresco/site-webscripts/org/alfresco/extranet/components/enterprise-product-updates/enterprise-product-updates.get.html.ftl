<style type="text/css">
<!--
table.notifications
{
	background-color: white;
}
table.notifications th {
	padding: 1px 1px 1px 1px;
	-moz-border-radius: 0px 0px 0px 0px;
}
table.notifications td {
	padding: 4px 4px 4px 4px;
	-moz-border-radius: 0px 0px 0px 0px;
}
td.notifications-label
{
	font: bold 12px arial;
}
tr.notifications-rowA {
	background-color: #eeeeff;
}
tr.notifications-rowB {
	background-color: #fafaff;
}
-->
</style>
<table width="100%" class="notifications">
	<tr>
		<td class="notifications-label"></td>
		<td class="notifications-label">ID</td>
		<td class="notifications-label">Summary</td>
		<td class="notifications-label">Date</td>
	</tr>
	
<#assign count = 0>
<#if objects?exists>

<#list objects as object>

	<#assign bgClass = "notifications-rowA">
	<#if count % 2 == 0>
		<#assign bgClass = "notifications-rowB">
	</#if>
	<tr class="${bgClass}">
		<td>
			<img src="${url.context}/images/extranet/info_16.gif"/>
		</td>
		<td nowrap>
			${object.key}
		</td>
		<td>
			<a href="https://issues.alfresco.com/browse/${object.key}" target="_blank">
			${object.summary}
			</a>
		</td>
		<td nowrap>
			${object.created.get(2)}/${object.created.get(5)}
		</td>
	</tr>	
	
	<#assign count = count + 1>

</#list>

</#if>

</table>

<br/>

* There have been 
<a href="https://issues.alfresco.com/secure/IssueNavigator.jspa?mode=hide&requestId=${filterId}" target="_blank">
${totalCount} Enterprise Check-ins
</a>
over the past four days.
 