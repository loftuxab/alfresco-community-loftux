
/**
 * TopicList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DiscussionsTopicList
 */
(function()
{
   /**
    * DocumentList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentList} The new DocumentList instance
    * @constructor
    */
   Alfresco.DiscussionsCreateTopic = function(htmlId)
   {
      this.name = "Alfresco.DiscussionsTopicList";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container", "editor"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DiscussionsCreateTopic.prototype =
   {
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DiscussionsCreateTopic_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DiscussionsCreateTopic_onReady()
      { 
      /*
		 var editor = new YAHOO.widget.SimpleEditor('template.createtopic.discussions-topiclist-description', {
		     height: '300px',
		     width: '522px',
		     dompath: false, //Turns on the bar at the bottom
		     animate: false //Animates the opening, closing and moving of Editor windows
		 });
		 editor.render();
      */
      	 
         var okButton = new YAHOO.widget.Button(this.id + "-ok-button", {type: "submit"});
         //okButton.subscribe("click", this.onOkButtonClick, this, true);
         
         var createTopicForm = new Alfresco.forms.Form(this.id + "-createTopic-form");
         //createSiteForm.addValidation(this.id + "-shortName", Alfresco.forms.validation.mandatory, null, "keyup");
         //createSiteForm.addValidation(this.id + "-shortName", Alfresco.forms.validation.regexMatch, {pattern: /^[^ ]*$/}, "keyup");         
         createTopicForm.setShowSubmitStateDynamically(true, false);
         createTopicForm.setSubmitElements(okButton);
         createTopicForm.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onCreateTopicSuccess,
               scope: this
            }
         });
         createTopicForm.setSubmitAsJSON(true);
         createTopicForm.init();

         var cancelButton = new YAHOO.widget.Button(this.id + "-cancel-button", {type: "button"});
         cancelButton.subscribe("click", this.onCancelButtonClick, this, true);
      },
/*
	Added doBeforeFormSubmit overridable function to forms-runtime.
	e.g. (myForm is Alfresco.forms.Form)
	myForm.doBeforeFormSubmit =
	{
	   fn: function(form, obj)
	   {
	      ... last chance form processing ...
	   },
	   obj: myArbitraryObject,
	   scope: this
	}
*/
      onCancelButtonClick: function(type, args)
      {
            Alfresco.util.PopupManager.displayMessage({text: "Can't cancel the form, just ignore it"});      	
      },

      onCreateTopicSuccess: function(response)
      {
         if (response.json === undefined || response.json.name === undefined || response.json.shortName.name == 0)
         {
            Alfresco.util.PopupManager.displayMessage({text: "Received a success message with missing variables (name)"});
         }
         else
         {
         	Alfresco.util.PopupManager.displayMessage({text: "Topic created with the name " + response.json.name});
            //document.location.href = Alfresco.constants.URL_CONTEXT + "page/collaboration-dashboard?site=" + response.json.shortName;
         }
      }
      
   };
})();
