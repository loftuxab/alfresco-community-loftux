// Data Binding: websiteName
// Data Binding: websiteDescription
//



// get data off the XML document
var siteConfiguration = site.getSiteConfiguration();
if(siteConfiguration != null)
{
	websiteName = wizard.getSafeProperty(siteConfiguration, "title");
	websiteDescription = wizard.getSafeProperty(siteConfiguration, "description");
}

// set up form elements
wizard.addElement("websiteName", websiteName);
wizard.addElement("websiteDescription", websiteDescription);
wizard.addElementFormat("websiteName", "Website name", "textfield", 290);
wizard.addElementFormat("websiteDescription", "Description", "textarea", 290);



