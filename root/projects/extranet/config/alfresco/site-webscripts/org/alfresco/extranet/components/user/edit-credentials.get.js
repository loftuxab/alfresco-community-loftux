// gives us the user's credential vault
var vault = sitedata.credentialVault;
model.vault = vault;

// request arguments
var endpointId = context.properties["endpointId"];
model.endpointId = endpointId;

// endpoint properties
model.endpointName = remote.getEndpointName(endpointId);
model.endpointDescription = remote.getEndpointDescription(endpointId);
model.endpointPersistent = remote.isEndpointPersistent(endpointId);

