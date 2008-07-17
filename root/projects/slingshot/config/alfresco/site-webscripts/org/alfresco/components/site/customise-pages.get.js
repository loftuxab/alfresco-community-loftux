

var availablePages = config.scoped["SitePages"]["pages"].childrenMap["page"];

var pages = [];
for (var i = 0; i < availablePages.size(); i++)
{
   var pageId = availablePages.get(i).value;
   var page = sitedata.getPage(pageId);
   if(page)
   {
      pages[pages.length] = {
         pageId: pageId,
         title: page.title,
         description: page.description,
         originallyInUse: false
      };
   }
}

model.pages = pages;

