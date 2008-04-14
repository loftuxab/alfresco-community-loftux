<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/utils.js">

var avmStore = null;
function populateAvmModel()
{
	// find the node inside of the AVM
	var fullpath = url.extension.split("/");
	
	var storeid = fullpath[0];

	// only go against the staging store (this is really weak)	
	var i = storeid.indexOf("--");
	if(i > 0)
		storeid = storeid.substring(0,i);

	var store = avm.lookupStore(storeid);
	model.store = store;
	avmStore = store;	
}

function bindResults(theResults)
{
	model.resultset = theResults;

	// any renditions to store away		
	model.renditions = { };
	for(var i = 0; i < theResults.length; i++)
	{
		var doc = theResults[i];
		
		var docRenditions = doc.properties["{http://www.alfresco.org/model/wcmappmodel/1.0}renditions"];
		if(docRenditions != null)
		{
			var ar = new Array();
			var count = 0;
			for(var j = 0; j < docRenditions.length; j++)
			{
				var avmNode = avm.lookupNode(avmStore.id + ":" + docRenditions[j]);
				if(avmNode != null)
				{
					ar[count] = avmNode;
					count++;
				}
			}
			model.renditions[doc.url] = ar;
		}
	}

}


// initialize
populateAvmModel();

// query
if(args["q"] != null)
{
	var avmQuery = args["q"];
	if(avmStore != null)
	{
		var avmResults = avmStore.luceneSearch(avmQuery);
		if(avmResults != null)
		{
			bindResults(avmResults);
		}
	}
}
else if(args["p"] != null)
{
	var relativePath = args["p"];
	if(avmStore != null)
	{
		var node = avmStore.lookupNode(relativePath);
		if(node != null)
		{
			var results = new Array();
			results[0] = node;
			bindResults(results);
		}
	}
}

