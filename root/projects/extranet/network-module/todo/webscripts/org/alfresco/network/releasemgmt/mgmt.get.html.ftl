<html>
  <head>
	<style type="text/css">
	<!--
	body
	{
		font: 10px arial;
	}
	
	input,textarea,select
	{
	   font-family: Tahoma, Arial, Helvetica, sans-serif;
	   font-size: 10px;
	}
	
	table
	{
		background-color: white;
	}
	th {
		font: 10px arial;
		padding: 1px 1px 1px 1px;
		background-color: white;
		-moz-border-radius: 0px 0px 0px 0px;
	}
	td {
		font: 10px arial;
		background-color: white;
		-moz-border-radius: 0px 0px 0px 0px;
	}
	td.label
	{
		font: bold 12px arial;
	}
	td.grayed
	{
		font: 10px arial;
		padding: 4px 4px 4px 4px;
		background-color: white;
		-moz-border-radius: 0px 0px 0px 0px;
		color: lightgray;
	}
	-->
	</style>
  </head>
  <body>
  
  <h1>Browse</h1>

  <form method="GET" action="/alfresco/service/releasemgmt">
  
  <!-- tools -->
  <table>
  <tr>
  	<td align="left" valign="top" nowrap>
  		<input type="radio" name="display" value="incoming" <#if display == 'incoming'>checked</#if>>Show incoming files
  		<br/>
  		<input type="radio" name="display" value="all" <#if display == 'all'>checked</#if>>Show all files
  		<br/>
  		<input type="radio" name="display" value="path" <#if display == 'path'>checked</#if>>Specific path:
  		<br/>
  		<input type="text" name="displayPath" size="40" value="<#if displayPath?exists>${displayPath}</#if>"/>
  	</td>
  	<td align="left" valign="top">
  		<B>Version</B>
  		<br/>
		<select name="pushVersion">
			<option value="" checked></option>
			<#list versions as version>
				<option value="${version.name}" <#if pushVersion?exists && version.name == pushVersion>selected</#if> >
					${version.name}
				</option>
			</#list>
		</select>
	</td>
	<td align="left" valign="top">
		<B>Platform</B>
		<br/>
		<select name="pushPlatform" multiple size=4>
			<option value="" checked></option>
			<#list platforms as platform>
				<option value="${platform.name}" <#if pushPlatform?exists && platform.name == pushPlatform>selected</#if> >
					${platform.name}
				</option>
			</#list>
		</select>
	</td>
	<td align="left" valign="top">
		<B>Family</B>
		<br/>
		<select name="pushFamily">
			<option value="" checked></option>
			<#list families as family>
				<option value="${family.name}" <#if pushFamily?exists && family.name == pushFamily>selected</#if> >
					${family.name}
				</option>
			</#list>
		</select>
	</td>
	<td align="left" valign="top">
		<B>Asset Type</B>
		<br/>
		<select name="pushAssetType">
			<option value="" checked></option>
			<#list assetTypes as assetType>
				<option value="${assetType.name}" <#if pushAssetType?exists && assetType.name == pushAssetType>selected</#if> >
					${assetType.name}
				</option>
			</#list>
		</select>
	</td>
	<td align="left" valign="top">
		<B>Descriptors</B>
		<br/>
		<select name="pushDescriptor">
			<option value="" checked></option>
			<#list descriptors as descriptor>
				<option value="${descriptor.name}" <#if pushDescriptor?exists && descriptor.name == pushDescriptor>selected</#if> >
					${descriptor.name}
				</option>
			</#list>
		</select>
	</td>
	<td align="left" valign="top">
		<B>Product</B>
		<br/>
		<select name="pushClass" multiple size=4>
			<option value="" checked></option>
			<#list productClasses as productClass>
				<option value="${productClass.name}" <#if pushClass?exists && productClass.name == pushClass>selected</#if> >
					${productClass.name}
				</option>
			</#list>
		</select>
	</td>
	<td align="left" valign="top">

		<input type="submit" value="Apply Filter"/>
	</td>
	
  </tr>
  </table>

  
  
  
  <hr/>

	<table border="0" width="100%">
		<tr>
			<td class="label">Version</td>
			<td class="label"></td>
			<td class="label" width="100%">Files</td>
		</tr>
				
<#list versions as version>

	<#assign versionId = version.name>
	
	<#if contents[versionId]?exists && contents[versionId]?size &gt; 0>
	
		<tr>
			<td colspan="3" align="center"><font size="3"><B>${versionId}</B></font></td>
		</tr>
	
		<#list contents[versionId] as file>

			<#assign platform = "">
			<#if platformContent[file.nodeRef]?exists>
				<#assign platform = platformContent[file.nodeRef]>
			</#if>
			<#assign descriptor = "">
			<#if descriptorContent[file.nodeRef]?exists>
				<#assign descriptor = descriptorContent[file.nodeRef]>
			</#if>
			<#assign family = "">
			<#if familyContent[file.nodeRef]?exists>
				<#assign family = familyContent[file.nodeRef]>
			</#if>
			<#assign assetType = "">
			<#if typeContent[file.nodeRef]?exists>
				<#assign assetType = typeContent[file.nodeRef]>
			</#if>
			<#assign productClass = "">
			<#if classContent[file.nodeRef]?exists>
				<#assign productClass = classContent[file.nodeRef]>
			</#if>
			
			<#assign filePath = file.displayPath + "/" + file.name>
			
			<tr>
				<td></td>
				<td><input type="checkbox" name="selectedFiles" value="${file.nodeRef}" /></td>
				<td width="100%">${filePath}</td>
			</tr>

		</#list>
	</#if>
	
</#list>



<!-- show the unknowns -->
	
	<#if contents["unknown"]?exists && contents["unknown"]?size &gt; 0>

		<tr>
			<td colspan="3" align="center"><font size="3"><B>Unknown</B></font></td>
		</tr>

		<#list contents["unknown"] as file>

			<#assign filePath = file.displayPath + "/" + file.name>
			<tr>
				<td></td>
				<td>
					<input type="checkbox" name="selectedFiles" value="${file.nodeRef}" />
				</td>
				<td width="10)%">${filePath}</td>
			</tr>

		</#list>
	</#if>
  
  
  	<br/>
  	<br/>
  	
  	<input type="button" name="classify" id="classify" value="Tag these files" onClick="this.form.action = '/alfresco/service/releasemgmt-tag'; this.form.submit();"/>
  </form>
  
  </body>
</html>