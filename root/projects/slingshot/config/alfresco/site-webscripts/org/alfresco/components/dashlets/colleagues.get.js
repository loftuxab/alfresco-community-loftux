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
   }
}

// Prepare the model
model.memberships = memberships;