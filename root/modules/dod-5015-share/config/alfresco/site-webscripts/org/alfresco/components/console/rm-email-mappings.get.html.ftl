<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<script type="text/javascript" charset="utf-8">
   new Alfresco.RM.EmailMappings('${htmlid}-emailMappings').setOptions({
      email:[
         'messageFrom',
         'messageTo',
         'messageCc',
         'messageSubject',
         'messageSent'
         ],
      rm: [
         'imap:threadIndex',
         'imap:messageFrom',
         'imap:messageCc',
         'imap:messageSubject',
         'cm:description',
         'cm:title'
         ]
     }).setMessages(${messages});
</script>

<div id="${htmlid}-emailMappings" class="emailMappings">
   <div>
      <h2>${msg('label.email-mappings')}</h2>
      <div>
         <span>${msg('label.map')}</span>
         <input type="text" name="emailProperty-text" value="" id="emailProperty-text" />
         <button id="emailProperty-but" name="emailProperty-but" class="thin-button"><img src="${page.url.context}/components/images/expanded.png" title="${msg('label.select-email')}"/></button>
         <div id="email-menu-container"></div>
         <span>${msg('label.to')}</span>
         <input type="text" name="rmProperty-text" value="" id="rmProperty-text" />
         <button id="rmProperty-but" name="rmProperty-but" class="thin-button"><img src="${page.url.context}/components/images/expanded.png" title="${msg('label.select-rm')}"/></button>
         <div id="rm-menu-container"></div>
         <button id="add-mapping" name="email-add" class="thin-button" disabled>${msg('label.add')}</button>
      </div>
   </div>
   <div id="emailMappings-list" class="emailMappings-list">
      <ul>
      </ul>
   </div>
   <div id="emailMappings-actions" class="emailMappings-actions">
      <input type="submit" name="save-mappings" value="${msg('label.save')}" id="save-mappings" disabled/>
      <button id="discard-mappings" name="discard-mappings" disabled>${msg('label.discard')}</button>
   </div>
</div>
</#if>