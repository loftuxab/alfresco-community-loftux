script:
{
   if(search.searchSubsystem == 'lucene')
   {
       if ((args.nodeRef) && (args.nodeRef != ""))
       {
          var indexTxnInfo = admIndexCheckService.getStatusForNode(utils.getNodeFromString(args.nodeRef).nodeRef);
        
          if (indexTxnInfo == undefined)
          {
             status.code = 404;
             status.message = "Failed to check node: "+args.nodeRef;
             status.redirect = true;
             break script;
          }
       }
       else if ((args.txnId) && (args.txnId != ""))
       {
          var indexTxnInfo = admIndexCheckService.getStatusForTxnNodes(args.txnId);
        
          if (indexTxnInfo == undefined)
          {
             status.code = 404;
             status.message = "Failed to check nodes for txnId: "+args.txnId;
             status.redirect = true;
             break script;
          }
       }
       else
       {
          status.code = 404;
          status.message = "Must specify a nodeRef or a txnId (numeric DB id)";
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