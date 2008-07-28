// Call the repo for sites the user is a member of
var result = remote.call("/api/people/" + user.name + "/sites");

// Create javascript objects from the server response
var sites = eval('(' + result + ')');

// Prepare the model for the template
model.sites = sites;