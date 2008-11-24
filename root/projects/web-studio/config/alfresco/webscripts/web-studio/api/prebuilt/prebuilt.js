function addSite(sites, id, title, description, previewImageUrl, archiveUrl)
{
	sites[id] = { };
	
	sites[id].title = title;
	sites[id].description = description;
	sites[id].previewImageUrl = previewImageUrl;
	sites[id].archiveUrl = archiveUrl;
}

function getSites()
{
	var sites = { };
	
	// add in the "none" option
	addSite(sites, "none", "Blank Web Site", "A starter site", "/images/wizards/blank-website.png", "");

	// call over to network and retrieve sites
	var remoteSites = getRemoteSites("alfresco-network", "/service/webstudio/sites");
	if(remoteSites != null)
	{
		for(var key in remoteSites)
		{
			sites[key] = remoteSites[key];
		}
	}
	
	// add in green energy
	//var baseUrl = "https://network.alfresco.com/extranet/proxy/alfresco-noauth/api/path/content/workspace/SpacesStore/Company%20Home/WebStudio";
	//addSite(sites, "green1", "Green Energy V1", "Green Energy V1", baseUrl + "/green1/green-energy-250.png", baseUrl + "/green1/green-energy.zip");

	return sites;	
}

function getSite(id)
{
	return getSites()[id];	
}

function getRemoteSites(endpointId, uri)
{
	var connector = remote.connect(endpointId);
	
	var feed = connector.get(uri);
	model.feed = feed;
	
	var obj = eval('(' + feed + ')');
	
	var sites = { };
	
	for(var key in obj.results)
	{
		if(key != null && key != "none")
		{
			var site = obj.results[key];
			sites[key] = site;
		}
	}
	
	return sites;
}