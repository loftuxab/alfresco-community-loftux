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
 * Search component.
 * 
 * @namespace Alfresco
 * @class Alfresco.CloudSearch
 */
(function()
{
   /**
    * Override component implementation
    */
   Alfresco.CloudSearch = function(htmlId)
   {
      Alfresco.CloudSearch.superclass.constructor.call(this, htmlId);
      return this;
   };
   
   YAHOO.lang.extend(Alfresco.CloudSearch, Alfresco.Search, 
   {
      /**
       * Build URI parameter string for search JSON data webscript
       *
       * @method _buildSearchParams
       */
      _buildSearchParams: function CloudSearch__buildSearchParams(searchRepository, searchAllSites, searchTerm, searchTag, searchSort, page)
      {
         // Cloud specific override - where searchAllSites = force repo wide search using a query on the backend that specifically
         // ignores everything not related to Cloud at the repo level - to avoid the v.slow All Sites PATH query step.
         var site = searchAllSites ? "" : this.options.siteId;
         var searchRepository = searchAllSites ? "true" : "false";
         var scope = searchRepository ? "repo" : (searchAllSites ? "all_sites" : this.options.siteId);
         var params = YAHOO.lang.substitute("site={site}&term={term}&tag={tag}&maxResults={maxResults}&sort={sort}&query={query}&repo={repo}&scope={scope}&pageSize={pageSize}&startIndex={startIndex}",
         {
            site: encodeURIComponent(site),
            repo: searchRepository,
            scope: encodeURIComponent(scope),
            term: encodeURIComponent(searchTerm),
            tag: encodeURIComponent(searchTag),
            sort: encodeURIComponent(searchSort),
            query: encodeURIComponent(this.options.searchQuery),
            maxResults: this.options.maxSearchResults + 1, // to calculate whether more results were available
            pageSize: this.options.pageSize,
            startIndex: (page - 1) * this.options.pageSize
         });
         
         return params;
      }
   });
   
})();