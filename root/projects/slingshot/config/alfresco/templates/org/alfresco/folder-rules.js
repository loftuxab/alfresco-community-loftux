<import resource="classpath:/alfresco/templates/org/alfresco/documentlibrary.js">

function main()
{
   var nodeRef = page.url.args.nodeRef;
   var connector = remote.connect("alfresco");
   // todo use this when webscript exist: var result = connector.get("/api/node/" + page.url.args.nodeRef.replace (":/", "") + "/ruleset/rules");
   var result = connector.get("/api/sites");
   if (result.status == 200)
   {

      //var data = eval('(' + result + ')'); 

      // Load folder info
      model.folder = {
         name: "The Folder",
         path: "/path/to/folder"
      };

      // Load rules and see if there are any or if they are linked
      if (false)
      {
         // Folder has linked rules
         model.linkedFolder = {
            nodeRef: "linkedFolderNodeRef",
            name: "Requirements",
            path: "/Sailfish/Product management"
         };
      }
      else if (true)
      {
         // Folder has rules
         var rules = [];
         rules[rules.length] = {
            nodeRef: "irnr1",
            title: "First inherited rule",
            description: "This is quite a rule one must say",
            inheritedFolder:  {
               nodeRef: "ifnr1",
               name: "ifnr1"
            },
            active:  true
         };
         rules[rules.length] = {
            nodeRef: "irnr2",
            title: "Second inherited rule",
            description: "This is quite a rule one must say",
            inheritedFolder:  {
               nodeRef: "ifnr1",
               name: "ifnr1"
            },
            active:  true
         };
         rules[rules.length] = {
            nodeRef: "ornr1",
            title: "First own rule",
            description: "This is quite a rule one must say",
            inheritedFolder:  null,
            active:  false
         };
         rules[rules.length] = {
            nodeRef: "ornr2",
            title: "Second own rule",
            description: "This is quite a rule one must say",
            inheritedFolder:  null,
            active:  true
         };
         model.rules = rules;
      }
      else
      {
         // No (linked) rules at all
      }
   }
}

main();
