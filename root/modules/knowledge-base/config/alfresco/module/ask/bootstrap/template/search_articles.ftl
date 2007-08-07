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

<#-- Form for collecting search criteria -->
<h3>Alfresco Knowledge Base Search</h3> 

	
<!--
<form id="searcharticles" name="searcharticles" method="get" action="javascript:sndReq('');">	
-->
		<table width="100%" border="0"><tr valign="top"><td align="left" valign="middle"><img valign="top" width="32" height="32" src="/alfresco/images/logo/AlfrescoLogo32.png"/></td><td align="left" valign="top"><input type="text" id="searchText" name="searchText" value=""/><input type="button"  value="Search" onclick="javascript:textsearch();"/><input onclick="javascript:resetSearchArticles();" type="button" value="Reset"/>
		| Show <SELECT id="maxresults" NAME="maxresults" onchange="javascript:setMaxresults('');">
        <option id="5" name="5" value=5>5</option>
        <option id="10" name="10" value=10>10</option>
        <option id="15" name="15" value=15>15</option>
        <option id="20" name="20" value=20>20</option>
        <option id="50" name="50" value=50>50</option>
    </select> Items
    </td>
    <td valign="top" align="right"><td>ASK ID:
    <input  type="text" name="askid" id="askid"/><input type="button" value="Search" onclick="javascript:askidsearch();">
    </tr></table>

<table border="0" valign="top">
<tr>
<td valign="top">Status
		<select name="status" id="status" onchange="javascript:setStatus('');">
		  <option value="Any">Any</option>
			<option value="Draft">Draft</option>
			<option value="Pending Approval">Pending Approval</option>
			<option value="Current">Current</option>
			<option value="Archived">Archived</option>
		</select>
</td>
		<td valign="top">Type
		<select name="article_type" id="article_type" onchange="javascript:setArticletype('');">
			<option value="Any">Any</option>
			<option value="Article">Article</option>
			<option value="FAQ">FAQ</option>
			<option value="White Paper">White Paper</option>
		</select>
</td>
		<td valign="top">Modifier
		<select name="article_modifier" id="article_modifier" onchange="javascript:setModifier('');">
			<option value="Any">Any</option>
			<#assign currentUser="${person.properties.userName}">
			<#list companyhome.childrenByLuceneSearch["+TYPE:\"{http://www.alfresco.org/model/content/1.0}person\""]?sort_by(['properties', 'userName']) as child>
				<option value="${child.properties.userName}">${child.properties.userName} <#if child.properties.userName = currentUser>(CURRENT)</#if></option>
			</#list>						
		</select>
</td>

</tr>
</table>	

<div><a id="toggleadvanced" href="javascript:categorydisplay();">Advanced&gt;&gt;</a></div>

<div id="categorydisplay"  style="display:none">
<table border="0">
<tr>
<td valign="top">&nbsp;Category</td><td>&nbsp;
			<select valign="top" name="category" id="category" multiple="multiple" onchange="javascript:setCategory('');">
			<option>Any</option>
			<#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base/cm:Article_x0020_Category/*\""] as child>              
			  
			          <option value="${child.properties.name}">- ${child.properties.name}</option>
			      
			</#list>	
		</select>
</td>
<td valign="top">
<label for="alfresco_version"><span>*</span>Alfresco Version(s)</label>
	<select name="alfresco_version" id="alfresco_version"  onchange="javascript:setAlfrescoversion('');">
	<option value="Any" selected=selected>Any</option>
	<#list companyhome.childrenByLuceneSearch["PATH:\"/cm:generalclassifiable/cm:Alfresco_x0020_Knowledge_x0020_Base/cm:Alfresco_x0020_Versions/*\""]as child>            
  
                  <option value="${child.properties.name}">- ${child.properties.name}</option>
			    
</#list>
	</select>
</td>
<td valign="top">Visibility
		<select name="visibility" id="visibility" onchange="javascript:setVisibility();">
			<option selected=selected>Any</option>
                          <option>Internal</option>
			<option>Partners</option>
			<option>Partners and Customers</option>
		</select>
</td><td valign="top">&nbsp;| Modified&nbsp;</td><td valign="top"><input name="modified" id="modified" size="8"></input></td>
<td valign="top">
<div id="cal2Container" name="cal2Container"  style="position:absolute;display:none"></div>
<div>
  <a href="javascript:void(null)" onclick="showCalendar2()"><img id="link2" name="link2" src="/alfresco/scripts/ajax/yahoo/calendar/assets/pdate.gif" border="0" style="vertical-align:middle;margin:5px"/>
</div>
</td>
</tr>
<tr></td></tr>
</table>
</div>

<#-- Search results will be show in the following search results div tag via AJAX -->
<div id="searchResults">
</div>



