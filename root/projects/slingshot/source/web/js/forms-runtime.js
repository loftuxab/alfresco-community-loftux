// Ensure Alfresco.forms and validation objects exist
Alfresco.forms = Alfresco.forms || {};
Alfresco.forms.validation = Alfresco.forms.validation || {};

/**
 * Class to represent the forms runtime.
 * 
 * @namespace Alfresco.forms
 * @class Alfresco.forms.Form
 */
(function()
{
   /**
    * Constructor for a form.
    * 
    * @param {String} formId The HTML id of the form to be managed
    * @return {Alfresco.forms.Form} The new Form instance
    * @constructor
    */
   Alfresco.forms.Form = function(formId)
   {
      this.formId = formId;
      this.validateOnSubmit = true;
      this.validateAllOnSubmit = false;
      this.showSubmitStateDynamically = false;
      this.showSubmitStateDynamicallyErrors = false;
      this.submitAsJSON = false;
      this.submitElements = [];
      this.validations = [];
      this.ajaxSubmit = false;
      this.ajaxSubmitMethod = "POST";
      this.errorContainer = "alert";

      return this;
   };
   
   Alfresco.forms.Form.prototype =
   {

      /**
       * HTML id of the form being represented.
       * 
       * @property formId
       * @type string
       */
      formId: null,

      /**
       * List of ids and/or elements being used to submit the form.
       * 
       * @property submitElements
       * @type object[]
       */
      submitElements: null,
      
      /**
       * Flag to indicate whether the form will validate upon submission, true
       * by default.
       * 
       * @property validateOnSubmit
       * @type boolean
       */
      validateOnSubmit: null,
      
      /**
       * Flag to indicate whether the form will validate all fields upon submission.
       * The default is false which will stop after the first validation failure,
       * true will validate all fields and thus show all errors.
       * 
       * @property validateAllOnSubmit
       * @type boolean
       */
      validateAllOnSubmit: null,

      /**
       * Flag to determine whether the submit elements dynamically update
       * their state depending on the current values in the form.
       * 
       * @property showSubmitStateDynamically
       * @type boolean
       */
      showSubmitStateDynamically: null,
      
      /**
       * Flag to determine whether any errors are shown when the dynamic
       * submit state option is enabled.
       * 
       * @property showSubmitStateDynamicallyErrors
       * @type boolean
       */
      showSubmitStateDynamicallyErrors: null,
      
      /**
       * Flag to determine whether the form will be submitted using an AJAX request.
       * 
       * @property ajaxSubmit
       * @type boolean
       */
      ajaxSubmit: null,
      
      /**
       * String representing where errors should be displayed. 
       * If the value is not "alert" it's presumed the string is the id of an 
       * HTML object to be used as the error container.
       * 
       * @property errorContainer
       * @type string
       */
      errorContainer: null,
      
      /**
       * Object literal containing the abstract function for pre-submission form processing.
       *   fn: function, // The override function.
       *   obj: object, // An object to pass back to the function.
       *   scope: object // The object to use for the scope of the function.
       * 
       * @property doBeforeFormSubmit
       * @type object
       */
      doBeforeFormSubmit:
      {
         fn: function(form, obj){},
         obj: null,
         scope: this
      },
      
      /**
       * Object literal containing the abstract function for intercepting AJAX form submission.
       * Returning false from the override will prevent the Forms Runtime from submitting the data.
       *   fn: function, // The override function.
       *   obj: object, // An object to pass back to the function.
       *   scope: object // The object to use for the scope of the function.
       * 
       * @property doBeforeAjaxRequest
       * @type object
       */
      doBeforeAjaxRequest:
      {
         fn: function(form, obj)
         {
            return true;
         },
         obj: null,
         scope: this
      },
      
      /**
       * Object holding the callback handlers and messages for AJAX submissions.
       * The callback handlers are themselves an object of the form:
       *   fn: function, // The handler to call when the event fires.
       *   obj: object, // An object to pass back to the handler.
       *   scope: object // The object to use for the scope of the handler.
       * 
       * @property ajaxSubmitHandlers
       * @type object
       */
      ajaxSubmitHandlers: null,
      
      /**
       * String representing the http method to be used for the
       * ajax call. Default is POST.
       * 
       * @property ajaxSubmitMethod
       * @type String
       */
      ajaxSubmitMethod: null,
      
      /**
       * Flag to determine whether the form data should be submitted 
       * represented by a JSON structure.
       * 
       * @property submitAsJSON
       * @type boolean
       */
      submitAsJSON: null,
      
      /**
       * List of validations to execute when the form is submitted.
       * 
       * @property validations
       * @type object[]
       */
      validations: null,
      
      /**
       * Sets up the required event handlers and prepares the form for use.
       * NOTE: This must be called after all other setup methods.
       * 
       * @method init
       */
      init: function()
      {
         var form = document.getElementById(this.formId);
         if (form != null)
         {
            if (form.getAttribute("forms-runtime") != "listening")
            {
               // add the event to the form and make the scope of the handler this form.
               YAHOO.util.Event.addListener(form, "submit", this._submitInvoked, this, true);
               form.setAttribute("forms-runtime", "listening");
               if (this.ajaxSubmit)
               {
                  form.setAttribute("onsubmit", "return false;");
               }
               
               var me = this;
               
               /**
                * Prevent the Enter key from causing a double form submission
                */
               var fnStopEvent = function(id, keyEvent)
               {
                  var event = keyEvent[1];
                  var targetName = event.target.name;
                  if (targetName && (targetName != "-"))
                  {
                     me._submitInvoked(event);
                     form.attributes.action.nodeValue = "";
                  }
               }
               
               var enterListener = new YAHOO.util.KeyListener(form,
               {
                  keys: YAHOO.util.KeyListener.KEY.ENTER
               },
               fnStopEvent, "keydown");
               enterListener.enable();
            }
            
            // determine if the AJAX and JSON submission should be enabled
            if (form.enctype && form.enctype === "application/json")
            {
               this.ajaxSubmit = true;
               this.submitAsJSON = true;
            }
            
            // setup the submit elements if the feature is enabled
            if (this.showSubmitStateDynamically)
            {
               // find the default submit elements if there are no submitIds set
               if (this.submitElements.length == 0)
               {
                  // use a selector to find any submit elements for the form
                  var nodes = YAHOO.util.Selector.query('#' + this.formId + ' > input[type="submit"]');
                  for (var x = 0; x < nodes.length; x++)
                  {
                     var elem = nodes[x];
                     this.submitElements.push(elem.id);
                  }
               }
               
               // make sure the submit elements start in the correct state
               this.updateSubmitElements();
            }
            
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Initialised form: ", this);
         }
         else
         {
            this._showInternalError("form with id of '" + this.formId + 
                  "' could not be located, ensure the form is created after the form element is available.");
         }
      },
      
      /**
       * Enables or disables validation when the form is submitted.
       * 
       * @method setValidateOnSubmit
       * @param validate {boolean} true to validate on submission, false
       *        to avoid validation
       */
      setValidateOnSubmit: function(validate)
      {
         this.validateOnSubmit = validate;
      },
      
      /**
       * Sets whether all fields are validated when the form is submitted.
       * 
       * @method setValidateAllOnSubmit
       * @param validate {boolean} true to validate all fields on submission, false
       *        to stop after the first validation failure
       */
      setValidateAllOnSubmit: function(validateAll)
      {
         this.validateAllOnSubmit = validateAll;
      },
      
      /**
       * Sets the list of ids and/or elements being used to submit the form.
       * By default the forms runtime will look for and use the first
       * input field of type submit found in the form being managed.
       * 
       * @method setSubmitElements
       * @param submitElements {object | object[]} Single object or array of objects
       */
      setSubmitElements: function(submitElements)
      {
         if (!YAHOO.lang.isArray(submitElements))
         {
            this.submitElements[0] = submitElements;
         }
         else
         {
            this.submitElements = submitElements;
         }
      },
      
      /**
       * Sets the container where errors will be displayed.
       * 
       * @method setErrorContainer
       * @param position {string} String representing where errors should
       *        be displayed. If the value is not "alert" it's presumed the 
       *        string is the id of an HTML object to be used as the error 
       *        container
       */
      setErrorContainer: function(container)
      {
         this.errorContainer = container;
      },
      
      /**
       * Sets a field as being repeatable, this adds a 'plus' sign after the field 
       * thus allowing multiple values to be entered.
       * 
       * @method setRepeatable
       * @param fieldId {string} Id of the field the validation is for
       * @param containerId {string} Id of the element representing the 
       *        field 'prototype' i.e. the item that will get cloned.
       */
      setRepeatable: function(fieldId, containerId)
      {
         alert("not implemented yet");
      },
      
      /**
       * Sets whether the submit elements dynamically update
       * their state depending on the current values in the form.
       * The visibility of errors can be controlled via the
       * showErrors parameter.
       * 
       * @method setShowSubmitStateDynamically
       * @param showState {boolean} true to have the elements update dynamically
       * @param showErrors {boolean} true to show any validation errors that occur
       */
      setShowSubmitStateDynamically: function(showState, showErrors)
      {
         this.showSubmitStateDynamically = showState;
         
         if (showErrors)
         {
            this.showSubmitStateDynamicallyErrors = showErrors;
         }
      },
      
      /**
       * Enables or disables whether the form submits via an AJAX call.
       * 
       * @method enableAJAXSubmit
       * @param ajaxSubmit {boolean} true to submit using AJAX, false to submit
       *        using the browser's default behaviour
       * @param callbacks {object} Optional object representing callback handlers 
       *        or messages to use, for example
       *        { 
       *           successCallback: yourHandlerObject,
       *           failureCallback: yourHandlerObject,
       *           successMessage: yourMessage,
       *           failureMessage: yourMessage
       *        }
       *        Callback handler objects are of the form:
       *        { 
       *           fn: function, // The handler to call when the event fires.
       *           obj: object, // An object to pass back to the handler.
       *           scope: object // The object to use for the scope of the handler.
       *        }
       */
      setAJAXSubmit: function(ajaxSubmit, callbacks)
      {
         this.ajaxSubmit = ajaxSubmit;
         this.ajaxSubmitHandlers = callbacks;
      },
      
      /**
       * Enables or disables submitting the form data in JSON format.
       * Setting the enctype attribute of the form to "application/json"
       * in Firefox will achieve the same result.
       * 
       * @method setSubmitAsJSON
       * @param submitAsJSON {boolean} true to submit the form data as JSON, 
       *        false to submit one of the standard types "multipart/form-data"
       *        or "application/x-www-form-urlencoded" depending on the enctype
       *        attribute on the form
       */
      setSubmitAsJSON: function(submitAsJSON)
      {
         this.submitAsJSON = submitAsJSON;
      },

      /**
       * Set the http method to use for the AJAX call.
       * 
       * @method setAjaxSubmitMethod
       * @param ajaxSubmitMethod {string} the http method to use for the AJAX call
       */
      setAjaxSubmitMethod: function(ajaxSubmitMethod)
      {
         this.ajaxSubmitMethod = ajaxSubmitMethod;
      },
      
      /**
       * Adds validation for a specific field on the form.
       * 
       * @method addValidation
       * @param fieldId {string} Id of the field the validation is for
       * @param validationHandler {function} Function to call to handle the 
       *        actual validation
       * @param validationArgs {object} Optional object representing the 
       *        arguments to pass to the validation handler function
       * @param when {string} Name of the event the validation should fire on
       *        can be any event applicable for the field for example on a text
       *        field "blur" can be used to fire the validation handler as the 
       *        user leaves the field. If null, the validation is only called
       *        upon form submission. 
       */
      addValidation: function(fieldId, validationHandler, validationArgs, when)
      {
         var field = document.getElementById(fieldId);
         if (field == null)
         {
            this._showInternalError("element with id of '" + fieldId + "' could not be located.");
            return;
         }
         
         // create object representation of validation
         var validation = {
            fieldId: fieldId,
            args: validationArgs,
            handler: validationHandler
         };
         
         // add to list of validations
         this.validations.push(validation);
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Added submit validation for field: " + fieldId +
                                  ", using handler: " + 
                                  (validationHandler.name || YAHOO.lang.dump(validationHandler)) + 
                                  ", args: " + YAHOO.lang.dump(validationArgs));
      
         // if an event has been specified attach an event handler
         if (when != null && when.length > 0)
         {
            // add the event to the field, pass the validation as a paramter 
            // to the handler and make the scope of the handler this form.
            YAHOO.util.Event.addListener(field, when, this._validationEventFired, validation, this);
            
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Added field validation for field: " + fieldId +
                                     ", using handler: " + 
                                     (validationHandler.name || YAHOO.lang.dump(validationHandler)) + 
                                     ", args: " + YAHOO.lang.dump(validationArgs) +
                                     ", on event: " + when);
         }
      },
      
      /**
       * Adds an error to the form.
       * 
       * @method addError
       * @param msg {string} The error message to display
       * @param field {object} The element representing the field the error occurred on
       */
      addError: function(msg, field)
      {
         // TODO: Allow an error handler to be plugged in which
         //       would allow for custom error handling
         
         if (this.errorContainer != null)
         {
            if (this.errorContainer === "alert")
            {
               alert(msg);
            }
            else
            {
               var htmlNode = document.getElementById(this.errorContainer);
               if (htmlNode != null)
               {
                  htmlNode.style.display = "block";
                  
                  var before = htmlNode.innerHTML;
                  var after = htmlNode.innerHTML + "<div>" + msg + "</div>";
                  htmlNode.innerHTML = after;
               }
            }
         }
      },
      
      /**
       * Retrieves the label text for a field
       * 
       * @method getFieldLabel
       * @param fieldId {string} The id of the field to get the label for
       * @return {string} The label for the field or the fieldId if a label could not be found
       */
      getFieldLabel: function(fieldId)
      {
         var label = null;
         
         // lookup the label using the "for" attribute (use the first if multiple found)
         //var nodes = YAHOO.util.Selector.query('label[for="' + fieldId + '"]');
         var nodes = YAHOO.util.Selector.query('label');
         // NOTE: there seems to be a bug in getting label using 'for' or 'htmlFor'
         //       for now get all labels and find the one we want
         if (nodes.length > 0)
         {
            for (var x = 0; x < nodes.length; x++)
            {
               var elem = nodes[x];
               if (elem["htmlFor"] == fieldId)
               {
                  // get the text for the label
                  label = elem.firstChild.nodeValue;
               }
            }
         }
         
         // default to the field id if the label element was not found
         if (label == null)
         {
            label = fieldId;
         }
         
         return label;
      },
      
      /**
       * Applies a Key Listener to input fields to ensure tabbing only targets elements
       * that specifically set a "tabindex" attribute.
       * This has only been seen as an issue with the Firefox web browser, so shouldn't be applied otherwise.
       *
       * @method applyTabFix
       */
      applyTabFix: function()
      {
         if (YAHOO.env.ua.gecko > 0)
         {
            /**
             * Ensure the Tab key only focusses relevant fields
             */
            var form = YAHOO.util.Dom.get(this.formId);
            
            var fnTabFix = function(id, keyEvent)
            {
               var event = keyEvent[1];
               var target = event.target;
               if (!target.hasAttribute("tabindex"))
               {
                  YAHOO.util.Event.stopEvent(event);
                  YAHOO.util.Selector.query("[tabindex]", form)[0].focus();
               }
            }
            
            var tabListener = new YAHOO.util.KeyListener(form,
            {
               keys: YAHOO.util.KeyListener.KEY.TAB
            },
            fnTabFix, "keyup");
            tabListener.enable();
         }
      },

      /**
       * Updates the state of all submit elements.
       * 
       * @method updateSubmitElements
       */
      updateSubmitElements: function()
      {
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Determining whether submit elements can be enabled...");
         
         // run all validations silently to see if submit elements can be enabled
         var valid = this._runValidations(true);
         
         // make sure all submit elements show correct state
         for (var x = 0; x < this.submitElements.length; x++)
         {
            var currentItem = this.submitElements[x];
            
            if (typeof currentItem == "string")
            {
               // get the element with the id and set the disabled attribute
               var elem = document.getElementById(currentItem);
               elem.disabled = !valid;
            }
            else
            {
               // TODO: for now if an object is passed presume it's a YUI button
               currentItem.set("disabled", !valid);
            }
         }
      },
      
      
      /**
       * Clears any errors displayed by previous validation failures.
       * 
       * @method _clearErrors
       * @private
       */
      _clearErrors: function()
      {
         if (this.errorContainer !== "alert")
         {
            var htmlNode = document.getElementById(this.errorContainer);
            if (htmlNode != null)
            {
               htmlNode.style.display = "none";
               htmlNode.innerHTML = "";
            }
         }
      },
      
      /**
       * Event handler called when a validation event is fired by any registered field.
       * 
       * @method _validationEventFired
       * @param event {object} The event
       * @param validation {object} Object representing the validation to execute, consists of 
       *        'fieldId', 'args' and 'handler' properties.
       * @private
       */
      _validationEventFired: function(event, validation)
      {
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Event has been fired for field: " + validation.fieldId);
         
         var silent = false;
         
         // if dynamic updating is enabled
         if (this.showSubmitStateDynamically)
         {
            if (this.showSubmitStateDynamicallyErrors)
            {
               // if errors are being shown clear previous ones
               this._clearErrors();
            }
            else
            {
               // otherwise hide errors
               silent = true;
            }
         }
         
         // call handler
         validation.handler(YAHOO.util.Event.getTarget(event), validation.args, event, this, silent);
         
         // update submit elements state, if required
         if (this.showSubmitStateDynamically)
         {
            this.updateSubmitElements();
         }
      },
      
      /**
       * Event handler called when the form is submitted.
       * 
       * @method _submitInvoked
       * @param event {object} The event
       * @private
       */
      _submitInvoked: function(event)
      {
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Submit invoked on form: ", this);
         
         // clear any errors that may be visible
         this._clearErrors();
         
         if (this.validateOnSubmit)
         {
            if (this._runValidations(false))
            {
               // validation was successful
               // get the form element
               var form = document.getElementById(this.formId);

               // call the pre-submit function, passing the form for last-chance processing
               this.doBeforeFormSubmit.fn.call(this.doBeforeFormSubmit.scope, form, this.doBeforeFormSubmit.obj);

               // should submission be done using AJAX, or let 
               // the browser do the submit?
               if (this.ajaxSubmit)
               {
                  // stop the browser from submitting the form
                  YAHOO.util.Event.stopEvent(event);
                  
                  // get the form's action URL
                  var submitUrl = form.attributes.action.nodeValue;
                  
                  if (Alfresco.logger.isDebugEnabled())
                     Alfresco.logger.debug("Performing AJAX submission to url: ", submitUrl);
                  
                  // determine how to submit the form, if the enctype
                  // on the form is set to "application/json" then
                  // package the form data as an AJAX string and post
                  if (form.enctype && form.enctype === "multipart/form-data")
                  {
                     this._showInternalError("AJAX multipart/form-data submission is not supported");
                     return;
                  }
                  
                  // create config object to pass to request helper
                  var config =
                  {
                     method: this.ajaxSubmitMethod,
                     url: submitUrl
                  };

                  if (this.ajaxSubmitHandlers)
                  {
                     if (this.ajaxSubmitHandlers.successCallback)
                     {
                        config.successCallback = this.ajaxSubmitHandlers.successCallback;
                     }
                     
                     if (this.ajaxSubmitHandlers.successMessage)
                     {
                        config.successMessage = this.ajaxSubmitHandlers.successMessage;
                     }

                     if (this.ajaxSubmitHandlers.failureCallback)
                     {
                        config.failureCallback = this.ajaxSubmitHandlers.failureCallback;
                     }
                     
                     if(this.ajaxSubmitHandlers.failureMessage)
                     {
                        config.failureMessage = this.ajaxSubmitHandlers.failureMessage;
                     }
                  }
                  if (this.submitAsJSON)
                  {
                     var jsonData = this._buildAjaxForSubmit(form);
                     
                     // set up specific config
                     config.dataObj = jsonData;
                     
                     // call the pre-request function, passing the config object for last-chance processing
                     if (this.doBeforeAjaxRequest.fn.call(this.doBeforeAjaxRequest.scope, config, this.doBeforeAjaxRequest.obj))
                     {
                        if (Alfresco.logger.isDebugEnabled())
                           Alfresco.logger.debug("Submitting JSON data: ", jsonData);

                        Alfresco.util.Ajax.jsonRequest(config);
                     }
                     else
                     {
                        if (Alfresco.logger.isDebugEnabled())
                           Alfresco.logger.debug("JSON data request cancelled in doBeforeAjaxRequest()");
                     }
                  }
                  else
                  {
                     if (Alfresco.logger.isDebugEnabled())
                        Alfresco.logger.debug("Submitting data in form: ", form.enctype);
                     
                     // set up specific config 
                     config.dataForm = form;
                     Alfresco.util.Ajax.request(config);
                  }
               }
            }
            else
            {
               // stop the event from continuing and sending the form.
               YAHOO.util.Event.stopEvent(event);
            }
         }
         else
         {
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Ignoring validations as submission validation is disabled");
         }
      },
      
      /**
       * Builds a JSON representation of the current form
       * 
       * @method _buildAjaxForSubmit
       * @param form {object} The form object to build the JSON for
       * @private
       */
      _buildAjaxForSubmit: function(form)
      {
         if (form != null)
         {
            var formData = {};
            var length = form.elements.length;
            for (var i = 0; i < length; i++)
            {
               var element = form.elements[i];
               var name = element.name;
               if (name == "-" || element.disabled)
               {
                  continue;
               }
               if (name == undefined || name == "")
               {
                  name = element.id;
               }
               var value = element.value;
               if (name)
               {
                  // check whether the input element is an array value
                  if ((name.length > 2) && (name.substring(name.length - 2) == '[]'))
                  {
                     name = name.substring(0, name.length - 2);
                     if (formData[name] == undefined)
                     {
                        formData[name] = new Array();
                     }
                     formData[name].push(value);
                  }
                  // check whether the input element is an object literal value
                  else if (name.indexOf(".") > 0)
                  {
                     var names = name.split(".");
                     var obj = formData;
                     var index;
                     for (var j = 0, k = names.length - 1; j < k; j++)
                     {
                        index = names[j];
                        if (obj[index] === undefined)
                        {
                           obj[index] = {};
                        }
                        obj = obj[index];
                     }
                     obj[names[j]] = value;
                  }
                  else if (!(element.type === "checkbox" && !element.checked))
                  {
                     formData[name] = value;
                  }
               }
            }
            
            return formData;
         }
      },
      
      /**
       * Executes all registered validations and returns result.
       * 
       * @method _runValidations
       * @param silent {boolean} Determines whether the validation checks are run silently
       * @private
       */
      _runValidations: function(silent)
      {
         var atLeastOneFailed = false;
         
         // iterate through the validations
         for (var x = 0; x < this.validations.length; x++)
         {
            var val = this.validations[x];
                  
            var field = document.getElementById(val.fieldId);
            if (field != null)
            {
               if (!val.handler(field, val.args, null, this, silent))
               {
                  atLeastOneFailed = true;
                  
                  if (!this.validateAllOnSubmit)
                  {
                     // if silent is false set the focus on the field that failed.
                     if (!silent)
                     {
                        field.focus();
                     }
                     
                     // stop if we aren't validating all fields
                     break;
                  }
               }
            }
         }
         
         return !atLeastOneFailed;
      },
      
      /**
       * Displays an internal form error message.
       * 
       * @method _showInternalError
       * @param msg {string} The error message to display
       * @param field {object} The element representing the field the error occurred on
       * @private 
       */
      _showInternalError: function(msg, field)
      {
         this.addError("Internal Form Error: " + msg, field);
      }
   };
})();

