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
   
   // TODO: apply avatar
}
user.save();
model.success = true;