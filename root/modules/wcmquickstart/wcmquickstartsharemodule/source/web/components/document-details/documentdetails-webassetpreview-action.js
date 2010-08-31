(function()
{
	Alfresco.DocumentActions.prototype.onPreviewWebasset = function WCMQS_onPreviewWebasset(asset)
   {
	  var nodeRef = new Alfresco.util.NodeRef(asset.nodeRef);	 
	   
	  this.modules.actions.genericAction(
      {
    	  success:
          {
             callback:
             {
                fn: function WCMQS_onPreviewWebasset_success(data)
                {
                	window.open(data.json.url);
                },
                scope: this
             }
          },
         failure:
         {
            message: "Unable to preview webasset"
         },
         webscript:
         {
        	stem: Alfresco.constants.PROXY_URI,
            name: "api/webassetpreviewer/{id}",
            method: Alfresco.util.Ajax.GET,
            params:
            {
               id: nodeRef.id
            }
         }
      });
   };
})();