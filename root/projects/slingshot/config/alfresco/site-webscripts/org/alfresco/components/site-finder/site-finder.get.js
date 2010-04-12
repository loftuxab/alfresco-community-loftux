var connector, result, theUrl;

theUrl = "/api/invitations?inviteeUserName=" + stringUtils.urlEncode(user.name);
connector = remote.connect("alfresco");
result = connector.get(theUrl);
if (result.status !== status.STATUS_OK)
{
   model.inviteData = [];
}
var inviteData = eval('(' + result.response + ')');
model.inviteData = inviteData.data;