// gives us the user's credential vault
var vault = sitedata.credentialVault;
model.vault = vault;

var endpointIds = remote.getEndpointIds();
model.endpointIds = endpointIds;

// build a map of endpoint names and descriptions
var endpointNames = { };
for(var i = 0; i < endpointIds.length; i++)
{
	var endpointId = endpointIds[i];
	
	endpointNames[endpointId] = remote.getEndpointName(endpointId);
}
model.endpointNames = endpointNames;
var endpointDescriptions = { };
for(var i = 0; i < endpointIds.length; i++)
{
	var endpointId = endpointIds[i];
	
	endpointDescriptions[endpointId] = remote.getEndpointDescription(endpointId);
}
model.endpointDescriptions = endpointDescriptions;
var endpointPersistence = { };
for(var i = 0; i < endpointIds.length; i++)
{
	var endpointId = endpointIds[i];
	
	endpointPersistence[endpointId] = remote.isEndpointPersistent(endpointId);
}
model.endpointPersistence = endpointPersistence;


// process commands
var command = context.properties["command"];
if(command == "remove")
{
	var epId = context.properties["endpointId"];
	if(epId != null)
	{
		vault.removeCredentials(epId);
		vault.save();
	}
}