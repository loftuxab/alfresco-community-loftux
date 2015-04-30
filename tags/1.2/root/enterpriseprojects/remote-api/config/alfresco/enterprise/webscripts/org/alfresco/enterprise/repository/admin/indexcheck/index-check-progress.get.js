script:
{
   if(search.searchSubsystem == 'lucene')
   {
       var progress = admIndexCheckService.getIndexCheckProgress();
        
       if (progress == undefined)
       {
          status.code = 404;
          status.message = "Failed to get index check progress";
          status.redirect = true;
          break script;
       }
       
       // setup model for templates
       model.progress = progress;   
       model.ipAddress = admIndexCheckService.ipAddress;
       
       model.indexTxnInfo = admIndexCheckService.getLastReportRun();
   }
   else
   {
       status.code = 404;
       status.message = "The index checker is only supported for Lucene.";
       status.redirect = true;
   }
}