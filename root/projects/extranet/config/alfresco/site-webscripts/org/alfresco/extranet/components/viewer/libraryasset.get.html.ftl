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


<#assign teaserImageUrl = "/extranet/images">
<#assign teaserImage = obj.associations["{http://www.alfresco.org/model/network/1.0}libraryAssetTeaserImage"]>
<#if teaserImage?exists>
	<#assign teaserImageUrl = "/extranet/proxy/alfresco/api/path/content/workspace/SpacesStore/" + teaserImage[0].displayPath + "/" + teaserImage[0].name>
</#if>


<!-- title -->
<h1>${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetHeadline"]}</h1>
<br/>
<table class="asset">
<tr>
	<td class="asset">
		<a href="${teaserImageUrl}" target="_blank">
			<img src='${teaserImageUrl}' width="240px" height="180px" />
		</a>
	</td>
	<td class="asset" valign="top">
		<table>
			<tr>
				<td class="asset-property" colspan="2">
					${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetTeaser"]}
				</td>
			</tr>
			<tr><td><br/></td></tr>
			<tr>
				<td class="asset-label">
					Version
				</td>
				<td class="asset-property" width="100%">
					${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetVersionID"]}
				</td>
			</tr>
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

<!-- details -->
<h2>Details</h2>
<p>
	${obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetLongDescription"]}
</p>
<table class="asset">
<tr>
	<td class="asset"></td>
	<td class="asset">
		<table>

		</table>
	</td>
</tr>
</table>

<br/>

<!-- Previews -->
<#assign previewUrl = obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetPreviewURL"]>
<#assign previews = obj.associations["{http://www.alfresco.org/model/network/1.0}libraryAssetPreviews"]>
<#if previewUrl?exists || previews?exists>

<h2>Previews</h2>

	<#if previewUrl?exists>

		<p>
			A <a href='${previewUrl}'>Live Preview of this application</a> is available.
		</p>
	</#if>
	
	<#if previews?exists>
	
		<p>
			<#assign firstPreview = true>
			<#list previews as preview>
			
				<#if firstPreview == false>,&nbsp;</#if>
			
				<#assign downloadUrl = "/extranet/proxy/alfresco/api/path/content/workspace/SpacesStore" + preview.displayPath + "/" + preview.name>
				<a target="_blank" href="${downloadUrl}">${preview.name}</a>
				
				<#assign firstPreview = false>
			</#list>
		</p>
	</#if>
</#if>



<br/>
<br/>


<!-- Downloads -->
<#assign downloadUrl = "/extranet/proxy/alfresco/api/path/content/workspace/SpacesStore" + obj.displayPath + "/" + obj.name + "/" + obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetDownloadRef"]>
<#assign documentationUrl = "/extranet/proxy/alfresco/api/path/content/workspace/SpacesStore" + obj.displayPath + "/" + obj.name + "/" + obj.properties["{http://www.alfresco.org/model/network/1.0}libraryAssetDocumentationRef"]>

<h2>Downloads and Documentation</h2>
<p>
	<a href='${downloadUrl}'>Download the plugin</a>
	<br/>
	<a href='${documentationUrl}'>Download the documentation</a>
	<br/>
</p>
<br/>
<br/>


<h2>Files</h2>
<p>
	<table>

		<#list obj.children as file>
			<#if file.isDocument>
			
				<#assign downloadUrl = "/extranet/proxy/alfresco/api/path/content/workspace/SpacesStore" + obj.displayPath + "/" + obj.name + "/" + file.name>

		<tr>

			<td nowrap>
				<img src="${file.icon32}"/>
			</td>
			<td width="100%">
				<a href="${downloadUrl}">
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


