<#assign el=args.htmlid?html/>
<div id="${el}-body" class="cloud-create-users">
   <div class="hd">${msg("header")}</div>
   <div class="bd">
      <form id="${el}-form" method="POST" action="${form.url}" enctype="application/json" class="form-fields">
         <#list (form.hidden!{})?keys as key>
         <input type="hidden" name="${key}" value="${form.hidden[key]?js_string}"/>
         </#list>

         <!-- Emails -->
         <div class="form-field">
            <label for="${el}-emails">${msg("label.emails")}:</label><br/>
            <textarea id="${el}-emails" name="emails" tabindex="0" class="wide"></textarea><br>
            <p class="tiny">${msg("help.emails")}</p>
         </div>

         <!-- Message -->
         <div class="form-field">
            <label for="${el}-message">${msg("label.message")}:</label><br/>
            <textarea id="${el}-message" name="message" tabindex="0" class="wide">${msg("text.message")}</textarea>
         </div>

         <!-- Buttons -->
         <div class="bdft">
            <button id="${el}-submit">${msg("button.create")}</button>
            <button id="${el}-cancel">${msg("button.cancel")}</button>
         </div>
      </form>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
new Alfresco.cloud.component.CreateUsers("${args.htmlid?js_string}").setOptions({}).setMessages(${messages});
//]]></script>
