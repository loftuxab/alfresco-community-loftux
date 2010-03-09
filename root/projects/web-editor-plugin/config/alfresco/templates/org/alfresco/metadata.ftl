<#include "include/awe.ftl" />
<@templateHeader>
</@>

<@templateBody>
   <div class="js-disabled-form">
      <h2>${url.args.title?html}</h2>
      <@region id="metadata" scope="template" />
      <div class="cancel-link"><a href="${url.args.redirect?html}">${msg("button.cancel")}</a></div>
   </div>
</@>