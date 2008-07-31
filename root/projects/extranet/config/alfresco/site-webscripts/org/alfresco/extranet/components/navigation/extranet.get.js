<import resource="classpath:/alfresco/extranet/core.js">

model.currentPageId = context.page.id;
model.pages = new Array();

var homePage = sitedata.rootPage;
//model.pages[model.pages.length] = homePage;

// add in the general community pages
if(alfCommunity)
{
	var pageAssociations = sitedata.findPageAssociations(homePage.id, null, "community");
	if(pageAssociations != null)
	{
		pageAssociations.sort(pageAssociationSort);
		for(var i = 0; i < pageAssociations.length; i++)
		{
			var destId = pageAssociations[i].properties["dest-id"];
			var page = sitedata.getPage(destId);		
			model.pages[model.pages.length] = page;
		}
	}
}

// add in the "registered" community pages
if(alfRegistered)
{
	var pageAssociations = sitedata.findPageAssociations(homePage.id, null, "community-registered");
	if(pageAssociations != null)
	{
		pageAssociations.sort(pageAssociationSort);
		for(var i = 0; i < pageAssociations.length; i++)
		{
			var destId = pageAssociations[i].properties["dest-id"];
			var page = sitedata.getPage(destId);
			model.pages[model.pages.length] = page;
		}
	}
}

// add in the "enterprise" pages
if(alfEnterprise)
{
	var pageAssociations = sitedata.findPageAssociations(homePage.id, null, "enterprise");
	if(pageAssociations != null)
	{
		pageAssociations.sort(pageAssociationSort);
		for(var i = 0; i < pageAssociations.length; i++)
		{
			var destId = pageAssociations[i].properties["dest-id"];
			var page = sitedata.getPage(destId);
			model.pages[model.pages.length] = page;
		}
	}
}

// add in the "final" pages
if(!alfEnterprise)
{
	var pageAssociations = sitedata.findPageAssociations(homePage.id, null, "final");
	if(pageAssociations != null)
	{
		pageAssociations.sort(pageAssociationSort);
		for(var i = 0; i < pageAssociations.length; i++)
		{
			var destId = pageAssociations[i].properties["dest-id"];
			var page = sitedata.getPage(destId);
			model.pages[model.pages.length] = page;
		}
	}
}

function pageAssociationSort(a,b)
{
	var indexA = a.getProperty("sort-order");
	var indexB = b.getProperty("sort-order");
	
	if(indexA == null && indexB == null)
		return 0;
	if(indexA == null && indexB != null)
		return 1;
	if(indexA != null && indexB == null)
		return -1;
		
	return indexA - indexB;
}