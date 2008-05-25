model.currentPageId = context.page.id;
var homePage = sitedata.rootPage;

childPageAssociations = sitedata.findChildPageAssociations(homePage.id, null);

model.pages = new Array();
model.pages[model.pages.length] = homePage;

for(var i = 0; i < childPageAssociations.length; i++)
{
	var destId = childPageAssociations[i].properties["dest-id"];
	var page = sitedata.getObject(destId);
	model.pages[model.pages.length] = page;
}

