// make a POST call to the remote alfresco server
var connector = remote.connect("alfresco");
model.result = connector.post("/index", "no data actually required for this post");