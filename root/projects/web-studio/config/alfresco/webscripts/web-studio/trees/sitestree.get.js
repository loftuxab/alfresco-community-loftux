<import resource="/include/support.js">

var siteId = url.templateArgs["site"];
var path = url.templateArgs["path"];
if(path != null)
{
	path = path.replace(/ /g, "%20");
}

// json
var json = new Array();

// if we don't have a site, load the site
if(siteId == null)
{
	// load the site
	var connector = remote.connect("alfresco");
	var responseString = connector.call("/api/sites");
	if(responseString != null)
	{
		var response = eval('(' + responseString.toString() + ')');
	
		for(var i = 0; i < response.length; i++)
		{
			var node = response[i];
	
			json[i] = { };
			json[i]["text"] = node["title"];
			json[i]["draggable"] = true;
			json[i]["leaf"] = false;
			json[i]["alfType"] = "alfSite";
	
			json[i]["url"] = node.url;
			json[i]["nodeRef"] = node.node;
			
			json[i]["shareUrl"] = "/site/" + node["shortName"] + "/dashboard";
	
			// copy in some alfresco metadata
			json[i]["cmType"] = "site";
			
			json[i]["path"] = node["shortName"];			
		}	
	}
}
else
{
	if(path == null)
	{
		 path = "";
	}
	
	// load the path relative to the site
	var connector = remote.connect("alfresco");
	var responseString = connector.call("/webframework/content/metadata?path=/Company%20Home/Sites/" + siteId + "/" + path);
	if(responseString != null)
	{
		var response = eval('(' + responseString.toString() + ')');
		
		//
		// walk the json tree and convert to the tree structure that the UI expects
		//	
		for(var i = 0; i < response.children.length; i++)
		{
			var node = response.children[i];
	
			json[i] = { };
			json[i]["text"] = node["name"];
			json[i]["draggable"] = true;
			json[i]["leaf"] = true;
			json[i]["alfType"] = "dmFile";
			if(node.isContainer)
			{
				json[i]["alfType"] = "dmSpace";		
				json[i]["leaf"] = false;
			}
			else
			{
				json[i]["mimetype"] = node["mimetype"];
			}
			
			
			// try to guess at the object type
			if(node["name"] == "documentLibrary")
			{
				json[i]["shareType"] = "doclib";
			}
			if(node["name"] == "wiki")
			{
				json[i]["shareType"] = "wiki";
			}
			if(node["name"] == "blog")
			{
				json[i]["shareType"] = "blog";
			}
			if(node["name"] == "calendar")
			{
				json[i]["shareType"] = "calendar";
			}
			if(node["name"] == "discussions")
			{
				json[i]["shareType"] = "discussions";
			}
			
	
			json[i]["url"] = node.url;
			json[i]["nodeId"] = node.id;
			json[i]["nodeRef"] = node.nodeRef;
			
			if(path.indexOf("/") == -1)
			{
				// root level path, special treatment for Share
				var pageId = path;
				json[i]["shareUrl"] = "/site/" + siteId + "/" + pageId;
			}
			else
			{
				// TODO
				// how do we link directly to a blog, wiki page, document, etc, in share...?
			}
	
			// copy in some alfresco metadata
			json[i]["cmType"] = node.type;
			
			// path
			json[i]["path"] = siteId + "/" + path + "/" + node.name;
		}
	}
}

var outputString = json.toJSONString();
model.json = outputString;
