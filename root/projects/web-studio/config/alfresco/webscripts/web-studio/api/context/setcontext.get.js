var webProjectId = args["webProjectId"];
var sandboxId = args["sandboxId"];
var storeId = args["storeId"];
var webappId = args["webappId"];

webstudio.setCurrentWebProject(webProjectId);
webstudio.setCurrentSandbox(sandboxId);
webstudio.setCurrentStore(storeId);

if(webappId == null)
{
	webappId = "ROOT";
	webstudio.setCurrentWebapp(webappId);
}

model.success = true;