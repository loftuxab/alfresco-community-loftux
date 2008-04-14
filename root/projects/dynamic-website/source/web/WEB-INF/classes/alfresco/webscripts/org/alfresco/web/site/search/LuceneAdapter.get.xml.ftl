<?xml version="1.0" encoding="UTF-8"?>
<list>

<#if resultset?exists && resultset?size &gt; 0>		
<#list resultset as doc>
    	<#if doc.url?ends_with(".xml")>    	
    	<item>
    		<path><@relativePath url="${doc.url}"/></path>
    		<id>${doc.id}</id>
    		<nodeRef>${doc.nodeRef}</nodeRef>
    		<#assign docRenditions = renditions[doc.url]>
		<#list docRenditions as docr>
			<#assign template = docr.properties["{http://www.alfresco.org/model/wcmappmodel/1.0}parentrenderingenginetemplate"]>
			<#assign templateTitle = template.properties["{http://www.alfresco.org/model/content/1.0}title"]>

			<#assign renditionProperties = docr.properties["{http://www.alfresco.org/model/wcmappmodel/1.0}parentrenditionproperties"]>
			<#assign renditionMimetype = renditionProperties.properties["{http://www.alfresco.org/model/wcmappmodel/1.0}mimetypeforrendition"]>

			<rendition>
				<templateTitle>${templateTitle}</templateTitle>
				<mimetype>${renditionMimetype}</mimetype>
				<path><@relativePath url="${docr.url}"/></path>
			</rendition>
		</#list>
    	</item>
        
        </#if>
</#list>
</#if>

</list>

<#macro relativePath url>
<#assign i = url?index_of("ROOT;")>
<#if i &gt; -1>
<#assign doc_path_temp  = url?substring(url?index_of("ROOT;")+4)>
<#assign doc_path_temp2 = doc_path_temp?substring(0,doc_path_temp?index_of("/"))>
<#assign file_path = doc_path_temp2?replace(";","/")>
${file_path}<#return/>
</#if>
<#assign i = url?index_of("ROOT/")>
<#if i &gt; -1>
<#assign doc_path_temp  = url?substring(url?index_of("ROOT/")+4)>
${doc_path_temp}<#return/>
</#if>
</#macro> 