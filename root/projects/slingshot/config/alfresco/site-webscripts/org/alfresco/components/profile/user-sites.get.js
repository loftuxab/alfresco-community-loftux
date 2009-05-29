
// Call the repo for sites the user is a member of
var result = remote.call("/api/people/" + stringUtils.urlEncode(page.url.templateArgs["userid"]) + "/sites?size=100");
model.sites = [];
if (result.status == 200)
{
   // Create javascript objects from the server response
   model.sites = eval('(' + result + ')');
}
model.numSites = model.sites.length;

// get activity for user
// /api/activities/feed/user/loz?s=mobile&format=json