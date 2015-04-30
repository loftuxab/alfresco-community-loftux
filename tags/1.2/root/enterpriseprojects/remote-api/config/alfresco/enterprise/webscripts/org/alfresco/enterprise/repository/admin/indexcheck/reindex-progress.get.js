script:
{
   if(search.searchSubsystem == 'lucene')
   {
        var progress = admIndexCheckService.getReindexProgress();
        
        if (progress == undefined)
        {
          status.code = 404;
          status.message = "Failed to get reindex progress";
          status.redirect = true;
          break script;
        }
        
        // setup model for templates
        model.progress = progress;
        model.ipAddress = admIndexCheckService.ipAddress;
   }
   else
   {
       status.code = 404;
       status.message = "The index checker is only supported for Lucene.";
       status.redirect = true;
   }
}