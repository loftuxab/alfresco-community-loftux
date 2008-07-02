<import resource="/org/alfresco/web/site/include/ads-support.js">


// inputs
var parentPageId = wizard.request("parentPageId");
var pageId = wizard.request("pageId");

// objects
var parentPage = sitedata.getObject(parentPageId);
var page = sitedata.getObject("page", pageId);


var html = "Are you sure that you would like to remove the following page:";
html += "<br/><br/>";
html += "<B>" + page.getTitle() + "</B>";
html += "<br/><br/>";
html += "This <u>will not delete</u> child pages for this page.";


// set the html
wizard.setDialogHTML(html);

