// get the summary info for this site
var json = remote.call("/slingshot/docsummary?site=" + page.url.args.site);

// create the model
var docs = eval('(' + json + ')');
model.docs = docs;