/**
 * Mandatory validation handler, tests that the given field has a value.
 * 
 * @method mandatory
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @static
 */
Alfresco.forms.validation.mandatory = function mandatory(field, args, event, form, silent)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating mandatory state of field '" + field.id + "'");
   
   var valid = true; 
      
   if (field.type && field.type == "radio")
   {
      // TODO: Do we actually need to support this scenario?
      //       wouldn't a radio button normally have a default
      //       'checked' option?
      
      var formElem = document.getElementById(form.formId);
      var radios = formElem[field.name];
      var anyChecked = false;
      for (var x = 0; x < radios.length; x++)
      {
         if (radios[x].checked)
         {
            anyChecked = true;
            break;
         }
      }
      
      valid = anyChecked;
   }
   else
   {
      valid = field.value.length != 0;
   }
   
   if (!valid && !silent && form !== null)
   {
      // if the keyCode from the event is the TAB or SHIFT keys don't show the error
      if (event && event.keyCode != 9 && event.keyCode != 16)
      {
         form.addError(form.getFieldLabel(field.id) + " is mandatory.", field);
      }
   }
   
   return valid; 
};

/**
 * Length validation handler, tests that the given field's value has either
 * a minimum and/or maximum length.
 * 
 * @method length
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Object representing the minimum and maximum length, for example
 *        {
 *           min: 3;
 *           max: 10;
 *        }
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @static
 */
