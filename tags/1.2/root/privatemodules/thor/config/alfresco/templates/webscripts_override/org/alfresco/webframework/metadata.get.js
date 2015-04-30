model.includeChildren = true;
model.includeContent = false;
model.isUser = false;

model.code = "ERROR";

var object = null;

// allow for content to be loaded from id
if (args["id"] != null)
{
	var id = args["id"];
	object = search.findNode(id);
}

// if not by id, then allow for user id
else if (args["user"] != null)
{
   var userId = args["user"];
   
   model.isUser = true; 
   model.includeChildren = false;
   
   object = people.getPerson(userId);
   
   var fullProfile = people.isFullProfileVisible(userId);
   model.fullProfile = fullProfile;
   
   model.homeTenant = userTenant.getHomeTenant(userId);
   model.capabilities = people.getCapabilities(object);
      
   if (fullProfile == true)
   {
      model.immutableProperties = people.getImmutableProperties(userId);
      
      model.defaultTenant = userTenant.getDefaultTenant(userId);
      model.secondaryTenants = userTenant.getSecondaryTenants(userId);
      model.accountType = userTenant.getAccountType(userId);
   }
}

// load content by relative path
else
{
	var path = args["path"];
	if (path == null || path == "" || path == "/")
	{
		path = "/Company Home";
	}
	
	// look up the content by path
	object = roothome.childByNamePath(path);
}

if (object != null)
{
	model.code = "OK";
}

model.object = object;