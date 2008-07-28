<script type="text/javascript">

	var host = "http://netrepo.alfresco.com/alfresco";
	
	var productClass = null;   
	var version = null;
	var platform = null;	
	var docsDownloads = null;
	
	var showRefresh = false;
	
	// allow file descriptors to float
	var fileDescriptor = null;
	
	// constraint family and asset type
	var family = "/categories/General/Releases/ReleaseFamily/enterprise";
	var assetType = "/categories/General/Releases/ReleaseAssetType/download";

	// div elements
	var classDiv = null;
	var versionDiv = null;
	var platformDiv = null;
	var fileListingDiv = null;
	var docsDownloadsDiv = null;
	
	function init()
	{
		classDiv = document.getElementById('releaseClassDiv');
		versionDiv = document.getElementById('releaseVersionDiv');
		platformDiv = document.getElementById('releasePlatformDiv');
		docsDownloadsDiv = document.getElementById('releaseDocsDownloadsDiv');
	
		fileListingDiv = document.getElementById('fileListingDiv');
		
		resetRadio('releaseClass');
		resetRadio('releaseVersion');
		resetRadio('releasePlatform');
		resetRadio('releaseDocsDownloads');
	}

	// called whenever a control changes   	
	function refreshResults()
	{
		if(showRefresh)
		{		
			var ajaxHtml = "<table width='100%'><tr><td align='center'><br/><br/><br/><br/><br/><br/><br/><img src='/extranet/components/extranet/downloads/ajax_anim.gif'></td></tr></table>";
			fileListingDiv.innerHTML = ajaxHtml;

			var first = true;
			var queryString = "{\"path\":\"/Network/Releases/Published\", \"categories\":[";
			if(version != null)
			{
				if(!first) { queryString += ","; };
				queryString += "{ \"path\" : \"" + version + "\" }";
				first = false;
			}
			if(platform != null)
			{
				if(!first) { queryString += ","; };
				queryString += "{ \"path\" : \"" + platform + "\" }";
				first = false;
			}
			if(fileDescriptor != null)
			{
				if(!first) { queryString += ","; };
				queryString += "{ \"path\" : \"" + fileDescriptor + "\" }";
				first = false;
			}
			if(family != null)
			{
				if(!first) { queryString += ","; };
				queryString += "{ \"path\" : \"" + family + "\" }";
				first = false;
			}
			if(productClass != null)
			{
				if(!first) { queryString += ","; };
				queryString += "{ \"path\" : \"" + productClass + "\" }";
				first = false;
			}
			if(assetType != null)
			{
				if(!first) { queryString += ","; };
				queryString += "{ \"path\" : \"" + assetType + "\" }";
				first = false;
			}
			queryString += "]}";

			// run the query
			var file = "/extranet/proxy/alfresco/webframework/query?json=" + queryString;

			// build the request object
			var req = null;
			if(window.XMLHttpRequest)
			{
				req = new XMLHttpRequest();
			}
			else if(window.ActiveXObject)
			{
				req = new ActiveXObject("Microsoft.XMLHTTP");
			}
			else
			{
				alert("Not Supported");
			}

			//prepare the xmlhttprequest object
			req.open("GET",file,true);
			req.setRequestHeader("Cache-Control", "no-cache");
			req.setRequestHeader("Pragma", "no-cache");
			req.onreadystatechange = function() 
			{
				if (req.readyState == 4)
				{
					if(req.status != 200)
					{
						alert("Error code " + req.status + " received: " + req.statusText);
					}
					else
					{
						var o = eval('(' + req.responseText + ')');

						// build the html
						var html = "<font face='Verdana' size='3'>";
						html +="<img src='/extranet/components/extranet/downloads/down_32.gif' width='16px' height='16px'>";
						
						if(o.results.length == 1)
						{
							html +="&nbsp;" + o.results.length + " Matching File &nbsp;";
						}
						else
						{
							html +="&nbsp;" + o.results.length + " Matching Files &nbsp;";
						}
						html +="<img src='/extranet/components/extranet/downloads/down_32.gif' width='16px' height='16px'>";
						html +="</font>";
						html +="<br/>";
						html +="<br/>";

						html += "<table>";

						// walk children, insert html
						var children = o.results;
						for(var i = 0; i < children.length; i++)
						{
							var node = children[i];
							var icon32 = host + node.icon32;
							var name = node.name;
							var downloadUrl = host + node.downloadUrl;
							var description = node.description;
							//description = "test description";
							
							var x = downloadUrl.indexOf("/d/a");
							if(x > -1)
							{
								var cdr = downloadUrl.substring(x+4);
								downloadUrl = "/api/node/content" + cdr;
								downloadUrl = "/extranet/proxy/alfresco" + downloadUrl;
							}

							html += "<tr>";
							html += "<td valign='center'>";
							html += "<img src='" + icon32 + "'/>";
							html += "</td>";
							html += "<td>";
							html += "&nbsp;";
							html += "</td>";
							html += "<td>";
							html += "<A href='" + downloadUrl + "'>";
							html += "<B>" + name + "</B>";
							html += "</A>";
							
							html += "<br/>" + description;
							html += "</td>";
							html += "</tr>";

						}
						html += "</table>";

						fileListingDiv.innerHTML = html;
					}
				}
			}

			//send the request
			req.send(null);		
		}
	}
	
	function pickClass(value)
	{
		productClass = value;
		if(productClass != null && productClass != "")
		{		
			// reset and show version
			versionDiv.style.display = "block";
			refreshResults();
		}
	}
	
	function pickVersion(value)
	{
		version = value;
		if(version != null && version != "")
		{		
			// reset and show platform
			platformDiv.style.display = "block";
			refreshResults();
		}
	}
	
	function pickPlatform(value)
	{
		platform = value;
		if(platform != null && platform != "")
		{		
			// reset and show doc/download
			docsDownloadsDiv.style.display = "block";
			refreshResults();
		}
	}	

	function pickDocsDownloads(value)
	{
		docsDownloads = value;
		if(docsDownloads != null && docsDownloads != "")
		{
			// set asset type
			assetType = "/categories/General/Releases/ReleaseAssetType/" + value;
			
			// set up file listing for display
			fileListingDiv.style.display = "block";
			showRefresh = true;
			refreshResults();
		}
	}	
	
	function resetRadio(name)
	{
		var radios = document.mainForm[name];
		for(var i = 0; i < radios.length; i++)
		{
			radios[i].checked = false;
		}
	}
		
</script>
