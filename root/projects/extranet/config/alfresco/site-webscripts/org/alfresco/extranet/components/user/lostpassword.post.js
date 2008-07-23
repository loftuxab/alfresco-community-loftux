var identity = args["identity"];
model.identity = identity;

var success = false;
if(identity != null)
{
	success = extranet.resetUserPassword(identity)
}
model.success = success;
