/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @module myalfresco/analytics/GetSatisfaction
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Common.html",
        "alfresco/core/Core"], 
        function(declare, _WidgetBase, _TemplatedMixin, template, AlfCore) {
   
   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {object[]}
       * @default [{cssFile:"./css/GetSatisfaction.css"}]
       */
      cssRequirements: [{cssFile:"./css/GetSatisfaction.css"}],

      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,

      /**
       * 
       * @instance
       */
      postCreate: function myalfresco_analytics_GetSatisfaction__postCreate() {

         // Get Satisfaction...
         var is_ssl = ("https:" == document.location.protocol);
         var asset_host = is_ssl ? "https://s3.amazonaws.com/getsatisfaction.com/" : "http://s3.amazonaws.com/getsatisfaction.com/";
         asset_host = asset_host + "javascripts/feedback-v2.js";

         var id = this.id;
         require([asset_host], function (getsatisfaction) {
            var feedback_widget_options = {};

            feedback_widget_options.display = "overlay";
            feedback_widget_options.company = "alfresco";
            feedback_widget_options.placement = "bottom";
            feedback_widget_options.color = "#222";
            feedback_widget_options.style = "idea";
            feedback_widget_options.container = id;

            if (GSFN)
            {
               var feedback_widget = new GSFN.feedback_widget(feedback_widget_options);
            }
         });
      }
   });
});