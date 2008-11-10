<import resource="/include/support.js">

// inputs
var pageId = wizard.request("pageId");
var parentId = wizard.request("parentId");

// get the page
var page = sitedata.getObject("page", pageId);

// get new page properties
var pageName = wizard.getSafeProperty(page, "title");
var pageDescription = wizard.getSafeProperty(page, "description");

// set up form elements
wizard.addHiddenElement("pageId", pageId);
wizard.addHiddenElement("parentId", parentId);
wizard.addElement("pageName", pageName);
wizard.addElement("pageDescription", pageDescription);
wizard.addElementFormat("pageName", "Page Name", "textfield", 290);
wizard.addElementFormat("pageDescription", "Description", "textarea", 290);

// set response title
wizard.setResponseTitle("Create a copy of '" + pageName + "'");
