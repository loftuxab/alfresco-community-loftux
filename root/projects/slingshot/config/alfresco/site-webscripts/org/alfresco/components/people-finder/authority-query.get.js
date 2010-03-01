var mapUser = function(data)
{
   return (
   {
      authorityType: "USER",
      shortName: data.userName,
      fullName: data.userName,
      displayName: (data.firstName ? data.firstName + " " : "") + (data.lastName ? data.lastName : ""),
      description: data.jobtitle ? data.jobtitle : "",
      metadata:
      {
         avatar: data.avatar || null,
         jobTitle: data.jobtitle || "",
         organization: data.organization || ""
      }
   });
};

var mapGroup = function(data)
{
   return (
   {
      authorityType: "GROUP",
      shortName: data.shortName,
      fullName: data.fullName,
      displayName: data.displayName,
      description: data.fullName,
      metadata:
      {
      }
   });
};

var getApiMappings = function()
{
   var apiMappings = [],
      authorityType = args.authorityType === null ? "all" : String(args.authorityType).toLowerCase();
   
   if (authorityType === "all" || authorityType == "user")
   {
      apiMappings.push(
      {
         url: "/api/people?filter=" + encodeURIComponent(args.filter),
         rootObject: "people",
         fn: mapUser
      });
   }

   if (authorityType === "all" || authorityType == "group")
   {
      var url = "/api/groups?shortNameFilter=" + encodeURIComponent(args.filter);
      if (args.zone !== "all")
      {
         url += "zone=" + encodeURIComponent(args.zone === null ? "APP.DEFAULT" : args.zone);
      }
      
      apiMappings.push(
      {
         url: url,
         rootObject: "data",
         fn: mapGroup
      });
   }
   return apiMappings;
};

function main()
{
   var apiMappings = getApiMappings(),
      connector = remote.connect("alfresco"),
      authorities = [],
      api, result, data;
   
   for (var i = 0; i < apiMappings.length; i++)
   {
      api = apiMappings[i];
      result = connector.get(api.url);
      if (result.status == 200)
      {
         data = eval('(' + result + ')');
         for (var j = 0; j < data[api.rootObject].length; j++)
         {
            authorities.push(api.fn.call(this, data[api.rootObject][j]));
         }
      }
   }
   
   return authorities;
}

model.authorities = main();