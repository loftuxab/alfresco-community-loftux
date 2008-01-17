var node = null;

// Find the mediawiki space
if (url.match.endsWith("node/") == true)
{
	node = search.findNode("workspace://SpacesStore/" + url.extension);
}
else
{
	node = companyhome.childByNamePath(url.extension);
}

// Check that we found the mediawiki space
if (node == null)
{
	status.code = 404;
   	status.message = "MediaWiki Space " + url.extension + " not found.";
   	status.redirect = true;
}

// Set the wiki space node value
model.mediawiki = node;

// Get all the config nodes
model.config = node.childAssocs["mw:config"][0];
