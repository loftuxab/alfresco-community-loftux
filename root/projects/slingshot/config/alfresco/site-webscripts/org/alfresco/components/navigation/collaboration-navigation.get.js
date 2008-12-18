// Get ids for all used pages
var siteId = page.url.templateArgs.site, usedPages = [];
var p = sitedata.getPage("site/" + siteId + "/dashboard");
if (p !== null)
{
   usedPages = eval('(' + p.properties.sitePages + ')');
   if (usedPages === null)
   {
      usedPages = [];
   }

   var availablePages = config.scoped["SitePages"]["pages"].childrenMap["page"], urlMap = {};
   for (i = 0; i < availablePages.size(); i++)
   {
      // Get page id from config file
      pageId = availablePages.get(i).attributes["id"];
      if (pageId)
      {
         pageUrl = availablePages.get(i).value;
         urlMap[pageId] = pageUrl;
      }
   }

   // Find the label for each page
   for (var i = 0; i < usedPages.length; i++)
   {
      var usedPage = usedPages[i], p = sitedata.getPage(usedPage.pageId), pageUrl = urlMap[usedPage.pageId];
      usedPage.title = p.title;
      if (pageUrl)
      {
         // Overwrite the stored pageUrl with the latest one from config file
         usedPage.pageUrl = pageUrl;  
      }
   }
   
   model.siteExists = true;
}

// Prepare template model
model.pages = usedPages;