/**
 * Site and All Site Search component GET method
 */

// fetch the request params search all / search term
var siteId = (page.url.templateArgs["site"] != undefined) ? page.url.templateArgs["site"] : "";
model.siteId = siteId;
model.searchTerm = (page.url.args["t"] != undefined) ? page.url.args["t"] : "";
model.searchTag = (page.url.args["tag"] != undefined) ? page.url.args["tag"] : "";