function getDocumentDetailsUrl(shareId)
{
   // todo ask repo for the noderef, site & tenant
   var result = remote.connect("alfresco").get("/api/internal/shared/share/" + encodeURIComponent(page.url.args.id));
   if (result.status == 200)
   {
      var info = JSON.parse(result),
         documentDetailsUrl = url.context.match("/[^/]+")[0] + "/" + encodeURIComponent(info.tenantDomain || "-system-");
      documentDetailsUrl += "/page";
      if (info.siteId)
      {
         documentDetailsUrl += '/site/' + encodeURIComponent(info.siteId);
      }
      documentDetailsUrl += '/document-details?nodeRef=' + encodeURIComponent(info.nodeRef);
      return documentDetailsUrl;
   }
   else
   {
      return url.context;
   }
}

function main()
{
   model.redirectUrl = getDocumentDetailsUrl(page.url.args.id);
}

main();
