var length = json.names().length();
for (var i=0; i<length; i++)
{
   var field = json.names().get(i);
   
   // look and set simple text input values
   var index = field.indexOf("-input-");
   if (index != -1)
   {
      user.properties[field.substring(index + 7)] = json.get(field);
   }
   
   // TODO: apply avatar
   
   // TODO: apply person description
   else if (field.indexOf("-text-biography") != -1)
   {
      user.properties["persondescription"] = json.get(field);
   }
}
user.save();
model.success = true;