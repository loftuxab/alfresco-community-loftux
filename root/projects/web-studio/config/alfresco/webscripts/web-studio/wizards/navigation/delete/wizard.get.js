<import resource="/include/support.js">

// inputs
var parentId = wizard.request("parentId");
var pageId = wizard.request("pageId");

// objects
var parentPage = sitedata.getObject("page", parentId);
var page = sitedata.getObject("page", pageId);


var html = "Are you sure that you would like to remove the following page:";
html += "<br/><br/>";
html += "<B>" + page.getTitle() + "</B>";
html += "<br/><br/>";
html += "This <u>will not delete</u> child pages for this page.";

// set the html
wizard.setDialogHTML(html);

// set response title
wizard.setResponseTitle("Do you want to remove this page?");

