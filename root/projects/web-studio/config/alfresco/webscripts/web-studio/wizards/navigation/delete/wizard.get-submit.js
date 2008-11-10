<import resource="/include/support.js">

// inputs
var parentId = wizard.request("parentId");
var pageId = wizard.request("pageId");

// remove child page
removeChildPage(parentId, pageId, false);

// finalize things
wizard.setResponseCodeFinish();
