function main()
{
   // allow for content to be loaded from id
   if(args.nodeRef != null)
   {
      var nodeRef = args.nodeRef;

      // Call the repo for sites the user is a member of
      var result = remote.call("/api/version?nodeRef=" + nodeRef);

      // Create javascript objects from the server response
      var versions = eval('(' + result + ')');

      var foundCurrent = false;
      var versionGroup = "newerVersion";
      for(var i = 0; i < versions.length; i++)
      {
         if(versions[i].createdDate == nodeRef)
         {
            versionGroup = "currentVersion";
            foundCurrent = true;
         }
         versions[i].versionGroup = versionGroup;
         if(foundCurrent && versions[i].nodeRef == nodeRef)
         {
            versionGroup = "olderVersion";            
         }
      }

      // Prepare the model for the template
      model.versions = versions;      
   }
}

main();