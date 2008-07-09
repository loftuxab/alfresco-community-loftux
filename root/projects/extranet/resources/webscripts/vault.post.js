var id = args["id"];
var xml = args["xml"];

// find the document
var folder = companyhome.childByNamePath("Credential Vault");
if(folder != null)
{
	var childNode = folder.childByNamePath(id + ".dat");
	if(childNode == null)
	{
		// create the document
		childNode = folder.createFile(id + ".dat");
	}
	
	if(childNode != null)
	{
		childNode.content = xml;
		childNode.save();	
	}
}
