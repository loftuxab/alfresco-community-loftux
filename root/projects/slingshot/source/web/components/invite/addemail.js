/**
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
 
/**
 * AddEmailInvite component.
 * 
 * @namespace Alfresco
 * @class Alfresco.AddEmailInvite
 */
(function()
{
   
   /**
    * AddEmailInvite constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.AddEmailInvite} The new AddEmailInvite instance
    * @constructor
    */
   Alfresco.AddEmailInvite = function(htmlId)
   {
      this.name = "Alfresco.AddEmailInvite";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["event", "button", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);
   
      return this;
   }
   
   Alfresco.AddEmailInvite.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.AddEmailInvite} returns 'this' for method chaining
       */
      setOptions: function AddEmailInvite_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.AddEmailInvite} returns 'this' for method chaining
       */
      setMessages: function AddEmailInvite_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function AddEmailInvite_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function AddEmailInvite_onReady()
      {  
         // listen on ok button click
         this.widgets.addEmailButton = Alfresco.util.createYUIButton(this, "add-email-button", this.addEmailButtonClick);         
      },

      /**
       * Add email button click
       */
      addEmailButtonClick: function DLTB_onNewFolder(e, p_obj)
      {
         // fetch the firstname, lastname nad email
         var firstNameElem = YAHOO.util.Dom.get(this.id + "-firstname");
         var firstName = firstNameElem.value;
         var lastNameElem = YAHOO.util.Dom.get(this.id + "-lastname");
         var lastName = lastNameElem.value;
         var emailElem = YAHOO.util.Dom.get(this.id + "-email");
         var email = emailElem.value;
         
         // check whether we got enough information to proceed
         if (firstName.length < 1 || lastName.length < 1 || email.length < 1)
         {
            Alfresco.util.PopupManager.displayMessage({text: "All fields need to be filled out!" });
            return;
         }
         
         // send a onAddInvite bubble event to add the new address to the
         // invitee list
         YAHOO.Bubbling.fire("onAddInvite",
         {
            firstName : firstName,
            lastName : lastName,
            email : email
         });
            
         // clear the values
         firstNameElem.value = "";
         lastNameElem.value = "";
         emailElem.value = "";
      },
            
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function AddEmailInvite__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.AddEmailInvite", Array.prototype.slice.call(arguments).slice(1));
      }

   };
})();
