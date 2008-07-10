// Get clients json request as a "normal" js object literal
var clientRequest = json.toString();
var clientJSON = eval('(' + clientRequest + ')');

// Call the repo to create the site
var scriptRemoteConnector = remote.connect("alfresco");
var repoResponse = scriptRemoteConnector.post("/api/sites", clientRequest, "application/json");
var repoJSON = eval('(' + repoResponse + ')');

// Create the site in the webtier
var tokens = new Array();
tokens["siteid"] = repoJSON.shortName;
sitedata.newPreset(clientJSON.sitePreset, tokens);

// Prepare the response
model.result = repoResponse;
