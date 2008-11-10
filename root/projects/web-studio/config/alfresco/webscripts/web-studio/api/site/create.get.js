<import resource="/include/support.js">

var id = args["id"];
var name = args["name"];
var basedOn = args["basedOn"];
var description = args["description"];
if(description == null)
{
	description = name;
}

model.status = null;

// call over to WCM Services API to Create Site
var argsObject = {
	"id": id,
	"title": name,
	"description": description
};
var argsObjectString = argsObject.toJSONString();

var url = "/api/wcm/webproject?json=" + argsObjectString;

var connector = remote.connect("alfresco");
var r = connector.put(url, { }, "application/json");
var result = eval('(' + r.response + ')');

if(result != null)
{
	if(result.status == 'ok')
	{
		// We successfully created the web project
		// We will now set about populating it
		
		// Set focus onto this store
		webstudio.setCurrentWebProject(result.webProjectId);
		webstudio.setCurrentSandbox(result.sandboxId);
		webstudio.setCurrentStore(result.storeId);

		// Begin by removing any legacy objects
		removeSiteObjects();

		// Generate the site
		var siteConfiguration = createSite(name, description);
		if(siteConfiguration)
		{
			generateSite(basedOn);
		}
		
		model.webProjectId = result.webProjectId;
		model.storeId = result.storeId;
		model.sandboxId = result.sandboxId;
		
		model.status = 'ok';
	}
}

if(model.status == null)
{
	model.status = 'error';
}