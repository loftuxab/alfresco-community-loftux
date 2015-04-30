function main()
{
   var source = args.source;
   if (source != "account-settings" && source != "header-invite-button")
   {
      var message = 'Argument source must be either "account-settings" or "header-invite-button"';
      status.code = 400;
      status.message = message;
      throw new Error(message);
   }

   model.form =
   {
      hidden: { source: source },
      url: url.context + "/proxy/alfresco/internal/cloud/accounts/initiatedsignupqueue"
   };
};

main();