<import resource="/org/alfresco/web/site/include/ads-support.js">


// get the navigation node
var pageId = wizard.request("pageId");
var page = sitedata.getObject(pageId);


// set up form elements
wizard.addHiddenElement("parentPageId", pageId);
wizard.addElement("pageName", "");
wizard.addElement("pageDescription", "");
wizard.addElementFormat("pageName", "Page Name", "textfield", 290);
wizard.addElementFormat("pageDescription", "Description", "textarea", 290);


