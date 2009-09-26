<#if !hasAccess>
   <#include "./rm-console-access.ftl">
<#else>
<script type="text/javascript" charset="utf-8">
   new Alfresco.RM.EmailMappings('${htmlid}').setOptions({
      email:[
         'Thread-Index',
         'messageFrom',
         'messageTo',
         'messageCc',
         'messageSubject',
         'messageSent'
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
         <button id="${htmlid}-rmproperty-button" name="rmproperty" class="thin-button">${msg("label.name")}</button>
         <select id="${htmlid}-rmproperty-button-menu">
            <option value="imap:threadIndex">${msg("label.imap.threadIndex")}</option>
            <option value="imap:messageFrom">${msg("label.imap.messageFrom")}</option>
            <option value="imap:messageTo">${msg("label.imap.messageTo")}</option>
            <option value="imap:messageCc">${msg("label.imap.messageCc")}</option>
            <option value="imap:messageSubject">${msg("label.imap.messageSubject")}</option>
            <option value="imap:dateReceived">${msg("label.imap.dateReceived")}</option>
            <option value="imap:dateSent">${msg("label.imap.dateSent")}</option>
            <option value="cm:name">${msg("label.name")}</option>
            <option value="cm:title">${msg("label.title")}</option>
            <option value="cm:description">${msg("label.description")}</option>
            <option value="cm:creator">${msg("label.creator")}</option>
            <option value="cm:created">${msg("label.created")}</option>
            <option value="cm:modifier">${msg("label.modifier")}</option>
            <option value="cm:modified">${msg("label.modified")}</option>
            <option value="cm:author">${msg("label.author")}</option>
            <option value="rma:originator">${msg("label.originator")}</option>
            <option value="rma:dateFiled">${msg("label.dateFiled")}</option>
            <option value="rma:publicationDate">${msg("label.publicationDate")}</option>
            <option value="rma:reviewAsOf">${msg("label.reviewDate")}</option>
            <option value="rma:originatingOrganization">${msg("label.originatingOrganization")}</option>
            <option value="rma:mediaType">${msg("label.mediaType")}</option>
            <option value="rma:format">${msg("label.format")}</option>
            <option value="rma:dateReceived">${msg("label.dateReceived")}</option>
            <option value="rma:location">${msg("label.location")}</option>
            <option value="rma:address">${msg("label.address")}</option>
            <option value="rmc:supplementalMarkingList">${msg("label.supplementalMarkingList")}</option>
            <!-- double ?html encoding required here due to YUI bug -->
            <#list meta as d>
            <option value="${d.name}">${d.title?html?html}</option>
            </#list>
         </select>
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