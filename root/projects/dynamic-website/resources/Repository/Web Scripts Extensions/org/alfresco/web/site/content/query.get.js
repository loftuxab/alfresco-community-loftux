<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/web/site/include/json.js">

function map(source, dest, key)
{
	var p = source[key];
	if(p != null)
	{
		if(p.name == null && p.getContent == null)
		{
			dest[key] = p;
		}
	}
}

// serializes a content object into a java object
function serialize(content, children)
{
	var object = { };

	// serialize all properties
	object["properties"] = { };
	for(var key in content.properties)
	{
		map(content.properties, object.properties, key);
	}
	
	if(children)
	{
		object["children"] = new Array();
		if(content.children != null)
		{
			for(var i = 0; i < content.children.length; i++)
			{
				object["children"][i] = serialize(content.children[i], false);
			}
		}
	}
	
	object["hasChildren"] = false;
	if(content.children != null && content.children.length > 0)
		object["hasChildren"] = true;
	

	// base properties
	map(content, object, "isContainer");
	map(content, object, "isDocument");
	//map(content, object, "content");
	
	map(content, object, "url");
	map(content, object, "downloadUrl");
	map(content, object, "mimetype");
	map(content, object, "size");
	map(content, object, "displayPath");

	map(content, object, "qnamePath");
	map(content, object, "icon16");
	map(content, object, "icon32");
	map(content, object, "isLocked");
	map(content, object, "id");
	
	map(content, object, "nodeRef");
	map(content, object, "name");
	map(content, object, "type");
	//map(content, object, "parent");
	map(content, object, "isCategory");

	return object;
}

var path = args["path"];
if(path == null || path == "" || path == "/")
	path = "/Company Home";
else
	path = "/Company Home" + path;
	
// look up the content by path
var content = roothome.childByNamePath(path);

// build the json output
var jsonObject = serialize(content, true);
var json = jsonObject.toJSONString();

// place onto model
model.json = json;


