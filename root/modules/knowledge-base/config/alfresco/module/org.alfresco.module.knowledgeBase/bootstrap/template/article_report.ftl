<#assign currentpath="+PATH:\"${companyhome.qnamePath}//*\"">
<#assign datetimeformat="dd MMM yyyy HH:mm">
<#assign currentdate=date?string("yyyy-MM-dd'T'HH:mm:ss'.00Z'")><#assign date_lteq="[\"1970-01-01T00:00:00.00Z\" TO \"${currentdate}\"]">

<#macro debug errcat="" errmsg="">

  <!-- <tr><th colspan="2">${errcat}</th><td colspan="7">${errmsg}</td></tr> -->

</#macro>

<script>

function openStartWorkflow(displayPath, childName) {

  var w=500;
  var h=50;
  var url="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/start_workflow.ftl&contextPath=" + displayPath + "/" + childName;
  
  // Fudge factors for window decoration space.
  // In my tests these work well on all platforms & browsers.
  w += 32;
  h += 96;
  wleft = (screen.width - w) / 2;
  wtop = (screen.height - h) / 2;
  var win = window.open(url,
    name,
    'width=' + w + ', height=' + h + ', ' +
    'left=' + wleft + ', top=' + wtop + ', ' +
    'location=no, menubar=no, ' +
    'status=no, toolbar=no, scrollbars=no, resizable=no, titlebar=no');
  // Just in case width and height are ignored
  win.resizeTo(w, h);
  // Just in case left and top are ignored
  win.moveTo(wleft, wtop);
  win.focus();
}


<#-- Used by start_workflow.ftl validate selection as start workflow via server js -->
function reportcheckWFForm(nodeid, returnid) {

	alert('in checkwfform nodeid = ' + nodeid);
	alert('in checkwfform returnid = ' + returnid);
	reviewer = document.getElementById("workflowAssignee").value;

    if (document.getElementById("workflowAssignee").options[0].selected == true)	{
    	alert("Please select a reviewer");
    	//return false;
    	}
    else {		
    			http.open("post", "/alfresco/command/script/execute?scriptPath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/script/start_workflow.js&workflowAssignee=" + reviewer + "&nodeid=" + nodeid + "&returnid=" + returnid);
    			http.onreadystatechange = handleResponse;
		    	http.send(null);
				
				//window.location.replace("/alfresco/faces/jsp/dashboards/container.jsp")
				
   				//return true;
   	}

}

<#-- Used by start_workflow.ftl to clear selection -->
function reportCancelWFForm(nodeid,returnid) {

	document.getElementById(returnid).innerHTML = "<a href=\"javascript:reportsndWFReq('" + nodeid + "','" + returnid + "');\">Request Approval</a>";

}

<#-- AJAX Request to display start workflow selector inline -->
function reportsndWFReq(nodeid) {
	    
	    http.open("post", "/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/start_report_workflow.ftl&returnid=report" + nodeid + "&nodeid=" + nodeid);
    	http.onreadystatechange = handleResponse;
    	http.send(null);
}    

</script>


<#-- Display Standard Headers -->
<#macro standardHeaders title extra="">

    <tr><td colspan="8"><h3>${title}</h3></td></tr>

    <tr Style="font-size:130%;font-weight:bold;color:#0000FF;">
        
        <td>ASK ID</td>

        <td>Title</td>

        <td>Modified By</td>

        <td>${extra}</td>
        
        <td>Status</td>
        
        <td>Action</td>

    </tr>

</#macro>

<!-- Display Properties for returned objects -->
<#macro standardProperties child extraProperty="">

    <!-- Set up workspace path to child and it's associated parent and file plan -->

    <#assign childRef=child.nodeRef>

    <#assign childWorkspace=childRef[0..childRef?index_of("://")-1]>

    <#assign childStorenode=childRef[childRef?index_of("://")+3..]>

    <#assign childPath="${childWorkspace}/${childStorenode}">

    <tr valign="top" >
    
    <td>
    	<a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/article.ftl&contextPath=${child.displayPath}/${child.name}" target="new">
    	<#if child.properties["ask:askid"]?exists>${child.properties["ask:askid"]}</#if></a>
    </td>

    <td> <#-- Article title -->

        <a href="/alfresco/navigate/showDocDetails/${childPath}">
    	    <img src="/alfresco/images/icons/View_details.gif" border=0 align=absmiddle alt="Article Details" title="Article Details"></a>

 		<a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/article.ftl&contextPath=${child.displayPath}/${child.name}" target="new">
        <img src="/alfresco${child.icon16}" width=16 height=16 border=0 align=absmiddle alt="View Article" title="View Article">
        <#if child.properties["cm:title"]?exists>${child.properties["cm:title"]}<#else>${child.properties.name}</#if></a>

    </td>


    <td> <#-- Article Modifier -->

        ${child.properties.modifier}

    </td>
    
    <td>

        ${extraProperty}

    </td>
    
    <td>
    
    	${child.properties["ask:status"]}
    
    </td>

	<#-- Add start workflow menu for draft articles -->
    <td><div id="report${child.id}">

    	<#if child.properties["ask:status"] == "Draft">
    		<!-- <a href="javascript:openStartWorkflow('${child.displayPath}','${child.name}');">Request Approval</a> -->
    		<!--<input type="button" value="Send Approval" onclick="javascript:sndWFReq('${child.id}');">-->
    		<a href="javascript:reportsndWFReq('${child.id}');">Request Approval</a>
    		
    	</#if>
		</div>
    </td>


    </tr>

</#macro>

<!-- Display Standard Footers -->
<#macro standardFooters>

    <tr><td colspan="10"><hr/></td></tr>

    <tr><td colspan="10"></td></tr>

</#macro>

<!-- Display Recent Changes -->
<table width="100%"  border="0" cellpadding="1" cellspacing="1">

<@standardHeaders title="Recent Article Changes" extra="Modified"/>

<#assign query="${currentpath} +ASPECT:\"{ask.new.model}article\" +@cm\\:modified:${date_lteq}">

<@debug errcat="QUERY" errmsg=query/>

<#list companyhome.childrenByLuceneSearch[query]?sort_by(['properties', 'modified'])?reverse as child>

    <#if (dateCompare(child.properties["cm:modified"], date, 1000*60*60*24*7) == 1) || (dateCompare(child.properties["cm:created"], date, 1000*60*60*24*7) == 1)>

        <@standardProperties child=child extraProperty=child.properties.modified?string(datetimeformat) />

    </#if>

</#list>

<@standardFooters/>


</table>
