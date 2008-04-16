<import resource="/org/alfresco/web/site/include/ads-support.js">

// update the page
assertPage(pageId, pageName, pageDescription);

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Page successfully updated!");
//setBrowserReload(true);
