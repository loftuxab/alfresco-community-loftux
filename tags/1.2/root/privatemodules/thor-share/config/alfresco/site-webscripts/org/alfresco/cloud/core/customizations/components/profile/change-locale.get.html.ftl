<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   var userProfile = new Alfresco.ChangeLocale("${el}").setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="locale profile">

   <div class="header-bar">${msg("label.changelocale")}</div>
   <div class="row">
      <span class="label"><label for="${el}-language">${msg("label.language")}:</label></span>
      <span><select id="${el}-language">
      <#list languages as lang>
         <option <#if lang.selected??>selected="selected"</#if> value="${lang.locale}">${lang.name}</option>
      </#list>
      </select></span>
   </div>

   <hr/>
   
   <div class="buttons">
      <button id="${el}-button-ok" name="save">${msg("button.ok")}</button>
      <button id="${el}-button-cancel" name="cancel">${msg("button.cancel")}</button>
   </div>
   
</div>