// make a call to the remote alfresco server
// for now, execute a simple webscript that does not require authentication!
var connector = remote.connect("alfresco");
model.result = connector.call("/index");