// Get the name of the user and place it in the model
model.username = person.properties.firstName + " " + person.properties.lastName;

// Get the visibility of the current user
model.visibility = kb.getUserVisibility(person.properties.userName);

