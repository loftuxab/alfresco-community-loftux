<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<script type="text/javascript" charset="utf-8">    
    new Alfresco.RM.EmailMappings('${htmlid}-emailMappings').setOptions({
       mappings:[],
       email:[
          'Thread-Index',
          'messageSubject',
          'message-ID',
          'message-To',
          'message-From',
          'messageCc'
          ],
       rm: [
          'imap:threadIndex',
          'cm:description',
          'imap:messageSubject',
          'cm:title',
          'imap:messageFrom',
          'imap:messageCc'
          ]
      }).setMessages(${messages});
</script>

  <div id="${htmlid}-emailMappings" class="emailMappings">
    <div class="yui-gc">
   	<div class="yui-u first">
         <h2>${msg('label.email-mappings')}</h2>
		   <div>
            <span>${msg('label.map')}</span>
            <input type="text" name="emailProperty-text" value="" id="emailProperty-text" /> 
            <button id="emailProperty-but" name="emailProperty-but"><img src="${page.url.context}/components/images/expanded.png" title="${msg('label.select-email')}"/></button>      
            <div id="email-menu-container"></div>
            <span>to</span>
            <input type="text" name="rmProperty-text" value="" id="rmProperty-text" />               
            <button id="rmProperty-but" name="rmProperty-but"><img src="${page.url.context}/components/images/expanded.png" title="${msg('label.select-rm')}"/></button>
            <div id="rm-menu-container"></div>
            <button id="add-mapping" name="email-add" disabled>${msg('label.add')}</button>              
         </div>
   	</div>
    </div>    
    <div id="emailMappings-list" class="yui-gc emailMappings-list">
   	<ul>
   	</ul>
    </div>
    <div id="emailMappings-actions" class="emailMappings-actions">
       <input type="submit" name="save-mappings" value="${msg('label.save')}" id="save-mappings" disabled/>
       <button id="discard-mappings" name="discard-mappings" disabled>${msg('label.discard')}</button>    
    </div>
  </div>
</#if>