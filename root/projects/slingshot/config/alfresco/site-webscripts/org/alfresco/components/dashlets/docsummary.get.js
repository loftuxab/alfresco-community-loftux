// Get the Documents Modified data for this site
var json = remote.call("/slingshot/doclib/doclist/documents/site/" + page.url.templateArgs.site + "/documentLibrary?filter=recentlyModified&max=10");

// Create the model
var docs = eval('(' + json + ')');
model.docs = docs;