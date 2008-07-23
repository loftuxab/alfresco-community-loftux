function toLuceneDateString(month, day, year)
{
	// convert the current date to lucene string
	var str = "";
	str += year;
	str += "\\-";
	
	var monthString = "" + month;
	if(monthString.length < 2)
	{
		monthString = "0" + monthString;
	}
	str += monthString;
	str += "\\-";
		
	var dayString = "" + day;
	if(dayString.length < 2)
	{
		dayString = "0" + dayString;
	}
	str += dayString;
	
	str += "T00:00:00";
	
	return str;
}


function cleanup(str)
{
	str = str.replace("\"", "'");
	str = str.replace("\r", "");
	str = str.replace("\n", "");
	str = str.trim();
	
	return str;
}

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



// get the required item type
var requiredItemType = args["itemType"];

// query for all content over the past month
var now = new Date();
var luceneNow = toLuceneDateString(now.getMonth() + 1, now.getDate(), now.getFullYear());
var then = new Date(now.getTime() - (28*24*60*60*1000)); // month
var luceneThen = toLuceneDateString(then.getMonth() + 1, then.getDate(), then.getFullYear());

var array = new Array();
var query = "+TYPE:\"{http://www.alfresco.org/model/network/1.0}newsItem\"";
query += " +@cm\\:modified:[" + luceneThen + " TO " + luceneNow + "]";

var children = search.luceneSearch(query);
for(var i = 0; i < children.length; i++)
{
	var child = children[i];
	
	var proceed = false;

	if(requiredItemType == null)
	{
		proceed = true;
	}
	else
	{
		// get the item types for this content
		var itemTypes = child.properties["{http://www.alfresco.org/model/network/1.0}newsItemType"];
		if(itemTypes != null)
		{
			for(var z = 0; z < itemTypes.length; z++)
			{
				if(requiredItemType == itemTypes[z])
				{
					proceed = true;
				}			
			}
		}
	}
	
	if(proceed)
	{
		var item = { };
		item["title"] = child.name;
		//item["nodeRef"] = child.nodeRef;
		item["nodeRef"] = child.nodeRef.storeRef.protocol + "://" + child.nodeRef.storeRef.identifier + "/" + child.nodeRef.id;
		item["id"] = child.id;
		item["author"] = child.properties["{http://www.alfresco.org/model/content/1.0}author"];
		item["creator"] = child.properties["{http://www.alfresco.org/model/content/1.0}creator"];		
		item["created"] = child.properties["{http://www.alfresco.org/model/content/1.0}created"];
		item["modifier"] = child.properties["{http://www.alfresco.org/model/content/1.0}modifier"];
		item["modified"] = child.properties["{http://www.alfresco.org/model/content/1.0}modified"];
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
				
		array[array.length] = item;
	}
}
model.objects = array;