script:
{
   if(search.searchSubsystem == 'lucene')
   {
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