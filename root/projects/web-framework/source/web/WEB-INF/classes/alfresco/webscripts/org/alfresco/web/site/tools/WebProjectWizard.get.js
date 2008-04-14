var updated = false;

function getWebForm(formName)
{
	var webFormsFolder = companyhome.childByNamePath("Data Dictionary/Web Forms");
	for(var i = 0; i < webFormsFolder.children.length; i++)
	{
		var webForm = webFormsFolder.children[i];
		if(webForm.name == formName)
			return webForm;
	}
	return null;
}

function associateWebForm(webProjectNode, formName)
{
	var webForm = getWebForm(formName);

	var name = "Web Form - " + formName;
	var type = "{http://www.alfresco.org/model/wcmappmodel/1.0}webform";
	var assocName = "{http://www.alfresco.org/model/wcmappmodel/1.0}webform";
	
	// check whether a web form association already exists
	var testNode = null;
	for(var i = 0; i < webProjectNode.children.length; i++)
	{
		var node = webProjectNode.children[i];
		if(node.type == "{http://www.alfresco.org/model/wcmappmodel/1.0}webform")
		{
			var fName = node.properties["{http://www.alfresco.org/model/wcmappmodel/1.0}formname"];
			if(fName == formName)
				testNode = node;
		}
	}
	
	if(testNode == null)
	{
		var webFormChildNode = webProjectNode.createNode(name, type, assocName);	
		webFormChildNode.properties["{http://www.alfresco.org/model/wcmappmodel/1.0}formname"] = webForm.name;
		webFormChildNode.properties["{http://www.alfresco.org/model/content/1.0}title"] = webForm.properties["{http://www.alfresco.org/model/content/1.0}title"];	
		webFormChildNode.properties["{http://www.alfresco.org/model/wcmappmodel/1.0}outputpathpattern"] = webForm.properties["{http://www.alfresco.org/model/wcmappmodel/1.0}outputpathpattern"];
		webFormChildNode.save();
		updated = true;
	}
}


// get all of the web projects
var webProjects = new Array();
var webProjectFolder = companyhome.childByNamePath("Web Projects");
for(var i = 0; i < webProjectFolder.children.length; i++)
{
	var child = webProjectFolder.children[i];
	var childType = child.type;
	if("{http://www.alfresco.org/model/wcmappmodel/1.0}webfolder" == childType)
	{
		webProjects[webProjects.length] = child;
	}
}
model.webProjects = webProjects;


// they clicked the submit button
var submit = args["submit"];
if(submit != null && submit.length > 0)
{
	var webProjectId = args["webProjectId"];
	var webProjectNode = search.findNode("workspace://SpacesStore/" + webProjectId);
	if(webProjectNode != null)
	{	
		// do web form associations to core ADW objects
		associateWebForm(webProjectNode, "component");
		associateWebForm(webProjectNode, "component-association");
		associateWebForm(webProjectNode, "component-type");
		associateWebForm(webProjectNode, "endpoint");
		associateWebForm(webProjectNode, "layout");
		associateWebForm(webProjectNode, "layout-type");
		associateWebForm(webProjectNode, "node");
		associateWebForm(webProjectNode, "node-association");
		associateWebForm(webProjectNode, "page");
		associateWebForm(webProjectNode, "page-association");
		associateWebForm(webProjectNode, "site-configuration");
		associateWebForm(webProjectNode, "template");
		associateWebForm(webProjectNode, "template-association");

		// do web form associations to additional types (if available)
		associateWebForm(webProjectNode, "article");
		
		if(updated == true)
			model.report = "Finished updating the web project";
	}
}
