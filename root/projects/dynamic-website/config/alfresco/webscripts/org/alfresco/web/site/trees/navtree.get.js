<import resource="/org/alfresco/web/site/include/utils.js">
<import resource="/org/alfresco/web/site/include/json.js">
<import resource="/org/alfresco/web/site/include/ads-support.js">

// inputs
var pageId = args["pageId"];

// return
var json = new Array();

// get children of this node
var ctr = 0;
var page = site.getObject(pageId);
var associations = site.findChildPageAssociations(pageId, null);
for(var i = 0; i < associations.length; i++)
{
	var association = associations[i];
	
	// info about the association
	var sourceId = association.getProperty("source-id");
	var destId = association.getProperty("dest-id");
	var associationType = association.getProperty("assoc-type");
	
	// child associations
	if("child" == associationType)
	{
		// get the child node
		var childNode = site.getObject(destId);
		if(childNode != null)
		{
			json[ctr] = { };
			json[ctr]["draggable"] = true;
			json[ctr]["alfType"] = "page";
			json[ctr]["pageId"] = destId;
			json[ctr]["associationId"] = association.getId();
			json[ctr]["parentId"] = pageId;
	
			json[ctr]["text"] = childNode.getTitle();
		
			// does this have children?
			var childAssociations = site.findChildPageAssociations(destId, null);
			if(childAssociations.length == 0)
				json[ctr]["leaf"] = true;
			
			// icon class
			json[ctr]["iconCls"] = "tree-icon-node";
		
			ctr++;
		}
	}
}

var outputString = json.toJSONString();
model.json = outputString;
