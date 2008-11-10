<#import "dynamic-tables.ftl" as tablesTemplate />
<#import "dynamic-absolute.ftl" as absoluteTemplate />

<html>
   <head>
      <title>${page.title}</title>
      ${head}
   </head>
   <body>
   
<#if ready?exists>

	<#assign rendered = false>

	<#if "${layoutType}" == "table layout" && !rendered>
		<@tablesTemplate.body/>
		<#assign rendered = true>
	</#if>

	<#if "${layoutType}" == "table" && !rendered>
		<@tablesTemplate.body/>
		<#assign rendered = true>
	</#if>

	<#if !rendered>
		<@absoluteTemplate.body/>
		<#assign rendered = true>
	</#if>

</#if>

   </body>
</html>