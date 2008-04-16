<import resource="/org/alfresco/web/site/include/ads-support.js">

// inputs
var parentPageId = wizard.request("parentPageId");
var pageId = wizard.request("pageId");

// remove child page
removeChildPage(parentPageId, pageId, false);

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Page successfully removed!");
//setBrowserReload(true);