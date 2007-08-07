<#if args.returnid?exists>${args.returnid}<#else>${args.nodeid}</#if>|

<table style="font-family: Arial, Helvetica, sans-serif;">
	<tr>
		<td valign="top" ></a><select valign="top" name="workflowAssignee" id="workflowAssignee">
				<option selected=SELECTED>Select Reviewer</option>
				<#list companyhome.childrenByLuceneSearch["+TYPE:\"cm:person\""] as person>            
				          <option value="${person.properties["cm:userName"]}">${person.properties["cm:firstName"]} ${person.properties["cm:lastName"]}</option>
				</#list>	
			</select><br/><input type="button" onclick="javascript:searchcheckWFForm('${args.nodeid}','${args.returnid}')" value="Start" title="Start Workflow"/>&nbsp;&nbsp;<input type="button" onclick="javascript:searchCancelWFForm('${args.nodeid}','${args.returnid}');" value="Cancel"/></td>

	</tr>
</table>

