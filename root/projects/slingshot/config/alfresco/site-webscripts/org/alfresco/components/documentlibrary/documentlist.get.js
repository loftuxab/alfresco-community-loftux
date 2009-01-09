const PREFERENCES_ROOT = "org.alfresco.share.documentList";

/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   var result, preferences = {};
   
   // Request the current user's preferences
   var result = remote.call("/api/people/" + stringUtils.urlEncode(user.name) + "/preferences?pf=" + PREFERENCES_ROOT);
   if (result.status == 200)
   {
      var prefs = eval('(' + result + ')');
      try
      {
         // Populate the preferences object literal for easy look-up later
         preferences = eval('(prefs.' + PREFERENCES_ROOT + ')');
         if (typeof preferences != "object")
         {
            preferences = {};
         }
      }
      catch (e)
      {
      }
   }

   model.preferences = preferences;
}

main();