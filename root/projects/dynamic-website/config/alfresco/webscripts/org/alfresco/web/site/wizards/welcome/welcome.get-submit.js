<import resource="/org/alfresco/web/site/include/SiteGenerator.js">

// Data Binding: websiteName
// Data Binding: websiteDescription
// Data Binding: prebuiltType
//

// clear the current site
clearSite();

// update the site configuration
assertSiteConfiguration(websiteName, websiteDescription);

// do site prebuild
generateSite(prebuiltType);

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Site configuration complete.  You may now begin building your web site!");
wizard.setBrowserReload(true);

