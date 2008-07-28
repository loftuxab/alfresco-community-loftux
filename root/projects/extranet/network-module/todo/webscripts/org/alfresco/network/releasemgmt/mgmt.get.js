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



//
// query for content and filter
//
var platformContent = { };
model.platformContent = platformContent;
var descriptorContent = { };
model.descriptorContent = descriptorContent;
var familyContent = { };
model.familyContent = familyContent;
var typeContent = { };
model.typeContent = typeContent;
var classContent = { };
model.classContent = classContent;



// walk all content in this folder and sort it into version categories
var categorizedContent = { };
var topFolder = model.releasesFolder;
if(display != null)
{
	if("all" == display)
	{
	}
	else if("incoming" == display)
	{
		topFolder = model.releasesFolder.childByNamePath("Incoming");
	}
	else if("path" == display)
	{
		topFolder = model.publishedFolder.childByNamePath(displayPath);
	}
	
}
sortVersions(topFolder, categorizedContent);

// set into model
model.contents = categorizedContent;






// root 
function passesFilter(file)
{
	var valid = true;

	if(model.pushVersion != null && valid)
	{
		valid = hasVersion(file, model.pushVersion);
	}
	if(model.pushPlatform != null && valid)
	{
		valid = hasPlatform(file, model.pushPlatform);
	}
	if(model.pushFamily != null && valid)
	{
		valid = hasFamily(file, model.pushFamily);
	}
	if(model.pushAssetType != null && valid)
	{
		valid = hasAssetType(file, model.pushAssetType);
	}
	if(model.pushDescriptor != null && valid)
	{
		valid = hasDescriptor(file, model.pushDescriptor);
	}
	if(model.pushClass != null && valid)
	{
		valid = hasProductClass(file, model.pushClass);
	}
	
	return valid;
}

function sortVersions(file, bucket)
{
	if(file.isDocument && passesFilter(file))
	{
		var sorted = false;

		var categories = file.properties["{http://www.alfresco.org/model/content/1.0}categories"];
		if(categories != null)
		{
			for(var i = 0; i < categories.length; i++)
			{
				if(categories[i].parent.name == "ReleaseVersion")
				{
					var files = bucket[categories[i].name];
					if(files == null)
					{
						files = new Array();
						bucket[categories[i].name] = files;
					}
					files[files.length] = file;
					sorted = true;
				}
				if(categories[i].parent.name == "ReleasePlatform")
				{
					platformContent[file.nodeRef] = categories[i].name;
				}
				if(categories[i].parent.name == "ReleaseFileDescriptor")
				{
					descriptorContent[file.nodeRef] = categories[i].name;
				}
				if(categories[i].parent.name == "ReleaseFamily")
				{
					familyContent[file.nodeRef] = categories[i].name;
				}
				if(categories[i].parent.name == "ReleaseAssetType")
				{
					typeContent[file.nodeRef] = categories[i].name;
				}
				if(categories[i].parent.name == "ReleaseClass")
				{
					classContent[file.nodeRef] = categories[i].name;
				}
			}
		}

		if(!sorted)
		{
			var files = bucket["unknown"];
			if(files == null)
			{
				files = new Array();
				bucket["unknown"] = files;
			}
			files[files.length] = file;
		}
	}
	else
	{
		if(file.children != null)
		{
			for(var i = 0; i < file.children.length; i++)
			{
				sortVersions(file.children[i], bucket);
			}
		}
	}
}
