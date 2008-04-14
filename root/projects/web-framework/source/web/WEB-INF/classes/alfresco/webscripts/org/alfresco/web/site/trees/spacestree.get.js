<import resource="/org/alfresco/web/site/include/json.js">

var path = args["path"];
if(path == null || path == "")
{
	path = "/";
}

path = path.replace(/ /g, "%20");

// remote call
var json = new Array();
var responseString = site.callRemote("http://localhost:8080", "admin", "admin", "/alfresco/service/content/query?path="+path);
if(responseString != null)
{
	// this is done to convert the string to a javascript literal
	var rString = "" + responseString.toString();

	// get the response
	var response = rString.parseJSON();

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
		if(node.isDocument)
			json[i]["alfType"] = "dmFile";
		else
			json[i]["alfType"] = "dmSpace";

		if(node.hasChildren)
			json[i]["leaf"] = false;

		json[i]["url"] = "/alfresco" + node.url;
		json[i]["nodeId"] = node.id;
		json[i]["nodeRef"] = node.nodeRef;

		// copy in some alfresco metadata
		json[i]["cmType"] = node.type;


		var icon = node.properties["{http://www.alfresco.org/model/application/1.0}icon"];
		if(icon != null)
		{
			json[i]["icon"] = "/alfresco/images/icons/" + icon + ".gif";
		}
		else
		{
			json[i]["icon"] = "/alfresco" + node.icon16;
		}
	}
}

var outputString = json.toJSONString();
model.json = outputString;
