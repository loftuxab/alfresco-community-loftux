function sortByName(membership1, membership2)
{
   var name1 = membership1.person ? membership1.person.firstName + membership1.person.lastName : "";
   var name2 = membership2.person ? membership2.person.firstName + membership2.person.lastName : "";
   return (name1 > name2) ? 1 : (name1 < name2) ? -1 : 0;
}

// Call the repo for the site memberships
var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships");

var memberships = [];

if (json.status == 200)
{
   // Create javascript objects from the repo response
   var obj = eval('(' + json + ')');
   if (obj)
   {
      memberships = obj;
      var userObj, member;
      for (var i = 0, j = memberships.length; i < j; i++)
      {
         member = memberships[i];
         userObj = user.getUser(member.person.userName);
         if (userObj != null)
         {
            member.avatar = userObj.properties.avatar;
         }
      }
      memberships.sort(sortByName);

   }
}

// Prepare the model
model.memberships = memberships;