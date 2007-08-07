<#assign debug="false">
<#if args?exists>
	<#if args.search?exists || args.reset?exists || args.askid?exists> 
searchResults|
		<#if !args.searchText?exists><#assign searchString = ""/><#else><#assign searchString = args.searchText/></#if>
		<#if args.reset?exists>Args: ${args.reset}</#if>
		<#assign articleHome = companyhome.childByNamePath["Data Dictionary/Knowledge Base"]>
	
		<#if debug="true"><p>${articleHome.name}</p></#if>
	
		<#if debug="true">Search String: <#if args.searchText?exists>${args.searchText}</#if><br/></#if>
	
	  <#if args.searchText?exists && args.searchText != "">
			<#assign query="+PATH:\"${articleHome.qnamePath}//.\" +@ask\\:status:\"Current\" +TEXT:\"${searchString}\" +ASPECT:\"{ask.new.model}article\"">
		<#else>
			<#assign query="+PATH:\"${articleHome.qnamePath}//.\" +@ask\\:status:\"Current\" +ASPECT:\"{ask.new.model}article\"">
		</#if>
	
		<#if args.type?exists>
			<#if debug="true">Type: ${args.type}<br/></#if>
		  <#if args.type!="Any">
		    <#assign query=query + " +@ask\\:article_type:\"${args.type}\"">
		  </#if>
		</#if>
	
		<#if args.askid?exists>
		  <#if debug="true">ASK ID: ${args.askid}<br/></#if>
		</#if>
		


		<#if args.categorylist?exists>
			<#if debug="true">Category List: ${args.categorylist}<br/></#if>
				  <#if args.categorylist!="Any">
				  
				    <#-- Need to split the list and process each in turn -->
				    <#assign catlist = args.categorylist>

				  	<#list catlist?split(",") as currentcategory>
					  	<#assign theCategory="${'${currentcategory}'?replace(':','\\\\:')}">
							<#assign theCategory="${'${theCategory}'?replace('/','\\\\/')}">
							<#assign categoryQuery="ID:" + "${theCategory}">
							<!--<br/>Category Query: ${categoryQuery}<br/>-->
							<#-- Get the category node by id -->
							<#-- CANT USE nodeByReference -->
							<#-- BUG IN nodeByReference, ITS RETURNING THE WRONG ID -->
					    <#assign categoryNode = companyhome.childrenByLuceneSearch["${categoryQuery}"][0]>
					    <!--<br/>Lucense category: ${categoryNode.qnamePath}<br/>-->
					    <#if categoryNode?exists>
					    	<#assign query=query + " +PATH:\"${categoryNode.qnamePath}//member\"">					    	
					    </#if>
				    </#list> 

				</#if>
				
		</#if>
		
				
		<#if args.modified?exists>
			<#if args.modified != "">
				<#if debug="true">Modified: ${args.modified}<br/></#if>
				  <#assign date1 = "${args.modified}"?date("dd/MM/yyyy")?string("yyyy-MM-dd'T'HH:mm:ss'.00Z'")>
				  <#assign fromDate="${'${date1}'?replace('-','\\\\-')}">
					<#assign currentdate=date?string("yyyy\\-MM\\-dd'T'HH:mm:ss'.00Z'")>
					<#assign date_range="[${fromDate} TO ${currentdate}]">
					<#assign query = query + " +@cm\\:modified:${date_range}">	
			</#if>
		</#if>
		
		<#-- Add article id -->
		<#if args.askid?exists>
		  <#if debug="true">ASK ID: ${args.askid}<br/></#if>
		  <#assign query="+PATH:\"${articleHome.qnamePath}//.\" +ASPECT:\"{ask.new.model}article\" +@ask\\:askid:\"${args.askid}\"">
		</#if>
		
		<#if debug="true">Query: ${query}</#if>

		<#if args?exists>
			<#if args.maxresults?exists>
	    	<#assign maxresults=args.maxresults?number>
			<#else>
	    	<#assign maxresults=10>
			</#if>
		<#else>
				<#assign maxresults=10>
		</#if>
		
		<#assign rescount=1>
		
		<#assign results = companyhome.childrenByLuceneSearch[query]>
		<#if results?size = 0>
		  <br/>
			<table border="0" cellpadding="0" cellspacing="0"  width="100%">
			<tr align="left"><td align="left" style="font-size:130%" class="recordSetHeader">Sorry, no articles matched your search criteria</td></tr>
			</table>
			<!--<table style="margin-left:5px;">-->
		<#else>
			<br/>
			<table border="0" cellpadding="0" cellspacing="0"  width="100%">
			<tr align="left"><td align="left" style="font-size:130%" class="recordSetHeader">Search Results - <#if (results?size > maxresults)>Displaying ${maxresults} of <#else> Displaying ${results?size} of</#if> ${results?size} Article(s) Found</td></tr>
			</table>
			<!--<table style="margin-left:5px;">-->
		</#if>
		
		<#list results as child>
		<hr/>
		<#if debug="true"><p><#if child.nodeRef?exists>${child.nodeRef} ${child.name}</#if></p></#if>
		
		 <#if child.mimetype = "text/xml">
		   <#assign dom=child.xmlNodeModel>
		   <h3>KB Article</h3>
		   <hr width="100%"/>
		   <table border="0" cellpadding="0" cellspacing="0">
		   <tr><td width="200px">Title: ${dom.article.article_title}</td><td>Created: ${child.properties.created?date}</td></tr>
			 <#-- Format the category -->
		   <#assign articleCategory="${'${dom.article.category}'?replace(':','\\\\:')}">
		   <#assign articleCategory="${'${articleCategory}'?replace('/','\\\\/')}">
			 <#assign articleCategoryQuery="ID:" + "${articleCategory}">
			 <#assign articleCategoryNode = companyhome.childrenByLuceneSearch["${articleCategoryQuery}"][0]>

