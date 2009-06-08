// Get the all image files in the doclibrary for this site
var url = "/slingshot/doclib/doclist/images/site/" + page.url.templateArgs.site + "/documentLibrary?filter=all&max=500";
var json = remote.call(url);

// Create the model from the response data
model.images = eval('(' + json + ')');