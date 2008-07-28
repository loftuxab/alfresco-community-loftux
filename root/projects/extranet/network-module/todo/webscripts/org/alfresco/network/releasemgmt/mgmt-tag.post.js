<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/ReleaseManagement/releases-include.js">

// things being pushed
var pushVersion = argsM["pushVersion"];
var pushPlatform = argsM["pushPlatform"];
var pushFamily = argsM["pushFamily"];
var pushAssetType = argsM["pushAssetType"];
var pushDescriptor = argsM["pushDescriptor"];
var pushClass = argsM["pushClass"];

// selected files
var selectedFiles = argsM["selectedFiles"];

// walk over each file
for each (nodeRef in selectedFiles)
{
	var file = search.findNode(nodeRef);
	if(file != null)
	{	
		// reset its categories
		file.properties["{http://www.alfresco.org/model/content/1.0}categories"] = new Array();
		var props = file.properties["{http://www.alfresco.org/model/content/1.0}categories"];

		// handle versions
		for each (n in pushVersion)
		{
			if(n != null && n != "")
			{
				var v = getVersion(model.rootCategory, n);
				if(v != null)
				{
					props[props.length] = v;
				
					// additional feature... check whether parent is also a version
					if(v.parent.name != "ReleaseVersion")
					{
						props[props.length] = v.parent;
					}
				}
			}
		}

		// handle platforms
		for each (n in pushPlatform)
		{
			if(n != null && n != "")
			{
				var v = getPlatform(model.rootCategory, n);
				if(v != null)
				{
					props[props.length] = v;
				}
			}
		}

		// handle families
		for each (n in pushFamily)
		{
			if(n != null && n != "")
			{
				var v = getFamily(model.rootCategory, n);
				if(v != null)
				{
					props[props.length] = v;
				}
			}
		}

		// handle asset types
		for each (n in pushAssetType)
		{
			if(n != null && n != "")
			{
				var v = getAssetType(model.rootCategory, n);
				if(v != null)
				{
					props[props.length] = v;
				}
			}
		}

		// handle descriptors
		for each (n in pushDescriptor)
		{
			if(n != null && n != "")
			{
				var v = getDescriptor(model.rootCategory, n);
				if(v != null)
				{
					props[props.length] = v;
				}
			}
		}

		// handle product classes
		for each (n in pushClass)
		{
			if(n != null && n != "")
			{
				var v = getProductClass(model.rootCategory, n);
				if(v != null)
				{
					props[props.length] = v;
				}
			}
		}

		file.save();
	}
}