<#--
		   <tr><td>Category: ${articleCategoryNode.name}</td><td></td></tr>
-->
		   <tr><td colspan="2">Categories:
		   
		   <#list child.properties.categories as cat>
			   <#assign articleCategory="${'${cat}'?replace(':','\\\\:')}">
			   <#assign articleCategory="${'${articleCategory}'?replace('/','\\\\/')}">
				 <#assign articleCategoryQuery="ID:" + "${articleCategory}">
				 <#assign articleCategoryNode = companyhome.childrenByLuceneSearch["${articleCategoryQuery}"][0]>
				 ${articleCategoryNode.name},
		   </#list>
		   
		   
		   </td></tr>
		   <tr><td>Created By: ${child.properties.creator}</td><td>Created By: ${child.properties.creator}</td></tr>
		   <tr><td>Status: ${child.properties["ask:status"]}</td><td>Last Modified: ${child.properties.modified?date}</td></tr>
		   <tr><td>Visibility: ${dom.article.visibility}</td><td>Modified By: ${child.properties.modifier}</td></tr>
		   <tr><td>Article Type: ${dom.article.article_type}</td><td>Tags: ${dom.article.article_tags}</td></tr>
		   <tr><td>Alfresco Version: ${dom.article.alfresco_version}</td><td></td></tr>
		   </table>
		   <br/>
		   <p>TESTERRRRR</p>
		   <a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/article.ftl&contextPath=${child.displayPath}/${child.name}&current=true" target="new">View Article</a><br/>
		 </#if>

			<#if rescount = maxresults>
			    <#break>
			</#if>
			<#assign rescount=rescount + 1>
		
		</#list>
		
		</table>

	</#if>

</#if>


<!-- ******************************** END RETURN OF RETURN RESULTS ************************************* -->

<#if args?exists>
	<#if !args.search?exists>
		<#assign noSearch="true">
	<#else>
		<#assign noSearch="false">
	</#if>
	<#if args.maxresults?exists>
		<#assign maxresults=args.maxresults>
	<#else>
		<#assign maxresults=10>
	</#if>
<#else>
		<#assign maxresults=10>
</#if>

<#if !args?exists || noSearch="true">

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Alfresco ASK Search</title>

<link type="text/css" rel="stylesheet" href="/alfresco/css/main.css">	

<!-- Yahoo UI Widgets Calender Control -->
<link type="text/css" rel="stylesheet" href="/alfresco/scripts/ajax/yahoo/calendar/assets/calendar.css">	
<link rel="stylesheet" type="text/css" href="/alfresco/scripts/ajax/yahoo/docs/assets/dpSyntaxHighlighter.css" />
<script type="text/javascript" src="/alfresco/scripts/ajax/yahoo/yahoo/yahoo.js;"></script>
<script type="text/javascript" src="/alfresco/scripts/ajax/yahoo/event/event.js" ></script>
<script type="text/javascript" src="/alfresco/scripts/ajax/yahoo/dom/dom.js" ></script>
<link type="text/css" rel="stylesheet" href="/alfresco/scripts/ajax/yahoo/fonts/fonts.css">
<script type="text/javascript" src="/alfresco/scripts/ajax/yahoo/calendar/calendar.js"></script>

<!-- Article Search Utility Scripts -->
<#assign searchUtilityScripts = companyhome.childByNamePath["Data Dictionary/Knowledge Base/script/search_scripts.js"] >
<script type="text/javascript" src="/alfresco${searchUtilityScripts.url}"></script>
<!-- End Javascript Includes -->


<#assign debug="false">

