
// Get all the spaces with 'Gallery' in the title
var nodes = search.luceneSearch("+TYPE:\"cm:folder\" +@cm\\:name:*Gallery*");
model.galleries = nodes;