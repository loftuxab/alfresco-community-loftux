script:
{
   if(search.searchSubsystem == 'lucene')
   {
       var indexTxnInfo;
       
       if ((args.txnId) && (args.txnId != ""))
       {
          indexTxnInfo = admIndexCheckService.checkTxn(args.txnId);
       }
       else if (args.txnId  == undefined)
       {
          indexTxnInfo = admIndexCheckService.checkLastTxn();
       }
       else
       {
          status.code = 404;
          status.message = "Must specify txnId (numeric DB id)";
          status.redirect = true;
          break script;
       }
       
       if (indexTxnInfo == undefined)
       {
          status.code = 404;
          status.message = "Failed to get index txn: "+args.txnId;
          status.redirect = true;
          break script;
       }
        
       // setup model for templates
       model.indexTxnInfo = indexTxnInfo;
       model.ipAddress = indexTxnInfo.ipAddress;
   }
   else
   {
       status.code = 404;
       status.message = "The index checker is only supported for Lucene.";
       status.redirect = true;
   }
}