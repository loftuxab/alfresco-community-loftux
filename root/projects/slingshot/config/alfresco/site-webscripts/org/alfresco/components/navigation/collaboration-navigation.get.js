// Get ids for all used pages
var siteId = page.url.templateArgs.site;
var p = sitedata.getPage("site/" + siteId + "/dashboard");
var usedPages = eval('(' + p.properties.sitePages + ')');
if(usedPages == null)
{
   usedPages = [];
}

// Find the label for each page
for(var i = 0; i < usedPages.length; i++)
{
   var usedPage = usedPages[i];
   var p = sitedata.getPage(usedPage.pageId);
   usedPage.title = p.title;
}

// Prepare template model
model.pages = usedPages;
