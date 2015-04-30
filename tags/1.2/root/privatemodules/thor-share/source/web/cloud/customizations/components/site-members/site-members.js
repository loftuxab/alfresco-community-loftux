// Override Alfresco.SiteMember's getRoles method and make it cloud specific
Alfresco.SiteMembers.prototype.getRoles = function(oRecordData)
{
   var roles = Alfresco.util.deepCopy(this.options.roles);
   if (oRecordData.isExternal)
   {
      // Remove "Site Manager" role
      Alfresco.util.arrayRemove(roles, "SiteManager");
   }
   return roles;
};