<import resource="/include/support.js">

// inputs
var pageId = args["pageId"];

// return
var json = new Array();

// walk child pages
var pages = sitedata.findChildPages(pageId);
for(var i = 0; i < pages.length; i++)
{
	var page = pages[i];

	json[i] = { };
	json[i]["parentId"] = pageId;

	json[i]["draggable"] = true;
	json[i]["alfType"] = "page";
	json[i]["pageId"] = page.id;
	json[i]["text"] = page.title;

	// does this have children?
	var childAssociations = sitedata.findChildPageAssociations(page.id, null);
	if(childAssociations.length == 0)
	{
		json[i]["leaf"] = true;
	}

	// icon class
	json[i]["iconCls"] = "tree-icon-node";
}

var outputString = json.toJSONString();
model.json = outputString;
