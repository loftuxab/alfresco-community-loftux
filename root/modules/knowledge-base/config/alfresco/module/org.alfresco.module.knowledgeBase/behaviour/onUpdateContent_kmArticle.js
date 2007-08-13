
// Get the article
var node = behaviour.args[0];
var isNewContent = behaviour.args[1];

if (node.hasAspect(kb.ASPECT_ARTICLE) == true && isNewContent == false)
{   
	var status = node.properties[kb.PROP_STATUS].nodeRef.toString();

	if (status == "workspace://SpacesStore/kb:status-published")
	{
   		// Update the SWF rendition
   		logger.log("Update the rendition");
   		kb.updatePublishedArticle(node);
   	}
}

