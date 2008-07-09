var categories = { };
function getCategory(path)
{
	if(path.substring(0,1) != "/")
		path = "/" + path;
		
	var object = categories[path];
	if(object == null)
	{
		var i = path.indexOf("/", 2);

		var rootCategoryName = path.substring(1,i);

		// get the category root node
		var rootCategoryNode = null;
		var rootCategories = classification.getRootCategories("cm:generalclassifiable");
		for(var i = 0; i < rootCategories.length; i++)
		{
			if(rootCategories[i].name == rootCategoryName)
				rootCategoryNode = rootCategories[i];
		}
		
		if(rootCategoryNode != null)
		{
			// assemble the remaining path
			path = path.substring(i+1, path.length);
			object = rootCategoryNode.childByNamePath(path);
		}
	}
	return object;
}

var object = null;

// allow for retrieval by path
if(args["path"] != null)
{
	var path = args["path"];
	object = getCategory(path);
}	

// store onto model
model.object = object;
model.includeChildren = true;
model.includeContent = false;