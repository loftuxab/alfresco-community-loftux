<import resource="/include/support.js">

// incoming
var pageId = wizard.request("pageId");
var formatId = wizard.request("formatId");

sitedata.unassociateTemplate(pageId, formatId);

// finalize things
wizard.setResponseCodeFinish();

