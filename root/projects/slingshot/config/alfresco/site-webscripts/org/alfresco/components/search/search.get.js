model.siteId = (page.url.templateArgs["site"] != undefined) ? page.url.templateArgs["site"] : "";
model.searchTerm = (page.url.args["t"] != undefined) ? page.url.args["t"] : "";
if (page.url.args["a"] != undefined)
{
   model.searchAll = (page.url.args["a"] != "false"); // we want to default to true
}
else
{
   model.searchAll = true;
}
