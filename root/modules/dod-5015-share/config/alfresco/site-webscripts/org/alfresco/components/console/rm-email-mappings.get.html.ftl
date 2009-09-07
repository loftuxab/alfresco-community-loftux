<script type="text/javascript" charset="utf-8">    
    new Alfresco.RM.EmailMappings('${htmlid}-emailMappings').setOptions({mappings:[]}).setMessages(${messages});
  </script>
  
  <div id="${htmlid}-emailMappings" class="emailMappings">
    <div class="yui-gc">
   	<div class="yui-u first">
         <h2>${msg('label.email-mappings')}</h2>
		   <div>
            <form action="email_submit" method="get" accept-charset="utf-8">
               <span>${msg('label.map')}</span>
               <input id="emailMappings-emailProperty-button" name="emailMappings-emailProperty-button" type="button" value="${msg('label.select-email')}" />
                  <select id="emailMappings-emailProperty-menu">
                     <option value="Thread-Index">Thread-Index</option>
                     <option value="messageSubject">messageSubject</option>
                     <option value="Message-ID">message-ID</option>
                     <option value="Message-To">message-To</option>                     
                     <option value="messageFrom">messageFrom</option>
                     <option value="messageCc">messageCc</option>                           
                  </select>
               <span>to</span>
               <input id="emailMappings-rmProperty-button" name="emailMappings-rmProperty-button" type="button" value="${msg('label.select-rm')}" />
                  <select id="emailMappings-rmProperty-menu">
                     <option value="imap:threadIndex" disabled >imap:threadIndex</option>
                     <option value="cm:description" disabled>cm:description</option>
                     <option value="imap:messageId">imap:messageId</option>
                     <option value="imap:messageSubject" disabled>imap:messageSubject</option>
                     <option value="cm:title">cm:title</option>
                     <option value="imap:messageFrom">imap:messageFrom</option>
                     <option value="imap:messageCc">imap:messageCc</option>                     
                  </select>
               <button id="add-mapping" name="email-add" disabled>${msg('label.add')}</button>              
            </form>
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