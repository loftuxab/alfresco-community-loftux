var originalPassword = args["originalPassword"];
var newPassword = args["newPassword"];
var newPasswordVerify = args["newPasswordVerify"];
var userId = args["username"];

var success = false;
var message = null;

try
{
	success = extranet.changePassword(userId, originalPassword, newPassword, newPasswordVerify);
	if(success)
	{
		// force reload of user
		sitedata.reloadUser();
	}
}
catch(e)
{
	message = e.message;
}
model.success = success;
model.message = message;
