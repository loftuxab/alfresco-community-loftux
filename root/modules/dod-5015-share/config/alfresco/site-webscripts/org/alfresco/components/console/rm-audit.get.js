function getAuditLogStatus()
{
   var result = remote.call("/api/rma/admin/rmauditlog");
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      return data.data;
   }
   else return {
      enabled:false,
      started:"",
      stopped:"",
      entries:[]
   }
}

model.auditStatus = getAuditLogStatus()