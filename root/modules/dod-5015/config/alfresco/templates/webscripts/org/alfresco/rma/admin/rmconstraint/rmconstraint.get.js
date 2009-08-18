/**
 * Get the detail of the rm constraint
 */ 
function main()
{
   // Get the shortname
   var shortName = url.extension;
   
   // Get the constraint
   var constraintDetails = caveatConfig.getConstraintDetails(shortName);
   
   if (constraintDetails != null)
   {
      // Pass the constraint detail to the template
      model.constraintName = shortName;
      model.constraintDetails = constraintDetails;
   }
   else
   {
      // Return 404
      status.setCode(404, "Constraint List " + shortName + " does not exist");
      return;
   }
}

main();