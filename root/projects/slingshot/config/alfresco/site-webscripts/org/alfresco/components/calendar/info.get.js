var uri = unescape(args['uri']);
// Call the repo for the event information
var result = remote.call(uri);
// Create javascript object from the server response
model.result = eval('(' + result + ')');
