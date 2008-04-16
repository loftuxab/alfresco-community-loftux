<import resource="/org/alfresco/web/site/include/ads-support.js">

// Data Binding: websiteName
// Data Binding: websiteDescription
//


// update the site configuration
assertSiteConfiguration(websiteName, websiteDescription);

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Site configuration successfully saved!");
wizard.setBrowserReload(true);

