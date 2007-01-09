// Generate a PDF transform of the current object

if (document.isDocument)
{
   var runAction = args['action'];
   var result = "Action completed.";

   if (runAction == "makepdf")
   {
      var trans = document.transformDocument("application/pdf");
   }
   else if (runAction == "delete")
   {
      var rc = document.remove();
   }
   else if (runAction == "checkout")
   {
      var wc = document.checkout();
   }
   else if (runAction == "checkin")
   {
      var wc = document.checkout();
   }
   else
   {
       result = "Unknown action.";
   }
   
   result;
}