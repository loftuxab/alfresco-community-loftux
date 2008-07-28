function getRootCategories()
{
	return classification.getRootCategories("cm:generalclassifiable");
}

function getRootCategory(name)
{
	var rootCategories = getRootCategories();
	
	var rootCat = null;
	if(rootCategories != null)
	{
		for(var i = 0; i < rootCategories.length; i++)
		{
			if(rootCategories[i].name == name)
			{
				rootCat = rootCategories[i];
			}
		}
	}

	return rootCat;
}

function fillArrayWithChildren(array, node)
{
	if(node.children != null && node.children.length > 0)
	{
		for(var i = 0; i < node.children.length; i++)
		{
			array[array.length] = node.children[i];
			fillArrayWithChildren(array, node.children[i]);
		}
	}
}

function getCategoryArray(rootCategory, path)
{
	var array = new Array();
	
	var category = rootCategory.childByNamePath(path);
	fillArrayWithChildren(array, category);
	
	return array;
}

function hasCategory(file, rootCategory, path)
{
	var associated = false;
	var categoryNode = getCategory(rootCategory, path);
	
	if(categoryNode != null)
	{	
		var nodes = file.properties["{http://www.alfresco.org/model/content/1.0}categories"];
		if(nodes != null)
		{
			for(var u = 0; u < nodes.length; u++)
			{
				var id1 = nodes[u].nodeRef + "";
				var id2 = categoryNode.nodeRef + "";
				if(id1 == id2)
				{
					associated = true;
				}
			}
		}
	}
	
	return associated;
}

function getCategory(rootCategory, path)
{
	return rootCategory.childByNamePath(path);
}
