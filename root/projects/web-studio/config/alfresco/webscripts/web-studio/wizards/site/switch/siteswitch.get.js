<import resource="/include/support.js">

wizard.addElement("webproject", "");
wizard.addElementFormat("webproject", "Web Project", "combo", 290);
wizard.addElementFormatKeyPair("webproject", "emptyText", "Please select a Web Site");
wizard.addElementFormatKeyPair("webproject", "title", "Web Site");

// fetch selection values
//var json = doGetCall("/api/wcm/webproject/list");
// NEW METHOD
var json = doGetCall("/api/wcm/webprojects");
for(var i = 0; i < json.results.length; i++)
{
	var id = json.results[i].webprojectref;
	var title = json.results[i].title;
	wizard.addElementSelectionValue("webproject", id, title);
}
