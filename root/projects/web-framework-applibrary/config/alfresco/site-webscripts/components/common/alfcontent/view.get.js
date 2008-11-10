var type = instance.properties["type"];

if(type == "person")
{
	var username = instance.properties["username"];
	if(username == null)
	{
		username = context.user.id;
	}
	
	// call over to Alfresco and fetch some content
	var connector = remote.connect("alfresco");
	var data = connector.get("/api/people?filter=" + username);
	model.data = data;
	
	var json = eval('(' + data + ')');
	var people = json["people"];
	for(var i = 0; i < people.length; i++)
	{
		var user = people[i];
		if(user.userName == username)
		{
			model.result = user;
			
			model.avatar = user.avatar;
			model.link = user.url;
			model.userName = user.userName;
			model.title = user.title;
			model.firstName = user.firstName;
			model.lastName = user.lastName;
			model.organisation = user.organisation;
			model.jobtitle = user.jobtitle;
			model.email = user.email;
		}
	}
}

