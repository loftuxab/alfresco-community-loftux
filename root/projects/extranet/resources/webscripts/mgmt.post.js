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
				var v = search.findNode(n);		
				if(v != null)
				{
					props[props.length] = v;
				}
			}
		}

		// handle platforms
		for each (n in pushPlatform)
		{
			if(n != null && n != "")
			{
				var v = search.findNode(n);		
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
				var v = search.findNode(n);		
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
				var v = search.findNode(n);		
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
				var v = search.findNode(n);		
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
				var v = search.findNode(n);		
				if(v != null)
				{
					props[props.length] = v;
				}
			}
		}

		file.save();
	}
}
