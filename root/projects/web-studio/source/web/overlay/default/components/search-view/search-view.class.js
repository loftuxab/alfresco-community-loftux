
if (typeof WebStudio == "undefined")
{
	var WebStudio = {};
}

WebStudio.SearchView = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.ID = index;
	
	this.defaultTemplateSelector = 'div[id=AlfrescoSearchViewTemplate]';
	
	this.defaultElementsConfig = {
		Body: {
			selector: 'div[id=ASVBody]'
		}
	}

	this.events = {};
	
	this.nodes = {};
	this.droppables = [];
}

WebStudio.SearchView.prototype = new WebStudio.AbstractTemplater('WebStudio.SearchView');

WebStudio.SearchView.prototype.activate = function() 
{	
	this.buildGeneralLayer();

	if (this.Body.el) 
	{
		var html = "<div>";
		html += "The Search Control will appear here.";
		html += "<BR/><BR/>";
		html += "Alfresco 3.0 offers a draft implementation of CMIS";
		html += " - a standard way for working with content repositories.  This provides a common interface for execution queries against Alfresco content and metadata.";
		html += "<BR/><BR/>";
		html += "Look for this functionality to appear in an upcoming drop of Alfresco Web Studio!";
		html += "</div>";
		
		this.Body.el.setHTML(html);
	}

	return this;
}

WebStudio.SearchView.prototype.build = function() {
	this.generalLayer.set({
		id: this.ID
	});
}