Alfresco.forms.validation.length = function length(field, args, event, form, silent)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating length of field '" + field.id +
                            "' using args: " + YAHOO.lang.dump(args));
   
   var valid = true;
   
   // TODO: Use merge to determine arg values.
   
   var min = -1;
   var max = -1;
   
   if (args.min)
   {
      min = args.min;
   }
   
   if (args.max)
   {
      max = args.max;
   }
   
   var length = field.value.length;
   
   if (min != -1 && length < min)
   {
      valid = false;
   }
   
   if (max != -1 && length > max)
   {
      valid = false;
   }
   
   if (!valid && !silent && form !== null)
   {
      form.addError(form.getFieldLabel(field.id) + " is not the correct length.", field);
   }
   
   return valid;
};

/**
 * Number validation handler, tests that the given field's value is a number.
 * 
 * @method number
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @static
 */
Alfresco.forms.validation.number = function number(field, args, event, form, silent)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating field '" + field.id + "' is a number");
   
   var valid = (isNaN(field.value) == false);
   
   if (!valid && !silent && form !== null)
   {
      form.addError(form.getFieldLabel(field.id) + " is not a number.", field);
   }
   
   return valid;
};

/**
 * Number range validation handler, tests that the given field's value has either
 * a minimum and/or maximum value.
 * 
 * @method numberRange
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Object representing the minimum and maximum value, for example
 *        {
 *           min: 18;
 *           max: 30;
 *        }
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @static
 */