<#if args?exists>
	<#if !args.searchText?exists>
		<#assign searchString = "test"/>
			<#else>
		<#assign searchString = args.searchText/>
	</#if>
<#else>
	<#assign searchString = "test"/>
</#if>


</head>


<body>

<h3>Alfresco Knowledge Base Search</h3>
	
<!-- <form name="searcharticles" method="get" action="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/Search_Articles.ftl&contextPath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/Search_Articles.ftl">
<form id="searcharticles" name="searcharticles" method="get" action="javascript:sndReq('');">	
	
		<table border="0"><tr valign="middle"><td valign="middle"><img valign="middle" width="32" height="32" src="/alfresco/images/logo/AlfrescoLogo32.png"/></td><td valign="middle"><input onkeyup="return userSearch(event);" type="text" id="searchText" name="searchText" value=""/><input type="button" onClick="javascript:sndReq('');" value="Search"/><input onclick="resetSearchArticles('true');" type="button" value="Reset"/></td>
		<td valign="middle">&nbsp;| Show <SELECT id="maxresults" NAME="maxresults" onchange="javascript:sndReq('');">
        <option id="5" name="5" value=5>5</option>
        <option id="10" name="10" value=10>10</option>
        <option id="15" name="15" value=15>15</option>
        <option id="20" name="20" value=20>20</option>
        <option id="50" name="50" value=50>50</option>
     </select> Items

</td></tr></table>
-->
		<table width="100%" border="0"><tr valign="top"><td align="left" valign="middle"><img valign="top" width="32" height="32" src="/alfresco/images/logo/AlfrescoLogo32.png"/></td><td align="left" valign="top"><input onkeyup="userSearch(event);" onchange="userSearch(event);" type="text" id="searchText" name="searchText" value=""/><input type="button" onClick="javascript:sndReq('current');" value="Search"/><input onclick="javascript:resetSearchArticles('true');" type="button" value="Reset"/>
		| Show <SELECT id="maxresults" NAME="maxresults" onchange="javascript:sndReq('current');">
        <option id="5" name="5" value=5>5</option>
        <option id="10" name="10" value=10>10</option>
        <option id="15" name="15" value=15>15</option>
        <option id="20" name="20" value=20>20</option>
        <option id="50" name="50" value=50>50</option>
    </select> Items
    </td>
    <td valign="top" align="right"><td>ASK ID:<input onkeyup="askIDSearch(event);" onchange="askIDSearch(event);"  type="text" maxlength="5" size="5" name="askid" id="askid"/><input type="button" value="Search" onclick="javascript:askidsearch('true');">
    </tr></table>

<table border="0" valign="top">
<tr>
<td valign="top">Type
		<select name="Type" id="article_type" onchange="javascript:sndReq('current');">
			<option value="Any">Any</option>
			<option value="Article">Article</option>
			<option value="FAQ">FAQ</option>
			<option value="White Paper">White Paper</option>
		</select>
</td>



</tr>
</table>	

<div><a id="toggleadvanced" href="javascript:categorydisplay();">Advanced&gt;&gt;</a></div>

<div id="categorydisplay"  style="display:none">
<table border="0">
<tr>
<td valign="top">&nbsp;Category</td><td>&nbsp;
			<select valign="top" name="category" id="category" multiple="multiple" onchange="javascript:sndReq('current');">
			<option>Any</option>
			<#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base\""] as child>            
			  <#list classification.getRootCategories("cm:generalclassifiable") as rootnode>
			    <#if rootnode.nodeRef = child.nodeRef>
			        <#list rootnode.subCategories as all>
			          <option value="${all.nodeRef}">- ${all.properties.name}</option>
			        </#list>
			    </#if>                             
			  </#list>
			</#list>	
		</select>
</td>
<td valign="top">&nbsp;| Modified&nbsp;</td><td valign="top"><input name="modified" id="modified" size="8"></input></td>
<td valign="top">
<div id="cal2Container" name="cal2Container"  style="position:absolute;display:none"></div>
<div>
  <a href="javascript:void(null)" onclick="showCalendar2()"><img id="link2" name="link2" src="/alfresco/scripts/ajax/yahoo/calendar/assets/pdate.gif" border="0" style="vertical-align:middle;margin:5px"/>
</div>
</td>
</tr>
</table>
</div>

<input type="hidden" name="templatePath" value="/Company Home/Data Dictionary/Knowledge Base/template/search_current_articles.ftl"/>
<input type="hidden" name="contextPath" value="/Company Home/Data Dictionary/Knowledge Base/template/search_current_articles.ftl"/>
<input type="hidden" id="status" value="Current">

</form>

<#-- Search results will be show in the search results div tag via AJAX -->
<div id="searchResults"></div>

</body>

</html>

</#if>

