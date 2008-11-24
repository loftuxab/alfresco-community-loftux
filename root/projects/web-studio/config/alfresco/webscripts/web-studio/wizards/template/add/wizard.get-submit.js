<import resource="/include/support.js">

//
// create the template
//
var template = sitedata.newTemplate();
template.setProperty("title", templateName);
template.setProperty("description", templateDescription);
template.setProperty("template-type", templateTypeId);
template.setProperty("template-layout-type", templateLayoutType);
template.setProperty("height", templateHeight);
template.setProperty("width", templateWidth);
template.save();

// finalize things
wizard.setResponseCodeFinish();

