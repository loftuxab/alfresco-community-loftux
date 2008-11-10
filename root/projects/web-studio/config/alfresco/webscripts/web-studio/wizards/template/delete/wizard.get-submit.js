<import resource="/include/support.js">

// inputs
var templateId = wizard.request("templateId");

// objects
var template = sitedata.getObject("template-instance", templateId);

// remove the object
remove(template);

// finalize things
wizard.setResponseCodeFinish();
