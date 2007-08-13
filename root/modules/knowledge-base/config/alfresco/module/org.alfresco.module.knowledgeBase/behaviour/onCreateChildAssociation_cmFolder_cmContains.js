var folder = behaviour.args[0].parent;
var node = behaviour.args[0].child;

// Check whether the node is a folder or not
if (node.isDocument == true && node.hasAspect("kb:article") == false)
{
	// Look for a knowledge base folder in the parent hierarchy
	var knowledgeBase = kb.getKnowledgeBase(node);
	
	if (knowledgeBase != null)
	{	
		// Link the article to the relevant knowledge base
		node.createAssociation(knowledgeBase, "kb:knowledgeBase");
		
		// Apply the article aspect
		node.addAspect("kb:article");		
		
		// Save any changes
		node.save();
	}
}