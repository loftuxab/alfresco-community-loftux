// Call the repo for sites the user is a member of
var result = remote.call("/api/sites?size=5&pos=0");

// Create javascript objects from the server response
var sites = eval('(' + result + ')');

// Prepare the model for the template
model.sites = sites;
