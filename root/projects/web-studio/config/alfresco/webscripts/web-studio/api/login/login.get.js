var username = args["u"];
var password = args["pw"];

var success = false;

try
{
	success = webstudio.login(username, password);	
}
catch(err)
{
}
model.success = success;
