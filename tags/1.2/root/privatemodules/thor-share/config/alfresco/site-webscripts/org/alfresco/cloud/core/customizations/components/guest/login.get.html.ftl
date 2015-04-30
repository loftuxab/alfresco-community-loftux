<#assign el=args.htmlid?html/>
<@markup id="cloud-core-css-imports" action="after" target="css">
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/cloud/customizations/components/guest/login.css" />
</@markup>
<@markup id="cloud-core-language-selector" action="after" target="fields">
   <div class="form-field">
      <label for="${el}-language">${msg("label.language")}</label><br/>
      <select id="${el}-language">
         <#list languages as lang>
         <option <#if lang.selected??>selected="selected"</#if> value="${lang.locale}">${lang.name}</option>
         </#list>
      </select>
   </div>
</@markup>
<@markup id="cloud-core-buttons" action="after" target="buttons">
   <a href="${config.scoped["Cloud"]["signup"].getChildValue("url")}?utm_medium=cloudapp&utm_source=Login" class="theme-color-1">${msg("label.signup")}</a>
   <span class="cloud-core-login-separator">|</span>
   <a href="${url.context?matches("\\/[^\\/]+")[0]}/page/forgot-password" class="theme-color-1 no-wrap">${msg("label.forgot-password")}</a>
</@markup>