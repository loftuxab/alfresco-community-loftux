(function()
{
	Alfresco.DocumentList.prototype.onReady = function WCMQS_toolbar_onReady()
    {
       // New Content menu button
       this.widgets.createContent = Alfresco.util.createYUIButton(this, "createContent-button", this.onCreateContent,
       {
          type: "menu", 
          menu: "createContent-menu",
          lazyloadmenu: false,
          disabled: true,
          value: "create"
       });

       this.widgets.newArticleButton = Alfresco.util.createYUIButton(this, "newArticle-button", this.onNewArticle,
       {
          disabled: true,
          value : "create"
       });
       
       // New Folder button: user needs "create" access
       this.widgets.newFolder = Alfresco.util.createYUIButton(this, "newFolder-button", this.onNewFolder,
       {
          disabled: true,
          value: "create"
       });
       
       // File Upload button: user needs  "create" access
       this.widgets.fileUpload = Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload,
       {
          disabled: true,
          value: "create"
       });

       // Selected Items menu button
       this.widgets.selectedItems = Alfresco.util.createYUIButton(this, "selectedItems-button", this.onSelectedItems,
       {
          type: "menu", 
          menu: "selectedItems-menu",
          lazyloadmenu: false,
          disabled: true
       });

       // Customize button
       this.widgets.customize = Alfresco.util.createYUIButton(this, "customize-button", this.onCustomize);

       // Hide/Show NavBar button
       this.widgets.hideNavBar = Alfresco.util.createYUIButton(this, "hideNavBar-button", this.onHideNavBar);
       this.widgets.hideNavBar.set("label", this.msg(this.options.hideNavBar ? "button.navbar.show" : "button.navbar.hide"));
       Dom.setStyle(this.id + "-navBar", "display", this.options.hideNavBar ? "none" : "block");
       
       // RSS Feed link button
       this.widgets.rssFeed = Alfresco.util.createYUIButton(this, "rssFeed-button", null, 
       {
          type: "link"
       });

       // Folder Up Navigation button
       this.widgets.folderUp =  Alfresco.util.createYUIButton(this, "folderUp-button", this.onFolderUp,
       {
          disabled: true
       });

       // DocLib Actions module
       this.modules.actions = new Alfresco.module.DoclibActions(this.options.workingMode);
       
       // Reference to Document List component
       this.modules.docList = Alfresco.util.ComponentManager.findFirst("Alfresco.DocumentList");

       // Preferences service
       this.services.preferences = new Alfresco.service.Preferences();

       // Finally show the component body here to prevent UI artifacts on YUI button decoration
       Dom.setStyle(this.id + "-body", "visibility", "visible");
    },
	
	Alfresco.DocumentList.prototype.onNewArticle = function WCMQS_toolbar_onNewArticle(e, p_obj)
	{
    	alert("Hello World!");
	   
	};
})();