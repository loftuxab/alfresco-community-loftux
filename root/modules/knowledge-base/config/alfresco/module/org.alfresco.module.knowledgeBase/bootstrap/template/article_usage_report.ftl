<#-- Display a list of the 10 most commonly used (read) articles-->

<link type="text/css" href="/alfresco/css/main.css" rel="stylesheet">

<#assign datetimeformat="dd MMM yyyy HH:mm">

<#assign usagequery="+ASPECT:\"{ask.new.model}article\" +ASPECT:\"{extension.contenthits}contentHits\" +@ask\\:status:\"Current\"">
<#assign articleusage = companyhome.childrenByLuceneSearch[usagequery]>

<table width="100%" border="0">
	<tr><th align="left" colspan="6"  class="recordSetHeader">Article Usage Report</th></tr>
    <tr><td><p>Top 10 Most Read Articles</p><h3></h3></td></tr>
    <tr><td>
    <table width="100%" border="0">
    <tr style="font-size:130%" >
    
    	<td></td>

        <td>Name</td>

        <td>Title</td>
        
        <td align="right">Read Count</td>

    </tr>

<#list articleusage?sort_by(['properties', 'ch:readCount'])?reverse as child>
    
   <tr>
   
    <td>

		${child_index + 1} 

    </td>



    <td> <#-- Article Name -->

        <a href="/alfresco/navigate/showDocDetails/workspace/SpacesStore/${child.id}"><img src="/alfresco/images/icons/View_details.gif" border=0 align=absmiddle alt="Article Details" title="Article Details"></a>

        <a href="/alfresco/navigate/showDocDetails/workspace/SpacesStore/${child.id}" title="View Article">${child.properties.name}</a>

    </td>

    <td> <#-- Article title -->

        <a href="/alfresco/download/direct/${child.nodeRef}/${child.name}"><img src="/alfresco${child.icon16}" width=16 height=16 border=0 align=absmiddle alt="View Article" title="View Article"></a>
        <!--<a href="/alfresco/download/direct/${child.nodeRef}/${child.name}">-->
        <a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/article.ftl&contextPath=${child.displayPath}/${child.name}" target="new">

        <#if child.properties["cm:title"]?exists>${child.properties["cm:title"]}<#else>${child.properties.name}</#if></a>

    </td>
    
    <td align="right">
    	<#if child.properties["ch:readCount"]?exists>
    		${child.properties["ch:readCount"]}
      </#if>
    </td>
    </tr>    
    <#-- Only show the top 10 -->
    <#if child_index = 9><#break></#if>
</#list>

	</table>

	</td>
	</tr>
	</table>    