Alfresco.forms.validation.numberRange = function numberRange(field, args, event, form, silent)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating number range of field '" + field.id +
                            "' using args: " + YAHOO.lang.dump(args));
   
   var valid = true;
   var value = field.value;
   
   if (value.length > 0)
   {
      if (isNaN(value))
      {
         valid = false;
         
         if (!silent && form !== null)
         {
            form.addError(form.getFieldLabel(field.id) + " is not a number.", field);
         }
      }
      else
      {
         var min = -1;
         var max = -1;
         
         if (args.min)
         {
            min = args.min;
         }
         
         if (args.max)
         {
            max = args.max;
         }
         
         if (min != -1 && value < min)
         {
            valid = false;
         }
         
         if (max != -1 && length > max)
         {
            valid = false;
         }
         
         if (!valid && !silent && form !== null)
         {
            form.addError(form.getFieldLabel(field.id) + " is not within the allowable range.", field);
         }
      }
   }
   
   return valid;
};

/**
 * Node name validation handler, tests that the given field's value is a valid
 * name for a node in the repository.
 *
 * @method nodeName
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @static
 */
Alfresco.forms.validation.nodeName = function number(field, args, event, form, silent)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating field '" + field.id + "' is a valid node name");

   if (!args)
   {
      args = {};
   }
   
   args.pattern = /([\"\*\\\>\<\?\/\:\|]+)|([ ]+$)|([\.]?[\.]+$)/;
   args.match = false;

   return Alfresco.forms.validation.regexMatch(field, args, event, form, silent);
};

/**
 * Email validation handler, tests that the given field's value is a valid
 * email address.
 *
 * @method email
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @static
 */
Alfresco.forms.validation.email = function number(field, args, event, form, silent)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating field '" + field.id + "' is a valid email address");

   if (!args)
   {
      args = {};
   }
   
   args.pattern = /(\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6})/;
   args.match = true;

   return Alfresco.forms.validation.regexMatch(field, args, event, form, silent);
};


/**
 * Regular expression validation handler, tests that the given field's value matches
 * the supplied regular expression.
 * 
 * @method regexMatch
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Object representing the expression.
 * The args object should have the form of:
 * {
 *    pattern: {regexp}, // A regular expression
 *    match: {boolean}   // set to false if the regexp should NOT match the input, default is true
 * }
 * An example to validate a field represents an email address can look like:
 * {
 *    pattern: /(\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6})/
 * }
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @static
 */
Alfresco.forms.validation.regexMatch = function regexMatch(field, args, event, form, silent)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating regular expression of field '" + field.id +
                            "' using args: " + YAHOO.lang.dump(args));
   
   var valid = true;
   
   if (field.value.length > 0)
   {
      // The pattern SHOULD match by default
      if (args.match === undefined)
      {
          args.match = true;
      }

      // Check if the patterns match
      var pattern = new RegExp(args.pattern);
      valid = pattern.test(field.value);

      // Adjust the result if the test wasn't intended to match
      if (!args.match)
      {
         valid = !valid;
      }

      // Inform the user if invalid
      if (!valid && !silent && form !== null)
      {
         form.addError(form.getFieldLabel(field.id) + " is invalid.", field);
      }
   }
   
   return valid;
};