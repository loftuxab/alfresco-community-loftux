// make a call to the remote alfresco server
// for now, execute a simple webscript that does not require authentication!
var connector = remote.connect("alfresco.com");
model.result = connector.call("/media/releases/feed/");