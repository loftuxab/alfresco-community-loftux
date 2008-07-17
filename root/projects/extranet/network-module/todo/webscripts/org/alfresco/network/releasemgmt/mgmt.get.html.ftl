<html>
  <head>
  	<link rel="stylesheet" href="/alfresco/css/main.css" type="text/css">
  </head>
  <body>
  
  <form method="POST" action="/alfresco/service/releasemgmt">
  
  <!-- tools -->
  Version:
  <select name="pushVersion">
  	<option value="" checked></option>
  <#list versions as version>
  	<option value="${version.nodeRef}">${version.name}</option>
  </#list>
  </select>
  
  Platform:
  <select name="pushPlatform" multiple>
  	<option value="" checked></option>
  <#list platforms as platform>
  	<option value="${platform.nodeRef}">${platform.name}</option>
  </#list>
  </select>

  Family:
  <select name="pushFamily">
  	<option value="" checked></option>
  <#list families as family>
  	<option value="${family.nodeRef}">${family.name}</option>
  </#list>
  </select>

  Asset Type:
  <select name="pushAssetType">
  	<option value="" checked></option>
  <#list assetTypes as assetType>
  	<option value="${assetType.nodeRef}">${assetType.name}</option>
  </#list>
  </select>

  Descriptors:
  <select name="pushDescriptor">
  	<option value="" checked></option>
  <#list descriptors as descriptor>
  	<option value="${descriptor.nodeRef}">${descriptor.name}</option>
  </#list>
  </select>

  Product:
  <select name="pushClass" multiple>
  	<option value="" checked></option>
  <#list productClasses as productClass>
  	<option value="${productClass.nodeRef}">${productClass.name}</option>
  </#list>
  </select>
  
  <input type="submit" value="SET"/>
  
  <hr/>

	<table border="0" width="100%">
		<tr>
			<td>Version</td>
			<td></td>
			<td>File</td>
			
			<td>Platform</td>
			<td>Descriptor</td>
			<td>Family</td>
			<td>Type</td>
			<td>Product Class</td>
		</tr>
				
<#list versions as version>

	<#assign versionId = version.name>

		<tr>
			<td colspan="8" align="center"><font size="3"><B>${versionId}</B></font></td>
		</tr>
	
	<#if contents[versionId]?exists>
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
			
			<tr>
				<td></td>
				<td><input type="checkbox" name="selectedFiles" value="${file.nodeRef}" /></td>
				<td>${file.name}</td>
				<td>${platform}</td>
				<td>${descriptor}</td>
				<td>${family}</td>
				<td>${assetType}</td>
				<td>${productClass}</td>
			</tr>

		</#list>
	</#if>
	
</#list>



<!-- show the unknowns -->

		<tr>
			<td colspan="8" align="center"><font size="3"><B>Unknown</B></font></td>
		</tr>
	
	<#if contents["unknown"]?exists>
		<#list contents["unknown"] as file>

			<tr>
				<td></td>
				<td>
					<input type="checkbox" name="selectedFiles" value="${file.nodeRef}" />
				</td>
				<td>${file.name}</td>
				<td colspan="5"></td>
			</tr>

		</#list>
	</#if>
  
  </form>
  
  </body>
</html>