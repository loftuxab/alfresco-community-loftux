/**
 * User Status Update method
 * 
 * @method POST
 */
 
function main()
{
   // make remote call to update user status
   var conn = remote.connect("alfresco");
   var result = conn.post(
      "/slingshot/profile/userstatus",
      jsonUtils.toJSONString({status: json.get("status")}),
      "application/json");
   if (result.status == 200 && result.response != "{}")
   {
      // update local cached user with status and date updated 
      user.properties["userStatus"] = json.get("status");
      var now = new Date();
      var userStatusTime = eval('(' + result.response + ')').userStatusTime.iso8601;
      user.properties["userStatusTime"] = userStatusTime;
      model.userStatusTime = userStatusTime;
   }
   else
   {
      status.code = result.status;
   }
}

function pad(s, len)
{
    var result = s;
    for (var i=0; i<(len - s.length); i++)
    {
        result = "0" + result;
    }
    return result;
}

main();