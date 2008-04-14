<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/utils.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/json.js">

var node = companyhome;

var path = args["path"];
if(path != null && path.length > 2)
{
	path = path.substring(1, path.length);
	node = companyhome.childByNamePath(path);
}

// return
var json = new Array();

var children = node.children;
for(var i = 0; i < children.length; i++)
{
	var node = children[i];
	
	json[i] = { };
	json[i]["draggable"] = true;
	if(node.isDocument)
		json[i]["alfType"] = "dmFile";
	else
		json[i]["alfType"] = "dmSpace";
	if(node.children.length == 0)
		json[i]["leaf"] = true;
	json[i]["text"] = node.name;
	json[i]["draggable"] = true;
	
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
	
	json[i]["url"] = "/alfresco" + node.url;
	json[i]["nodeId"] = node.id;
	json[i]["nodeRef"] = node.nodeRef;
}

var outputString = json.toJSONString();
model.json = outputString;
