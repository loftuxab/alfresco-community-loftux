<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/ReleaseManagement/releases-include.js">

function getArg(argName)
{
	var value = args[argName];
	if("" == value)
	{
		value = null;
	}
	return value;
}

// selected files
var sf = argsM["selectedFiles"];
var selectedFiles = new Array();
for(var z = 0; z < sf.length; z++)
{
	selectedFiles[z] = search.findNode(sf[z]);
}
model.selectedFiles = selectedFiles;



//
// incoming args
//
var display = getArg("display");
if(display == null)
{
	display = "incoming";
}
model.display = display;
var displayPath = getArg("displayPath");
model.displayPath = displayPath;
var pushVersion = getArg("pushVersion");
model.pushVersion = pushVersion;
var pushPlatform = getArg("pushPlatform");
model.pushPlatform = pushPlatform;
var pushFamily = getArg("pushFamily");
model.pushFamily = pushFamily;
var pushAssetType = getArg("pushAssetType");
model.pushAssetType = pushAssetType;
var pushDescriptor = getArg("pushDescriptor");
model.pushDescriptor = pushDescriptor;
var pushClass = getArg("pushClass");
model.pushClass = pushClass;



