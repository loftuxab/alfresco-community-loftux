if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.LoginDialog = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.ID = index;
	
	this.defaultTemplateSelector = 'div[id=AlfrescoWebStudioLoginTemplatePanel]';
	
	this.defaultElementsConfig = {
		Username: {
			selector: 'input[id=AlfrescoWebStudioLoginTemplateUsername]'
		},
		Password: {
			selector: 'input[id=AlfrescoWebStudioLoginTemplatePassword]'
		},
		Login: {
			selector: 'input[id=AlfrescoWebStudioLoginTemplateLogin]'
		}
	};

	this.events = {};	
	this.nodes = {};
	this.droppables = [];
};

WebStudio.LoginDialog.prototype = new WebStudio.AbstractTemplater('WebStudio.LoginDialog');

WebStudio.LoginDialog.prototype.activate = function() 
{	
	this.buildGeneralLayer();
	
	// set up on click handler
	this.Login.el.addEvent("click", this.loginHandler);
};

WebStudio.LoginDialog.prototype.setLoginHandler = function(f)
{
	this.loginHandler = f;
};

WebStudio.LoginDialog.prototype.setActive = function() 
{
	//Set this window active, bring to front and apply Active styles
	this.generalLayer.setStyle('z-index', WebStudio.WindowsZIndex + this.zIndexUpper);
	WebStudio.WindowsZIndex++;
	WebStudio.WindowsActive = this;
	return this;
};


WebStudio.LoginDialog.prototype.popup = function() 
{	
	this.block();
	this.show();
	this.centered();
	this.zIndexUpper = 2000;
	this.setActive();
	
	this.Username.el.focus();
};

WebStudio.LoginDialog.prototype.popout = function()
{	
    this.hide();
    this.unblock();
};