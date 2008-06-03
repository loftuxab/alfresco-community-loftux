// gives us the user's credential vault
var vault = sitedata.credentialVault;
model.vault = vault;

// handle the update
if(args["command"] == "update")
{
	for (var key in args)
	{
		if(key.substring(0,6) == "PARAM_")
		{
			var value = args[key];

			var param = key.substring(6);
			var x = param.indexOf("_");
			if(x > -1)
			{
				var endpointId = param.substring(0, x);
				var credentialKey = param.substring(x+1);

				var credentials = vault.properties[endpointId];
				if(credentials == null)
				{
					credentials = vault.newCredentials(endpointId);
				}
				
				credentials.properties[credentialKey] = value;
			}
		}
	}
	
	vault.save();
}
