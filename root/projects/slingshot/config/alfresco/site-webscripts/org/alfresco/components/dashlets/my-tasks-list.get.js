model.myTasks = getMyTasks();

function getMyTasks()
{
	// Call the repo for sites the user is a member of
	var result = remote.call("/slingshot/dashlets/my-tasks?filter=" + args["filter"]);
	if (result.status == 200)
	{
		// Create javascript objects from the server response
		return eval('(' + result + ')');
	}
	return null;
}
