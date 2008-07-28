var originalUsername = args["originalUsername"];
model.originalUsername = originalUsername;

var username = args["username"];
model.username = username;

var firstName = args["firstName"];
model.firstName = firstName;

var lastName = args["lastName"];
model.lastName = lastName;

var email = args["email"];
model.email = email;

var success = false;
var message = null;
try
{
	success = extranet.setUserProperties(originalUsername, username, email, firstName, lastName);
	if(success)
	{
		// force reload of user
		sitedata.reloadUser();
	}
}
catch(e)
{
	message = e.description;
}
model.success = success;
model.message = message;
