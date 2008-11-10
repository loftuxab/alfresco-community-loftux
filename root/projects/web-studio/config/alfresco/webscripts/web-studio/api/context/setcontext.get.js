var webProjectId = args["webProjectId"];
var sandboxId = args["sandboxId"];
var storeId = args["storeId"];

webstudio.setCurrentWebProject(webProjectId);
webstudio.setCurrentSandbox(sandboxId);
webstudio.setCurrentStore(storeId);

model.success = true;