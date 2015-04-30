/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * @module myalfresco/analytics/FacetedSearchAnalyticsService
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "dojo/_base/lang"], 
        function(declare, AlfCore, lang) {
   
   return declare([AlfCore], {

      /**
       * Set up the subscriptions to facet application, sort selection and scope selection events.
       *
       * @instance
       */
      constructor: function myalfresco_analytics_FacetedSearchAnalyticsService__constructor(args) {
         lang.mixin(this, args);
         this.alfSubscribe("ALF_APPLY_FACET_FILTER", lang.hitch(this, this.trackFacetApplication));
         this.alfSubscribe("ALF_DOCLIST_SORT_FIELD_SELECTION", lang.hitch(this, this.trackSortField));
         this.alfSubscribe("ALF_SEARCHLIST_SCOPE_SELECTION", lang.hitch(this, this.trackScopeSelection));
      },

      /**
       * Checks for the existence of the GoogleAnalytics "window._gaq" global variable and if it exists
       * it will push an event to capture the application of the facet that has been applied.
       *
       * @instance
       * @param {object} payload The details of the facet filter that has been applied
       */
      trackFacetApplication: function myalfresco_analytics_FacetedSearchAnalyticsService__trackFacetApplication(payload) {
         this.alfLog("info", "Facet filter applied", payload);
         if (window._gaq != null && typeof window._gaq.push === "function")
         {
            if (payload.filter)
            {
               var filterData = payload.filter.split("|");
               if (filterData.length > 1)
               {
                  window._gaq.push(['_trackEvent', 'Facets Applied', filterData[0], filterData[1]]);
               }
            }
         }
      },

      /**
       * Checks for the existence of the GoogleAnalytics "window._gaq" global variable and if it exists
       * it will push an event to capture the selection of a sort field.
       *
       * @instance
       * @param {object} payload The details of the facet filter that has been applied
       */
      trackSortField: function myalfresco_analytics_FacetedSearchAnalyticsService__trackSortField(payload) {
         this.alfLog("info", "Sort field selected", payload);
         if (window._gaq != null && typeof window._gaq.push === "function")
         {
            if (payload.value)
            {
               window._gaq.push(['_trackEvent', 'Sort Field Selected', payload.value]);
            }
         }
      },

      /**
       * Checks for the existence of the GoogleAnalytics "window._gaq" global variable and if it exists
       * it will push an event to capture the selection of a sort field.
       *
       * @instance
       * @param {object} payload The details of the facet filter that has been applied
       */
      trackScopeSelection: function myalfresco_analytics_FacetedSearchAnalyticsService__trackScopeSelection(payload) {
         this.alfLog("info", "Scope selected", payload);
         if (window._gaq != null && typeof window._gaq.push === "function")
         {
            if (payload.value)
            {
               var scope = payload.value;
               if (scope !== "REPO")
               {
                  scope = "SITE";
               }
               window._gaq.push(['_trackEvent', 'Scope Selected', scope]);
            }
         }
      }
   });
});