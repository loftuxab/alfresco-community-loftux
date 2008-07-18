function toJsonArray(elements)
{
	var array = null;
	
	if(elements != null)
	{
		array = new Array();
		
		for(var i = 0; i < elements.length; i++)
		{
			array[array.length] = elements[i];
		}
	}
	
	return array;
}



// the node ref
var nodeRef = args["nodeRef"];

// get the item
var child = search.findNode(nodeRef);

var item = { };
item["title"] = child.name;
item["description"] = child.properties["{http://www.alfresco.org/model/content/1.0}description"];
item["headline"] = child.properties["{http://www.alfresco.org/model/network/1.0}newsItemHeadline"];
item["teaser"] = child.properties["{http://www.alfresco.org/model/network/1.0}newsItemTeaser"];
item["readMore"] = child.properties["{http://www.alfresco.org/model/network/1.0}newsItemReadMore"];

// effectivity
item["effectiveFrom"] = child.properties["{http://www.alfresco.org/model/content/1.0}from"];
item["effectiveTo"] = child.properties["{http://www.alfresco.org/model/content/1.0}to"];

// item types
item["itemTypes"] = toJsonArray(child.properties["{http://www.alfresco.org/model/network/1.0}newsItemType"]);

// related links
item["relatedLinks"] = toJsonArray(child.properties["{http://www.alfresco.org/model/network/1.0}newsItemRelatedLinks"]);

// teaser image
item["teaserImage"] = toJsonArray(child.assocs["{http://www.alfresco.org/model/network/1.0}newsItemTeaserImage"]);

// related media
item["relatedMedia"] = toJsonArray(child.assocs["{http://www.alfresco.org/model/network/1.0}newsItemRelatedMedia"]);

// categories
item["categories"] = toJsonArray(child.assocs["{http://www.alfresco.org/model/network/1.0}newsItemCategories"]);
		
model.object = item;		
