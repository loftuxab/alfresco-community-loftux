function main()
{
   // Check to see if the web site data has already been loaded or not
   var conn = remote.connect("alfresco");
   var res = conn.get("/api/loadwebsitedata?site=" + page.url.templateArgs.site + "&preview=true");
   var jsonData = eval("(" + res + ")");   
   model.dataloaded = !jsonData.success;
}

main();