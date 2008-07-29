<style type="text/css">
<!--
table.asset
{
	background-color: white;
}
table.asset th {
	padding: 1px 1px 1px 1px;
	background-color: white;
	-moz-border-radius: 0px 0px 0px 0px;
}
table.asset td {
	padding: 4px 4px 4px 4px;
	background-color: white;
	-moz-border-radius: 0px 0px 0px 0px;
}
td.asset-label
{
	font: bold 12px verdana;
}
td.asset-property
{
	font: 12px verdana;
}

-->
</style>




<!-- title -->
<h1>${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetHeadline"]}</h1>
<p>
	Version ${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetVersionID"]}
	
	<br/>
	${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetTeaser"]}
	
	<br/>
	<a href='${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetPreviewURL"]}'>Preview</a>
</p>
<br/>
<br/>


<!-- details -->
<h2>Details</h2>
<p>
	${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetLongDescription"]}
</p>
<table class="asset">
<tr>
	<td class="asset">
		<img src=""/>
	</td>
	<td class="asset">
		<table>
			<tr>
				<td class="asset-label">
					Creation
				</td>
				<td class="asset-property">
					${obj.properties["{http://www.alfresco.org/model/content/1.0}creator"]}
					
					&nbsp;
					
					<i>(${obj.properties["{http://www.alfresco.org/model/content/1.0}created"]})</i>
				</td>
			</tr>
			<tr>
				<td class="asset-label">
					Modification
				</td>
				<td>
					${obj.properties["{http://www.alfresco.org/model/content/1.0}modifier"]}
					
					&nbsp;
					
					<i>(${obj.properties["{http://www.alfresco.org/model/content/1.0}modified"]})</i>
				</td>
			</tr>
			<tr>
				<td class="asset-label">
					Installation Type
				</td>
				<td>
					${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetInstallationType"]}
				</td>
			</tr>

		</table>
	</td>
</tr>
</table>
<br/>
<br/>



<h2>Downloads and Documentation</h2>
<p>
	<a href='${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetDownloadRef"]}'>Download the plugin</a>
	<br/>
	<a href='${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetDocumentationRef"]}'>Download the documentation</a>
	<br/>
</p>
<br/>
<br/>


<h2>Files</h2>
<p>
	<table>

		<#list obj.children as file>
			<#if file.isDocument>

		<tr>

			<td nowrap>
				<img src="${file.icon32}"/>
			</td>
			<td width="100%">
				<a href="${file.downloadUrl}">
					${file.name}
				</a>
			</td>
			<td nowrap align="right">
				<i>${file.size} bytes</i>
			</td>

		</tr>
		
			</#if>
		</#list>		

	</table>
</p>
<br/>
<br/>



<h2>About the author</h2>
<p>
	${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetAuthorDetails"]}
</p>
