<import resource="/org/alfresco/web/site/include/ads-support.js">


// add a new child page
addChildPage(parentPageId, pageName, pageDescription);


// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Page successfully added!");
//setBrowserReload(true);

