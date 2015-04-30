<import resource="classpath:alfresco/site-webscripts/org/alfresco/cloud/core/customizations/locales.lib.js">
function main()
{
   // Ensure that the edition is marked as "CLOUD"...
   // This will add a CSS class that can be used to control the colour of the login button
   model.edition = "CLOUD";
   
   var applicationContext = url.context.match("/[^/]+")[0];
   model.loginUrl = applicationContext + "/-default-/page/dologin";
   var successUrl = context.properties["alfRedirectUrl"];
   if (successUrl == null)
   {
      successUrl = applicationContext;
   }
   model.successUrl = successUrl;
   
   // Add the available languages to an array
   model.languages = getLocales();
   
   /* The 3 for loops broaden the search to match locale with an available language.
    * The first loop matches the locale exactly e.g. en_GB == en_GB
    * The second matches the language only if there is no country code in the available languages e.g. en_GB == en
    * The third matches the language only and ignores the country e.g. en_GB == en_US
    */
   var currLang = locale;
   var langFound = false;
   for(var x = 0; x < model.languages.length && !langFound; x++)
   {
      if(model.languages[x].locale === currLang)
      {
         model.languages[x].selected = true;
         langFound = true;
         break;
      }
   }
   currLang = locale.substr(0,2);
   for(var x = 0; x < model.languages.length && !langFound; x++)
   {
      if(model.languages[x].locale === currLang)
      {
         model.languages[x].selected = true;
         langFound = true;
         break;
      }
   }
   for(var x = 0; x < model.languages.length && !langFound; x++)
   {
      if(model.languages[x].locale.substr(0,2) === currLang)
      {
         model.languages[x].selected = true;
         langFound = true;
         break;
      }
   }

   // Preload images
   model.images =
   [
      "components/images/welcome-background.png",
      "components/images/user-16.png",
      "components/images/alfresco-share-logo-enterprise.png",
      "components/images/header/my-dashboard.png",
      "components/images/header/sites.png",
      "components/images/header/help.png",
      "components/images/feed-icon-16.png",
      "components/documentlibrary/images/simple-view-on-16.png",
      "components/documentlibrary/images/detailed-view-on-16.png",
      "components/documentlibrary/images/detailed-view-off-16.png",
      "components/images/search-16.png",
      "components/images/star-selected_16x16.png",
      "components/images/star-deselected_16x16.png",
      "components/images/lightbox/overlay.png",
      "components/images/filetypes/generic-file-16.png",
      "components/images/comment-16.png",
      "components/images/filetypes/generic-site-32.png",
      "components/documentlibrary/images/navbar-show-16.png",
      "components/documentlibrary/images/select-all-16.png",
      "components/documentlibrary/images/feed-icon-16.png",
      "components/documentlibrary/images/select-documents-16.png",
      "components/documentlibrary/images/select-folders-16.png",
      "components/documentlibrary/images/select-invert-16.png",
      "components/documentlibrary/images/select-none-16.png",
      "components/documentlibrary/images/folders-hide-16.png",
      "components/documentlibrary/images/sort-ascending-16.png",
      "components/documentlibrary/images/sort-descending-16.png",
      "components/documentlibrary/images/simple-view-off-16.png",
      "components/documentlibrary/images/folders-show-16.png",
      "components/documentlibrary/images/folder-new-16.png",
      "components/documentlibrary/images/upload-16.png",
      "components/documentlibrary/actions/document-move-to-16.png",
      "components/documentlibrary/actions/document-copy-to-16.png",
      "components/documentlibrary/actions/document-delete-16.png",
      "components/documentlibrary/actions/document-manage-permissions-16.png",
      "components/documentlibrary/images/folder-up-disabled-16.png",
      "components/documentlibrary/indicators/exif-16.png",
      "components/documentlibrary/images/folder-64.png",
      "components/images/drop-arrow-left-large.png",
      "components/images/drop-arrow-left-small.png",
      "components/images/like-16.png",
      "components/images/liked-16.png",
      "components/images/edit-16.png",
      "components/documentlibrary/actions/folder-view-details-16.png",
      "components/documentlibrary/actions/folder-edit-properties-16.png",
      "components/documentlibrary/images/plus-sign-16.png",
      "components/document-details/images/document-download-16.png",
      "components/documentlibrary/actions/document-view-content-16.png",
      "components/documentlibrary/actions/document-edit-properties-16.png",
      "components/documentlibrary/actions/document-assign-workflow-16.png",
      "components/documentlibrary/actions/document-upload-new-version-16.png",
      "components/documentlibrary/actions/document-edit-metadata-16.png",
      "components/documentlibrary/actions/document-edit-offline-16.png",
      "components/document-details/images/document-view-metadata-16.png",
      "components/document-details/images/revert-16.png"
   ];
}

main();