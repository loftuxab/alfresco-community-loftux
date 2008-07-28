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

  <h1>Tag</h1>
  
  <form method="POST" action="/alfresco/service/releasemgmt-tag">
  
  <!-- tools -->
  <table>
  <tr>
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

		<input type="submit" value="Apply Tags"/>
	</td>
	
  </tr>
  </table>

  <hr/>

  <table border="0" width="100%">

	<tr>
		<td class="label" width="100%">Files to be tagged</td>
	</tr>
				
<#list selectedFiles as selectedFile>

	<tr>
		<td width="100%">
			${selectedFile.displayPath}/${selectedFile.name}
			<input type="hidden" name="selectedFiles" id="selectedFiles" value="${selectedFile.nodeRef}"/>
		</td>
	</tr>

</#list>
		
  </table>

  </form>
  
  </body>
</html>