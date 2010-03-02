/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * RuleConfigAction.
 *
 * @namespace Alfresco
 * @class Alfresco.RuleConfigAction
 */
(function()
{

   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Selector = YAHOO.util.Selector,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML,
       $hasEventInterest = Alfresco.util.hasEventInterest;
   
   Alfresco.RuleConfigAction = function(htmlId)
   {
      Alfresco.RuleConfigAction.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.RuleConfigAction";
      Alfresco.util.ComponentManager.reregister(this);

      // Instance variables
      this.customisations = YAHOO.lang.merge(this.customisations, Alfresco.RuleConfigAction.superclass.customisations);
      this.renderers = YAHOO.lang.merge(this.renderers, Alfresco.RuleConfigAction.superclass.renderers);

      return this;
   };

   YAHOO.extend(Alfresco.RuleConfigAction, Alfresco.RuleConfig,
   {

      /**
       * CUSTOMISATIONS
       */

      customisations:
      {

         Select:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               configDef.parameterDefinitions = [
                  {
                     name: "mandatory",
                     type: "d:text",
                     isMandatory: true,
                     isMultiValued: false,
                     displayLabel: this.msg("label.selectAnAction"),
                     _type: "hidden"
                  }
               ];
               return configDef;
            }
         },

         SendAnEmail:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters but subject
               this._hideParameters(configDef.parameterDefinitions);
               this._getParamDef(configDef, "subject")._type = null;
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push({
                  type: "arca:email-dialog-button",
                  _buttonLabel: this.msg("button.message")
               });
               return configDef;
            }
         },

         CheckIn:
         {
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push({
                  type: "arca:checkin-dialog-button",
                  _buttonLabel: this.msg("button.options")
               });
               return configDef;
            }
         },

         Checkout:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters except destination-folder that shall be resolved to a path
               this._hideParameters(configDef.parameterDefinitions);
               this._getParamDef(configDef, "destination-folder")._type = "path";
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push({
                  type: "arca:destination-dialog-button",
                  displayLabel: this.msg("label.workingCopyLocation"),
                  _buttonLabel: this.msg("button.select"),
                  _destinationParam: "destination-folder"
               });
               return configDef;
            }
         },


         FileDestination:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters since we are using a cusotm ui but set default values
               this._hideParameters(configDef.parameterDefinitions);
               this._getParamDef(configDef, "destination-folder")._type = "path";
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters since we are using a cusotm ui but set default values
               this._hideParameters(configDef.parameterDefinitions);
               this._setParameter(ruleConfig, "assoc-type", "cm:contains");

               // Make parameter renderer create a "Destination" button that displays an destination folder browser
               configDef.parameterDefinitions.push({
                  type: "arca:destination-dialog-button",
                  displayLabel: this.msg("label.to"),
                  _buttonLabel: this.msg("button.select"),
                  _destinationParam: "destination-folder"
               });
               return configDef;
            }
         },

         SimpleWorkflow:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters but the labels
               this._hideParameters(configDef.parameterDefinitions);
               this._getParamDef(configDef, "approve-step")._type = null;
               if (ruleConfig.parameterValues && ruleConfig.parameterValues["reject-step"])
               {
                  this._getParamDef(configDef, "reject-step")._type = null;
               }
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters since we are using a cusotm ui
               this._hideParameters(configDef.parameterDefinitions);

               // Make approve parameters mandatory (not changed in repo for legacy reasons)
               this._getParamDef(configDef, "approve-step").isMandatory = true;
               this._getParamDef(configDef, "approve-folder").isMandatory = true;
               this._getParamDef(configDef, "approve-move").isMandatory = true;

               // Make reject parameters mandatory (will only be used when reject is enabled and inputs are enabled)
               this._getParamDef(configDef, "reject-step").isMandatory = true;
               this._getParamDef(configDef, "reject-folder").isMandatory = true;
               this._getParamDef(configDef, "reject-move").isMandatory = true;

               // Make parameter renderer create an "Approve" button that displays an approve simple workflow dialog
               configDef.parameterDefinitions.push({
                  type: "arca:simple-workflow-dialog-button",
                  _buttonLabel: this.msg("button.approve"),
                  _mode: Alfresco.module.RulesWorkflowAction.VIEW_MODE_APPROVAL_STEP
               });

               // Make parameter renderer create a "Reject" button that displays a reject simple workflow dialog
               configDef.parameterDefinitions.push({
                  type: "arca:simple-workflow-dialog-button",
                  _buttonLabel: this.msg("button.reject"),
                  _mode: Alfresco.module.RulesWorkflowAction.VIEW_MODE_REJECTION_STEP
               });
               return configDef;
            }
         },

         LinkCategory:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               this._getParamDef(configDef, "category-aspect")._type = "hidden";
               this._getParamDef(configDef, "category-value")._type = "category";
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               this._hideParameters(configDef.parameterDefinitions);
               configDef.parameterDefinitions.push(
               {
                  type: "arca:category-picker"
               });
               return configDef;
            }
         },

         Transform:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters since we are using a cusotm ui but set default values
               this._hideParameters(configDef.parameterDefinitions);

               // But make mime type and destination folder visible and destination folder resolve the nodeRef to a path
               this._getParamDef(configDef, "mime-type")._type = null;
               this._getParamDef(configDef, "destination-folder")._type = "path";
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters but mime-type
               this._hideParameters(configDef.parameterDefinitions);
               this._getParamDef(configDef, "mime-type")._type = null;

               // todo set appropriate values when known OR hide if they aren't mandatory
               //this._setParameter(ruleConfig, "assoc-type", "");
               //this._setParameter(ruleConfig, "assoc-name", "");

               // Make parameter renderer create a "Destination" button that displays an destination folder browser
               configDef.parameterDefinitions.push({
                  type: "arca:destination-dialog-button",
                  displayLabel: this.msg("label.to"),
                  _buttonLabel: this.msg("button.select"),
                  _destinationParam: "destination-folder"
               });
               return configDef;
            }
         },

         Import:
         {
            text: function(configDef, ruleConfig, configEl)
            {
               // Hide encoding and make destination folder resolve the nodeRef to a path
               this._getParamDef(configDef, "encoding")._type = "hidden";
               this._getParamDef(configDef, "destination")._type = "path";
               return configDef;
            },
            edit: function(configDef, ruleConfig, configEl)
            {
               // Hide all parameters
               this._hideParameters(configDef.parameterDefinitions);

               // Make parameter renderer create a "Destination" button that displays an destination folder browser
               configDef.parameterDefinitions.push({
                  type: "arca:destination-dialog-button",
                  displayLabel: this.msg("label.to"),
                  _buttonLabel: this.msg("button.select"),
                  _destinationParam: "destination"
               });
               return configDef;
            }
         }


      },

      /**
       * RENDERERS
       */

      renderers:
      {
         "arca:email-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_emailFormButton_onClick(type, obj)
               {
                  this.renderers["arca:email-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig
                  };
                  if (!this.widgets.emailForm)
                  {
                     this.widgets.emailForm = new Alfresco.module.EmailForm(this.id + "-emailForm");
                     YAHOO.Bubbling.on("emailFormCompleted", function (layer, args)
                     {
                        if ($hasEventInterest(this.widgets.emailForm, args))
                        {
                           var values = args[1].options;
                           if (values !== null)
                           {
                              var ctx = this.renderers["arca:email-dialog-button"].currentCtx;
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "to_many", values.recipients);
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "subject", values.subject);
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "text", values.message ? values.message : "");
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "template", values.template ? values.template : "");
                              this._updateSubmitElements(ctx.configDef);
                           }
                        }
                     }, this);
                  }
                  var params = this._getParameters(obj.configDef);
                  this.widgets.emailForm.showDialog(
                  {
                     recipients: params.to_many,
                     subject: params.subject,
                     message: params.text,
                     template: params.template
                  });
               });
            }
         },

         "arca:checkin-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_checkinFormButton_onClick(type, obj)
               {
                  this.renderers["arca:checkin-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig
                  };
                  if (!this.widgets.checkInForm)
                  {
                     this.widgets.checkInForm = new Alfresco.module.RulesCheckinAction(this.id + "-checkinForm");
                     YAHOO.Bubbling.on("checkinConfigCompleted", function (layer, args)
                     {
                        if ($hasEventInterest(this.widgets.checkInForm, args))
                        {
                           var values = args[1].options;
                           if (values !== null)
                           {
                              var ctx = this.renderers["arca:checkin-dialog-button"].currentCtx;
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "minorChange", values.version == "minor" ? "true" : "false");
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "description", values.comments);
                              this._updateSubmitElements(ctx.configDef);
                           }
                        }
                     }, this);
                  }
                  var params = this._getParameters(obj.configDef);
                  this.widgets.checkInForm.showDialog(
                  {
                     version: params.minorChange == "true" ? "minor" : "major",
                     comments: params.description
                  });
               });
            }
         },

         "arca:destination-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this._createLabel(paramDef.displayLabel, containerEl); 
               var nodeRef = ruleConfig.parameterValues ? ruleConfig.parameterValues[paramDef._destinationParam] : null;
               this._createPathSpan(containerEl, this.id + "-" + configDef._id + "-destinationLabel", nodeRef);
               this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_destinationDialogButton_onClick(type, obj)
               {
                  this.renderers["arca:destination-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig,
                     paramDef: obj.paramDef
                  };
                  if (!this.widgets.destinationDialog)
                  {
                     this.widgets.destinationDialog = new Alfresco.module.DoclibGlobalFolder(this.id + "-destinationDialog");
                     this.widgets.destinationDialog.setOptions(
                     {
                        title: this.msg("dialog.destination.title")
                     });

                     YAHOO.Bubbling.on("folderSelected", function (layer, args)
                     {
                        if ($hasEventInterest(this.widgets.destinationDialog, args))
                        {
                           var selectedFolder = args[1].selectedFolder;
                           if (selectedFolder !== null)
                           {
                              var ctx = this.renderers["arca:destination-dialog-button"].currentCtx,
                                 path = selectedFolder.path;
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, ctx.paramDef._destinationParam, selectedFolder.nodeRef);
                              if (selectedFolder.siteId !== undefined)
                              {
                                 path = this.msg("label.site-path", selectedFolder.siteId, selectedFolder.path);
                              }
                              Dom.get(this.id + "-" + ctx.configDef._id + "-destinationLabel").appendChild(document.createTextNode($html(path)));
                              this._updateSubmitElements(ctx.configDef);
                           }
                        }
                     }, this);
                  }
                  this.widgets.destinationDialog.setOptions(
                  {
                     path: Dom.get(this.id + "-" + obj.configDef._id + "-destinationLabel").innerHTML
                  });
                  this.widgets.destinationDialog.showDialog();
               });
            }
         },

         "arca:simple-workflow-dialog-button":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               var enableCheckboxEl,
                  RWA = Alfresco.module.RulesWorkflowAction,
                  prefix = paramDef._mode == RWA.VIEW_MODE_APPROVAL_STEP ? "approve" : "reject";;
               if (paramDef._mode == RWA.VIEW_MODE_REJECTION_STEP)
               {
                  /**
                   * Add a checkbox that enables/disables the reject button and hidden input parameters
                   * listener will be attached later so the button object can be passed to callback
                   */
                  enableCheckboxEl = document.createElement("input");
                  enableCheckboxEl.setAttribute("type", "checkbox");
                  containerEl.appendChild(enableCheckboxEl);
               }

               // Create button that displays the simple workflow dialog
               var button = this._createButton(containerEl, configDef, paramDef, ruleConfig, function RCA_destinationDialogButton_onClick(type, obj)
               {
                  this.renderers["arca:simple-workflow-dialog-button"].currentCtx =
                  {
                     configDef: obj.configDef,
                     ruleConfig: obj.ruleConfig,
                     paramDef: obj.paramDef
                  };
                  if (!this.widgets.simpleWorkflowDialog)
                  {
                     this.widgets.simpleWorkflowDialog = new Alfresco.module.RulesWorkflowAction(this.id + "-simpleWorkflowDialog");
                     YAHOO.Bubbling.on("workflowOptionsSelected", function (layer, args)
                     {
                        if ($hasEventInterest(this.widgets.simpleWorkflowDialog, args))
                        {
                           var values = args[1].options;
                           if (values !== null)
                           {
                              var ctx = this.renderers["arca:simple-workflow-dialog-button"].currentCtx,
                                 RWA = Alfresco.module.RulesWorkflowAction,
                                 prefix = values.viewMode == RWA.VIEW_MODE_APPROVAL_STEP ? "approve" : "reject";
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, prefix + "-step", values.label);
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, prefix + "-move", values.action == "move");
                              this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, prefix + "-folder", values.nodeRef);
                              this._updateSubmitElements(ctx.configDef);
                           }
                        }
                     }, this);
                  }
                  var viewMode = obj.paramDef._mode;
                  this.widgets.simpleWorkflowDialog.setOptions({
                     viewMode: viewMode
                  });
                  var params = this._getParameters(obj.configDef),
                     RWA = Alfresco.module.RulesWorkflowAction,
                     prefix = viewMode == RWA.VIEW_MODE_APPROVAL_STEP ? "approve" : "reject";
                  this.widgets.simpleWorkflowDialog.showDialog(
                  {
                     label: params[prefix + "-step"],
                     action: (params[prefix + "-move"] == "true" ? "move" : "copy"),
                     nodeRef: params[prefix + "-folder"]
                  });
               });

               if (enableCheckboxEl)
               {
                  // Toggle enable/disable on the button and the belonging hidden fields
                  Event.addListener(enableCheckboxEl, "click", function(p_oEvent, p_oObj)
                  {
                     var disabled = !p_oObj.enableCheckboxEl.checked;
                     var hiddenFields = [];
                     hiddenFields.push(Selector.query("input[param=" + p_oObj.prefix + "-step]", p_oObj.configDef._id)[0]);
                     hiddenFields.push(Selector.query("input[param=" + p_oObj.prefix + "-folder]", p_oObj.configDef._id)[0]);
                     hiddenFields.push(Selector.query("input[param=" + p_oObj.prefix + "-move]", p_oObj.configDef._id)[0]);
                     this._toggleDisableOnElements(hiddenFields, disabled);
                     p_oObj.button.set("disabled", disabled);
                     this._updateSubmitElements(p_oObj.configDef);
                  },
                  {
                     enableCheckboxEl: enableCheckboxEl,
                     button: button,
                     configDef: configDef,                     
                     prefix: prefix
                  }, this);

                  // Enable
                  enableCheckboxEl.click();
                  if (!ruleConfig.parameterValues || !ruleConfig.parameterValues[prefix + "-folder"])
                  {
                     // Disable
                     enableCheckboxEl.click();
                  }
               }

            }
         },
         
         "arca:category-picker":
         {
            manual: { edit: true },
            currentCtx: {},
            edit: function (containerEl, configDef, paramDef, ruleConfig, value)
            {
               this.renderers["arca:category-picker"].currentCtx =
               {
                  configDef: configDef,
                  ruleConfig: ruleConfig,
                  paramDef: paramDef
               };
               var picker = new Alfresco.module.ControlWrapper(Alfresco.util.generateDomId());
               picker.setOptions(
               {
                  type: "category",
                  container: containerEl,
                  controlParams:
                  {
                     multipleSelectMode: false
                  },
                  fnValueChanged:
                  {
                     fn: function(obj)
                     {
                        var ctx = this.renderers["arca:category-picker"].currentCtx;
                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "category-aspect", "cm:classifiable");
                        this._setHiddenParameter(ctx.configDef, ctx.ruleConfig, "category-value", obj.selectedItems[0]);
                        this._updateSubmitElements(ctx.configDef);
                     },
                     scope: this
                  }
               });
               picker.show();
            }
         }
      }
   });

})();
