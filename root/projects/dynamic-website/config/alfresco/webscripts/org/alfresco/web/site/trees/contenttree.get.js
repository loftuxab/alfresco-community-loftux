<import resource="/org/alfresco/web/site/include/utils.js">
<import resource="/org/alfresco/web/site/include/json.js">

var relativePath = args["path"];
var avmStoreId = args["avmStoreId"];

// return
var json = new Array();

var fs = sitedata.fileSystem;
var rootFile = fs.getFile(relativePath);

var children = rootFile.children;
for(var i = 0; i < children.length; i++)
{
	var scriptFile = children[i];
	var fileName = scriptFile.name;
	
	json[i] = { };
	json[i]["text"] = fileName;
	json[i]["draggable"] = true;
	json[i]["leaf"] = true;
	
	// file case
	if(scriptFile.isFile)
	{
		json[i]["alfType"] = "file";
		
		// stamp the json with anything interesting that we can tell about this file
		var z = fileName.indexOf(".");
		if(z > -1)
		{
			var fileExtension = fileName.substring(z+1, fileName.length());
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
	
	// directory case
	if(scriptFile.isDirectory)
	{
		json[i]["alfType"] = "directory";
		if(scriptFile.children != null && scriptFile.children.length > 0)
		{
			json[i]["leaf"] = false;
		}
	}
}

var outputString = json.toJSONString();
model.json = outputString;
