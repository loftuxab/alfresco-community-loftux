var id = args["id"];

// find the document
var folder = companyhome.childByNamePath("Credential Vault");
if(folder != null)
{
	var childNode = folder.childByNamePath(id + ".dat");
	if(childNode != null)
	{
		model.xml = childNode.properties.content.content;
		model.vaultId = id;
	}
}
