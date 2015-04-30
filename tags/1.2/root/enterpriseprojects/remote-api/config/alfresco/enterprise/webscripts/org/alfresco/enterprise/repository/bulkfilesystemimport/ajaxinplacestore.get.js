function main()
{
   // Get the currently registered store names
   var stores = enterpriseBulkFSImport.getStoreNames();
   var query  = args.query;
   if (stores == null)
   {
     status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "enterpriseBulkFSImport was null !");
     status.redirect = true;
     return;
   }

   var filteredStores=[];
   if(query != null && query != "") // filter out the stores for which the name does not contain the query
   {
       for (var i = 0; i < stores.size(); i++)
       {
           var storeName = (stores.get(i)+"");
           if( (storeName).indexOf(query) != -1)
           {
               filteredStores.push(storeName);
           }
       }

   }

   model.stores = filteredStores;
}

main();