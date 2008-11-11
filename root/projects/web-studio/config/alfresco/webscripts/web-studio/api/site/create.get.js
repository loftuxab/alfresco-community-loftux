<import resource="/include/support.js">

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
var r = connector.post("/api/wcm/webproject", json.toJSONString(), "application/json");

var response = eval('(' + r.response + ')');
if(response != null)
{
	// We successfully created the web project
	// We will now set about populating it

	model.webProjectId = id;
	model.sandboxId = id;
	model.storeId = id;


	// Set focus onto this store
	webstudio.setCurrentWebProject(model.webProjectId);
	webstudio.setCurrentSandbox(model.sandboxId);
	webstudio.setCurrentStore(model.storeId);
	webstudio.setCurrentWebapp("ROOT"); // assume root


	// Begin by removing any legacy objects
	// Should not technically be necessary for new sites... but we'll leave it in for now.
	removeSiteObjects();


	// Generate the site
	var siteConfiguration = createSite(title, description);
	if(siteConfiguration)
	{
		generateSite(basedOn);
	}		

	model.status = 'ok';
}

if(model.status == null)
{
	model.status = 'error';
}