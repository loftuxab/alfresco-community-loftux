<import resource="/include/support.js">
<import resource="/web-studio/api/prebuilt/prebuilt.js">

var id = args["id"];
var title = args["name"];
var basedOn = args["basedOn"];
var description = args["description"];
if(description == null)
{
	description = name;
}

model.status = null;

// call over to WCM Services API to Create Site

var json = {
	"name" : id,
	"description" : description,
	"title" : title,
	"dnsName" : id
};

var connector = remote.connect("alfresco");
var r = connector.post("/api/wcm/webprojects", json.toJSONString(), "application/json");

var response = eval('(' + r.response + ')');
if(response != null)
{
	// We successfully created the web project
	// We will now set about populating it

	model.webProjectId = id;
	model.sandboxId = id;
	model.storeId = id;
	model.webappId = "ROOT"; // assume ROOT


	// Set focus onto this store
	webstudio.setCurrentWebProject(model.webProjectId);
	webstudio.setCurrentSandbox(model.sandboxId);
	webstudio.setCurrentStore(model.storeId);
	webstudio.setCurrentWebapp(model.webappId);


	// Begin by removing any legacy objects
	// Should not technically be necessary for new sites... but we'll leave it in for now.
	removeSiteObjects();
	
	
	if(basedOn == "none")
	{
		// set up a basic site via scripting
		var siteConfiguration = createSite(title, description);
	}
	else
	{
		var site = getSite(basedOn);
		
		var url = site.archiveUrl;
		logger.log("URL is: " + url);

		// import the archive
		webstudio.importer.importArchive(model.storeId, model.webappId, url);
	}

	model.status = 'ok';
}

if(model.status == null)
{
	model.status = 'error';
}