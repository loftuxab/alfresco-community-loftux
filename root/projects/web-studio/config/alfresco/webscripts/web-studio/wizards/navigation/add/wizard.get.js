<import resource="/include/support.js">

// get the navigation node
var pageId = wizard.request("pageId");

// set up form elements
wizard.addHiddenElement("parentPageId", pageId);
wizard.addElement("pageName", "");
wizard.addElement("pageDescription", "");
wizard.addElementFormat("pageName", "Page Name", "textfield", 290);
wizard.addElementFormat("pageDescription", "Description", "textarea", 290);

