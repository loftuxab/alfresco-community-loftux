<html>
<head>
<style type="text/css">
body {
   font-family: Arial, Helvetica, sans-serif;
   font-size: 62.5%;
   background-color: #999999;
   margin: 20px;
}
#container {
	 color: #333333;
     background-color: white;
    padding: 2em;
    /*
    -moz-border-radius-topleft: 2em;
    -moz-border-radius-topright: 2em;
    -moz-border-radius-bottomleft: 2em;
    -moz-border-radius-bottomright: 2em;
    */
    font-size: 1.2em;
}
ul li {
    list-style-type: square;
    list-style-image: url("http://www.alfresco.com/assets/images/icons/arrow_b.gif");
    margin-bottom: 0.25em;
}
h1 {
    font-size: 1.67em;
    font-weight: 400;
    color: #333333;
    padding-bottom: 0.2em;
    margin-bottom: 1em;
    border-bottom: 1px #cccccc solid;
}
select, input, textarea {
    font-size: 1em;
}
td {
    font-size: 0.9em;
}
.desc {
	font-size: 0.8em;
	color: #666666;
	margin-bottom: 1em;
}
</style>

</head>
<body>
<div id="container">
<#if document?exists>
 <#if document.mimetype = "text/xml">
   <#assign dom=document.xmlNodeModel>
   <h3>KB Article</h3>
   <#if document.properties["ch:readCount"]?exists>Read Count: ${document.properties["ch:readCount"]}</#if>
   <hr/>
   <table cellpadding="0" cellspacing="0">
   <tr><td width="300px">Title: <#if document.properties.title?exists>${document.properties.title}<#else>${document.name}</#if></td><td>Created: ${document.properties.created?date}</td></tr>
   <tr><td>Categories:
   <#-- Display all the catagories assigned to the node -->
		<#list document.properties.categories as cat>
			   <#assign articleCategory="${'${cat}'?replace(':','\\\\:')}">
			   <#assign articleCategory="${'${articleCategory}'?replace('/','\\\\/')}">
				 <#assign articleCategoryQuery="ID:" + "${articleCategory}">
				 <#assign articleCategoryNode = companyhome.childrenByLuceneSearch["${articleCategoryQuery}"][0]>
				 ${articleCategoryNode.name}<#if cat_has_next>,</#if>
		</#list>		   
   </td><td>Created By: ${document.properties.creator}</td></tr>
   <tr><td>Status:: ${document.properties["ask:status"]}</td><td>Last Modified: ${document.properties.modified?date}</td></tr>
   <tr><td>Visibility: <#if document.properties["ask:visibility"]?exists>${document.properties["ask:visibility"]}</#if></td><td>Modified By: ${document.properties.modifier}</td></tr>
   <tr><td>Article Type: ${document.properties["ask:article_type"]}</td><td>Tags: 
   <#list document.properties["ask:tags"] as tag>${tag}<#if tag_has_next>,</#if></#list>
   </td></tr>
   <tr><td>Alfresco Version: <#if document.properties["ask:alfresco_version"]?exists><#list document.properties["ask:alfresco_version"] as versionValue>${versionValue}<#if versionValue_has_next>,</#if></#list></#if></td><td></td></tr>
   </table>
      
   <br/>
   
   <b>Article Body:</b><br/>
   ${dom.article.editor}
   
   <hr/>
   
   <#if args?exists>
     	<hr/>
        <#if !args.current?exists>
     		<a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/edit_article.ftl&contextPath=${document.displayPath}/${document.name}">Edit Article</a><br/>
   		</#if>
    	<a href="javascript:window.close();">Close</a>
   <#else>
     <br/>
     <a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/article.ftl&contextPath=${document.displayPath}/${document.name}" target="new">View Standalone</a><br/>
     <br/>
   </#if>

     
 </#if>
 </#if>
 
 </div>
 </body>
 </html>