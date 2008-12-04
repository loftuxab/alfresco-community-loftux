<import resource="/include/support.js">
<import resource="/web-studio/api/prebuilt/prebuilt.js">

model.status = null;

// basic arguments for creating the web site
// these are passed over to the WCM Web Project Services
var id = args["id"];
var title = args["name"];
var basedOn = args["basedOn"];
var description = args["description"];
if(description == null)
{
	description = name;
}
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
	
	// These define the context against which we will do site creation
	var storeId = args["storeId"];
	var sandboxId = args["sandboxId"];
	var webappId = args["webappId"];
	if(webappId == null || "" == webappId)
	{
		webappId = "ROOT";
	}

	// plug onto model	
	model.webappId = webappId;
	model.sandboxId = sandboxId;
	model.storeId = storeId;

	// Set focus onto this store
	webstudio.setCurrentWebProject(model.webProjectId);
	webstudio.setCurrentSandbox(model.sandboxId);
	webstudio.setCurrentStore(model.storeId);
	webstudio.setCurrentWebapp(model.webappId);


	// Begin by removing any legacy objects
	// Should not technically be necessary for new sites... but we'll leave it in for now.
	removeSiteObjects();
	

	// Now create the site
	if(basedOn == "none")
	{
		// set up a basic site via scripting
		var siteConfiguration = createSite(title, description);
		
		model.status = 'completed';
	}
	else
	{
		// load the site information from network
		var site = getSite(basedOn);

		// import the archive asynchronously
		// this will give us back a task id that we can check against
		// to see if and when the job finally completes
		var taskId = webstudio.importer.importArchive(model.storeId, model.webappId, site.archiveUrl);
		
		model.status = 'importing';
		model.taskId = taskId;		
	}
}

if(model.status == null)
{
	model.status = 'error';
}