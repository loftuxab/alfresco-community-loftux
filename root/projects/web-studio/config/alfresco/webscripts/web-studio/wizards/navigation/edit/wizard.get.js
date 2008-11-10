<import resource="/include/support.js">

// inputs
var pageId = wizard.request("pageId");
var page = sitedata.getObject("page", pageId);

var pageName = wizard.getSafeProperty(page, "title");
var pageDescription = wizard.getSafeProperty(page, "description");

// set up form elements
wizard.addHiddenElement("pageId", pageId);
wizard.addElement("pageName", pageName);
wizard.addElement("pageDescription", pageDescription);
wizard.addElementFormat("pageName", "Page Name", "textfield", 290);
wizard.addElementFormat("pageDescription", "Description", "textarea", 290);

// set response title
wizard.setResponseTitle("Editing properties for page: '" + pageName + "'");



