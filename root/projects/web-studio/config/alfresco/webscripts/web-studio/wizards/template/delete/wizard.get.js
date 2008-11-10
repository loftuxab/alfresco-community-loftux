<import resource="/include/support.js">

// inputs
var templateId = wizard.request("templateId");

// objects
var template = sitedata.getObject("template-instance", templateId);

// generate the html
var html = "Are you sure that you would like to remove the following template:";
html += "<br/><br/>";
html += "<B>" + template.title + "</B>";
html += "<br/><br/>";
html += "This <u>will not delete</u> pages that reference this template.";

// set the html
wizard.setDialogHTML(html);

