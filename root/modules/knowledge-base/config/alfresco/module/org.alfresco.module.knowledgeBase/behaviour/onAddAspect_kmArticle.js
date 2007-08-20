// Get the knowledge base for this article
var node = behaviour.args[0];
var kb = kb.getKnowledgeBase(node);

if (kb != null)
{
	// Get the article count
	var action = actions.create("counter");
	action.execute(kb);
	var articleCount = kb.properties["cm:counter"];
	   
	// Set the kb id
	node.properties["kb:kbId"] = utils.pad(String(articleCount), 4);
	node.save();
}