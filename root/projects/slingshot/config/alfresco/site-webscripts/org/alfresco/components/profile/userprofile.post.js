/**
 * User Profile Component Update method
 * 
 * @method POST
 */
 
function main()
{
   var names = json.names();
   for (var i=0; i<names.length(); i++)
   {
      var field = names.get(i);
      
      // look and set simple text input values
      var index = field.indexOf("-input-");
      if (index != -1)
      {
         user.properties[field.substring(index + 7)] = json.get(field);
      }
      // apply person description content field
      else if (field.indexOf("-text-biography") != -1)
      {
         user.properties["persondescription"] = json.get(field);
      }
      // apply avatar noderef if changed
      else if (field.indexOf("-photoref") != -1)
      {
         var ref = json.get(field);
         if (ref != null && ref.length() != 0)
         {
            user.properties["avatar"] = ref;
         }
      }
   }
   user.save();
   model.success = true;
}

main();