<import resource="/org/alfresco/web/site/include/utils.js">
<import resource="/org/alfresco/web/site/include/json.js">

var incomingPath = args["path"];


var fullpath = url.extension.split("/");	
var storeid = fullpath[0];
model.store = avm.lookupStore(storeid);	
model.path = (fullpath.length == 1 ? "/" : "/" + fullpath.slice(1).join("/"));
model.node = model.store.lookupNode(model.path);

// kind of a strange way to determine the relative path
var i = model.path.indexOf("/",1);
var relativePath = model.path.substring(i, model.path.length);
if(i == -1)
	relativePath = "";


// return
var json = new Array();

var children = model.node.children;
for(var i = 0; i < children.length; i++)
{
	var node = children[i];
	
	json[i] = { };
	json[i]["draggable"] = true;
	if(node.isFile())
	{
		json[i]["alfType"] = "avmFile";
		
		// stamp the json with anything interesting that we can tell about this file
		var z = node.name.indexOf(".");
		if(z > -1)
		{
			var fileExtension = node.name.substring(z+1, node.name.length());
			if("html" == fileExtension)
				json[i]["alfFileType"] = "html";
			if("htm" == fileExtension)
				json[i]["alfFileType"] = "html";
			if("gif" == fileExtension)
				json[i]["alfFileType"] = "image";
			if("jpg" == fileExtension)
				json[i]["alfFileType"] = "image";
			if("jpeg" == fileExtension)
				json[i]["alfFileType"] = "image";
			if("png" == fileExtension)
				json[i]["alfFileType"] = "image";
			if("bmp" == fileExtension)
				json[i]["alfFileType"] = "image";
			if("xml" == fileExtension)
				json[i]["alfFileType"] = "xform";
			if("pdf" == fileExtension)
				json[i]["alfFileType"] = "pdf";
				
				
			// set up icon class
			if("html" == json[i]["alfFileType"])
				json[i]["iconCls"] = "tree-icon-webapplication-htmlfile";
			if("image" == json[i]["alfFileType"])
				json[i]["iconCls"] = "tree-icon-webapplication-imagefile";
			if("xml" == fileExtension)
				json[i]["iconCls"] = "tree-icon-webapplication-xmlfile";
			if("pdf" == fileExtension)
				json[i]["iconCls"] = "tree-icon-webapplication-pdffile";
				
			if("jsp" == fileExtension)
				json[i]["iconCls"] = "tree-icon-webapplication-textfile";
			if("asp" == fileExtension)
				json[i]["iconCls"] = "tree-icon-webapplication-textfile";
			if("php" == fileExtension)
				json[i]["iconCls"] = "tree-icon-webapplication-textfile";
		}
	}
	if(node.isDirectory())
	{
		json[i]["alfType"] = "avmDirectory";
	}
	if(node.children.length == 0)
		json[i]["leaf"] = true;
	json[i]["text"] = node.name;
	json[i]["draggable"] = true;
}

var outputString = json.toJSONString();
model.json = outputString;
