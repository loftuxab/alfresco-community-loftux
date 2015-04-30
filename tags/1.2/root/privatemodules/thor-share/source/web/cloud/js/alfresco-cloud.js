
/**
 * Alfresco top-level cloud namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud
 */
Alfresco.cloud = Alfresco.cloud || {};

/**
 * Alfresco top-level cloud component namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud.component
 */
Alfresco.cloud.component = Alfresco.cloud.component || {};

/**
 * Alfresco top-level cloud module namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud.module
 */
Alfresco.cloud.module = Alfresco.cloud.module || {};

/**
 * Alfresco top-level cloud constants namespace.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud.constants
 */
Alfresco.cloud.constants = Alfresco.cloud.constants || {};


/* ALFRESCO OVERRIDES */

/**
 * Overrides the url creation for online editing with sharepoint.
 * Uses the same parameters as the original Alfresco.util.onlineEditUrl method but will return a slightly different url
 * to match the cloud environment.
 *
 * @method Alfresco.util.onlineEditUrl
 * @return {String} The url to where the document can be edited online
 */
_Alfresco_util_onlineEditUrl = Alfresco.util.onlineEditUrl;
Alfresco.util.onlineEditUrl = function(vtiServer, location)
{
   // Reuse the original functionality but make sure to exclude the container name from the url
   if (location.container)
   {
      location.container.name = "";
   }
   location.tenant = Alfresco.cloud.constants.CURRENT_TENANT;
   return _Alfresco_util_onlineEditUrl(vtiServer, location);
};

/**
 * Overrides the url creation for online editing with the AOS sharepoint implementation.
 * Uses the same parameters as the original Alfresco.util.onlineEditUrl method but will return a slightly different url
 * to match the cloud environment.
 *
 * @method Alfresco.util.onlineEditUrlAos
 * @return {String} The url to where the document can be edited online
 */
_Alfresco_util_onlineEditUrlAos = Alfresco.util.onlineEditUrlAos;
Alfresco.util.onlineEditUrlAos = function(aos, record)
{
   // sanity checks
   if (!Alfresco.util.isValueSet(aos) || !Alfresco.util.isValueSet(aos.baseUrl) || !Alfresco.util.isValueSet(record.webdavUrl) || (record.webdavUrl.substring(0,8) != '/webdav/') )
   {
      throw new Error("Alfresco.util.onlineEditUrlAos: Sanity checks failed.");
   }
   //Build the exact same URL as with the VTI based implementation 
	var location = record.location;
   // Thor: used by overridden JS to place the tenant domain into the URL.
   var tenant = Alfresco.cloud.constants.CURRENT_TENANT ? Alfresco.cloud.constants.CURRENT_TENANT : "";
   var result = Alfresco.util.combinePaths(aos.baseUrl, tenant, location.site ? location.site.name : "", location.path.replace(/#/g,"%23"), location.file.replace(/#/g,"%23"));
   
   // Check if the length of the encoded path exceeds 256 characters. If so, use the nodeid instead of the path
   if (encodeURI(result).length > 256)
   {
      result = Alfresco.util.combinePaths(aos.baseUrl, "_aos_nodeid", tenant, record.nodeRef.split("/").pop(), location.file.replace(/#/g,"%23"));
   }
   // If URL still exceeds 256 characters, we also need to replace the filename
   if(encodeURI(result).length > 256)
   {
      var fileextSepIdx = location.file.lastIndexOf(".");
      result = Alfresco.util.combinePaths(aos.baseUrl, "_aos_nodeid", tenant, record.nodeRef.split("/").pop(), "Document",fileextSepIdx > 0 ? location.file.substring(fileextSepIdx + 1) : '');
   }
   return result;   
};