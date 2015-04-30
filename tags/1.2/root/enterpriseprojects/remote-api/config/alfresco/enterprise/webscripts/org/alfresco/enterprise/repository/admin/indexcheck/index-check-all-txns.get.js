script:
{
   if(search.searchSubsystem == 'lucene')
   {
       var indexTxnInfo = admIndexCheckService.checkAllTxns();
        
       if (indexTxnInfo == undefined)
       {
          status.code = 404;
          status.message = "Failed to get all index txns";
          status.redirect = true;
          break script;
       }
       else if (indexTxnInfo.alreadyRunning == true)
       {
          status.code = 404;
          status.message = "Index check is already running!";
          status.redirect = true;
          break script;
       }
       
       // setup model for templates
       model.indexTxnInfo = indexTxnInfo;
       model.ipAddress = indexTxnInfo.ipAddress;
       model.enabled = true;
   }
   else
   {
       status.code = 404;
       status.message = "The index checker is only supported for Lucene.";
       status.redirect = true;
   }
}