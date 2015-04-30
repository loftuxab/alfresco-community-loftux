<#-- Hide the email input row -->
<@markup id="cloud-editContactInfo" action="after" target="editContactInfo">
   <script type="text/javascript">
      YAHOO.util.Dom.addClass(YAHOO.util.Dom.get("${args.htmlid?js_string}-input-email").parentNode.parentNode, "hidden");
   </script>
</@markup>

<#-- Hide the Follow buttons, Contact Info and Company Info sections if user is not in the current network -->
<#if !(profile.properties["homeTenant"]??) || !(user.properties["homeTenant"]??) ||
     profile.properties["homeTenant"] != user.properties["homeTenant"]>
<@markup id="cloud-viewControls" action="remove" target="viewControls" />
<@markup id="cloud-viewContactInfo" action="remove" target="viewContactInfo" />
<@markup id="cloud-viewCompanyInfo" action="remove" target="viewCompanyInfo" />
</#if>