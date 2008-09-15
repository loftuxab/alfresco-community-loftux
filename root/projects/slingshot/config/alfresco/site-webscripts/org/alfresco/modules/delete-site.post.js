function main()
{
   var req = json.toString();
   var reqJSON = eval('(' + req + ')');
   
   // Call the repo to delete the site
   var conn = remote.connect("alfresco");
   var res = conn.del("/api/sites/" + reqJSON.shortName);
   var resJSON = eval('(' + res + ')');
   
   // Check if we got a positive result
   if (resJSON.success)
   {
      // Yes we did - the client will refresh
      model.success = true;
   }
   else
   {
      // Error occured - report back to client with the status and message
      status.setCode(resJSON.status.code, resJSON.message);
      model.success = false;
   }
}

main();