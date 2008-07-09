<p align="center">

<form name="mainForm" id="mainForm">

<table width="100%">
<tr>
	<td width="50%" valign="top">
		<div id="releaseClassDiv">

			<table>
				<tr>
					<td width="48px" align="center" valign="center">
						<img src="/extranet/components/extranet/downloads/edit_32.gif"/>
					</td>
					<td valign="top">Which Alfresco product suite are you interested in?</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<div id="releaseClassControl">

							<table>
						<#list productClasses as productClass>
								<tr>
									<td rowspan="2">
										<input name="releaseClass" type="radio" value="${productClass.displayPath}/${productClass.name}" onclick="pickClass(this.value);" />
									</td>
									<td>
										<B>${productClass.name}</B>
									</td>
								</tr>
								<tr>
									<td>
										${productClass.description}
									</td>
								</tr>
						</#list>
							</table>				
						</div>	
					</td>
				</tr>
			</table>

		</div>
		
		<br/>

		<div id="releaseVersionDiv" style="display: none;">

			<table>
				<tr>
					<td width="48px" align="center" valign="center">
						<img src="/extranet/components/extranet/downloads/calc_32.gif"/>
					</td>
					<td valign="top">Which version of the product suite are you interested in?</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<div id="releaseVersionControl">

							<table>
						<#list versions as version>
								<tr>
									<td rowspan="2">
										<input name="releaseVersion" type="radio" value="${version.displayPath}/${version.name}" onclick="pickVersion(this.value);" />
									</td>
									<td>
										<B>${version.name}</B>
									</td>
								</tr>
								<tr>
									<td>
										${version.description}
									</td>
								</tr>
						</#list>
							</table>				
						</div>	
					</td>
				</tr>
			</table>

		</div>
		
		<br/>

		<div id="releasePlatformDiv" style="display: none;">

			<table>
				<tr>
					<td width="48px" align="center" valign="center">
						<img src="/extranet/components/extranet/downloads/smicn_32.gif">
					</td>
					<td valign="top">
						Which platform interests you?
					</td>
				</tr>
				<tr>
					<td></td>					
					<td>
						<div id="releasePlatformControl">

							<table>
						<#list platforms as platform>
								<tr>
									<td rowspan="2">
										<input name="releasePlatform" type="radio" value="${platform.displayPath}/${platform.name}" onclick="pickPlatform(this.value);" />
									</td>
									<td>
										<B>${platform.name}</B>
									</td>
								</tr>
								<tr>
									<td>
										${platform.description}
									</td>
								</tr>
						</#list>
							</table>				
						</div>	
					</td>
				</tr>
			</table>
		</div>
	</td>
	<td width="50%" nowrap valign="top">
		<div id="fileListingDiv">
		</div>
	</td>
</tr>
</table>

<script language="Javascript">
init();
</script>

</form>