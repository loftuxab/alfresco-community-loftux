script:
{
   if(search.searchSubsystem == 'lucene')
   {
       if ((args.fromTime) && (args.fromTime != ""))
       {
          admIndexCheckService.reindexFromTime(utils.fromISO8601(args.fromTime));
       }
       else if ((args.fromTxnId) && (args.fromTxnId != ""))
       {
          admIndexCheckService.reindexFromTxn(args.fromTxnId);
       }
       else
       {
          status.code = 404;
          status.message = "Must specify a fromTime (in ISO 8601 format) or a fromTxnId (numeric DB id)";
          status.redirect = true;
          break script;
       }
       
       // setup model for templates
       model.ipAddress = admIndexCheckService.ipAddress;  
   }
   else
   {
       status.code = 404;
       status.message = "The index checker is only supported for Lucene.";
       status.redirect = true;
   }
}