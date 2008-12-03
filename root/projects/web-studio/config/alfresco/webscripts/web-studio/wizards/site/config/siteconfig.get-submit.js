<import resource="/include/support.js">

// Data Binding: websiteName
// Data Binding: websiteDescription
//

// get the web site config
var siteConfig = sitedata.getSiteConfiguration();
if(siteConfig != null)
{
	siteConfig.setTitle(websiteName);
	siteConfig.setDescription(websiteDescription);
	siteConfig.save();
}

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Site configuration successfully saved!");
wizard.setBrowserReload(true);

