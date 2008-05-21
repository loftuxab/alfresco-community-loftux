model.currentPageId = site.getRequestContext().getPageId();

var homePage = site.getRootPage();
childPageAssociations = site.findChildPageAssociations(homePage.getId(), null);

model.pages = new Array();
model.pages[model.pages.length] = homePage;

for(var i = 0; i < childPageAssociations.length; i++)
{
	var destId = childPageAssociations[i].getProperty("dest-id");
	var page = site.getObject(destId);
	model.pages[model.pages.length] = page;
}

