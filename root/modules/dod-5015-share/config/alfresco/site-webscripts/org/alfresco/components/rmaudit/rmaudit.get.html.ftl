<script type="text/javascript" charset="utf-8">    
    new Alfresco.RM_Audit('audit').setOptions({'viewMode':Alfresco.RM_Audit.VIEW_MODE_COMPACT}).setMessages(${messages});
  </script>
<div id="audit">
   <div id="audit-search" class="theme-bg-color-1">
      <form>
        <label><input name="q" value=""></label>
        <label><input name="search" value="${msg("label.search")}" id="search" type="submit"/></label>
      </form>
   </div>
   <div id="audit-info">
      <h1>${msg("label.title")}</h1>
      <p>${msg("label.from", fromDate?string("EEE, dd MMM yyyy HH:mm:ss 'GMT'"))}</p>
      <p>${msg("label.to", toDate?string("EEE, dd MMM yyyy HH:mm:ss 'GMT'"))}</p>
   </div>
   <div id="auditDT"></div>
</div>
