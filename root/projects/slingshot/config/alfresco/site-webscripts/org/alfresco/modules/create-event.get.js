
if (args['uri']) 
{
	var uri = unescape(args['uri']);
	// Call the repo for the event information
	var result = remote.call(uri);
	// Create javascript object from the server response
	model.event = eval('(' + result + ')');	
	//model.event = result;
}
else
{
	model.event = {};
}

