function main()
{
	// Call the repo for sites the user is a member of
	var result = remote.call("/api/people/" + user.name + "/sites");
	if (result.status == 200)
	{
		// Create javascript objects from the server response
		var sites = eval('(' + result + ')');
		
		// Prepare the model for the template
		model.sites = sites;
	}
}

main();