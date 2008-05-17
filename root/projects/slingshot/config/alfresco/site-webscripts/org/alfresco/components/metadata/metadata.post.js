try
{
   model.formdata = formdata;
}
catch (err)
{
   // do nothing, it means the form was posted in
   // x-www-form-urlencoded format or has a content
   // type of json
   
   try
   {
      model.json = json;
   }
   catch (err2)
   {
   }
}