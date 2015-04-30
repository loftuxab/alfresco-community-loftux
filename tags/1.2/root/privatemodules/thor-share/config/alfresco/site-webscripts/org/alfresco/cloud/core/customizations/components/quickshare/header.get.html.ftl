<#-- Add cloud CSS -->
<@markup id="cloud-header-css" action="after" target="css">
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/cloud/components/quickshare/header-cloud.css" />
</@>

<#-- Add cloud signup info -->
<@markup id="cloud-signup" action="before" target="linkButtons">
   <#if user?? && user.isGuest>
      <span class="signup-label">${msg("label.signup")}</span>
      <a href='${config.scoped["Cloud"]["signup"].getChildValue("url")}?utm_medium=cloudapp&utm_source=QuickShare' class="quickshare-header-signup" tabindex="0">${msg("button.signup")}</a>
   </#if>
</@markup>