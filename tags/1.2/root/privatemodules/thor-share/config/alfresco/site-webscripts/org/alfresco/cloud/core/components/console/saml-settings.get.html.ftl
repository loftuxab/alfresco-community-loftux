<@markup id="css" >
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/cloud/components/console/saml-settings.css" group="cloud"  />
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/form/form.js" group="cloud"/>
   <@script type="text/javascript" src="${url.context}/res/cloud/components/console/saml-settings.js" group="cloud"/>
</@>

<@markup id="widgets">
   <@createWidgets group="cloud"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el = args.htmlid?html>

   <div class="share-form cloud-saml-settings">

      <#-- Account -->
      <div class="form-manager">
         <h1>${msg("header.samlSettings")}</h1>
      </div>
      <div class="form-container">
         <div class="caption">
            <input id="${el}-ssoEnabledButton" name="-" type="checkbox"> ${msg("ssoEnabled.label")}
         </div>
         <script type="text/javascript">
            <#assign callback>onCloudSAMLSettingsComponent_${el?replace("-", "__")}</#assign>
            var ${callback}_success = function(response)
            {
               var component = Alfresco.util.ComponentManager.get('${el}');
               component.onSaveSuccess.call(component, response)
            };
            var ${callback}_failure= function(response)
            {
               var component = Alfresco.util.ComponentManager.get('${el}');
               component.onSaveFailure.call(component, response)
            };
         </script>
         <form id="${el}-form" method="POST" enctype="multipart/form-data" accept-charset="utf-8" class="hidden">
            <input type="hidden" name="success" value="window.parent.${callback}_success"/>
            <input type="hidden" name="failure" value="window.parent.${callback}_failure"/>
            <input type="hidden" id="${el}-ssoEnabled" name="ssoEnabled" value="true"/>
            <div class="form-fields">

               <div class="set">
                  <div class="set-title">${msg("set.settings")}</div>

                  <#-- idpSsoURL -->
                  <div class="form-field">
                     <label for="${el}-idpSsoURL">${msg("idpSsoURL.label")}:<span class="mandatory-indicator">*</span></label>
                     <input id="${el}-idpSsoURL" name="idpSsoURL" tabindex="0" type="text" value="">
                     <span class="help-icon">
                        <img id="${el}-idpSsoURL-help-icon" src="${url.context}/res/components/form/images/help.png" title="${msg("form.field.help")}" tabindex="0">
                     </span>
                     <div class="help-text" id="${el}-idpSsoURL-help">${msg("idpSsoURL.help")?html}</div>
                  </div>

                  <#-- idpSloURL -->
                  <div class="form-field">
                     <label for="${el}-idpSloRequestURL">${msg("idpSloRequestURL.label")}:<span class="mandatory-indicator">*</span></label>
                     <input id="${el}-idpSloRequestURL" name="idpSloRequestURL" tabindex="0" type="text" value="">
                     <span class="help-icon">
                        <img id="${el}-idpSloRequestURL-help-icon" src="${url.context}/res/components/form/images/help.png" title="${msg("form.field.help")}" tabindex="0">
                     </span>
                     <div class="help-text" id="${el}-idpSloRequestURL-help">${msg("idpSloRequestURL.help")?html}</div>
                  </div>

                  <#-- idpSloResponseURL -->
                  <div class="form-field">
                     <label for="${el}-idpSloResponseURL">${msg("idpSloResponseURL.label")}:<span class="mandatory-indicator">*</span></label>
                     <input id="${el}-idpSloResponseURL" name="idpSloResponseURL" tabindex="0" type="text" value="">
                     <span class="help-icon">
                        <img id="${el}-idpSloResponseURL-help-icon" src="${url.context}/res/components/form/images/help.png" title="${msg("form.field.help")}" tabindex="0">
                     </span>
                     <div class="help-text" id="${el}-idpSloResponseURL-help">${msg("idpSloResponseURL.help")?html}</div>
                  </div>

                  <#-- certificate -->
                  <div class="form-field">
                     <label>${msg("certificate.label")}:<span class="mandatory-indicator">*</span></label>
                     <div id="${el}-certificate-details-container" class="certificate-details-container hidden">
                        <span id="${el}-certificate-details" class="certificate-details">${msg("certificate.details.label")}:</span>
                        <span id="${el}-certificate-status"></span>
                        <span id="${el}-certificate-expires"></span>
                        <span id="${el}-certificate-edit" class="certificate-edit" title="${msg("certificate.edit.title")}"></span>
                        <span><a href="${url.context}/proxy/alfresco/saml/idp/pubcert?a=true" class="download-link">${msg("certificate.downloadIdPCertificate")}</a></span>
                     </div>
                     <input id="${el}-certificate" class="hidden" name="certificate" tabindex="0" type="file">
                     <span class="help-icon">
                        <img id="${el}-certificate-help-icon" class="hidden" src="${url.context}/res/components/form/images/help.png" title="${msg("form.field.help")}" tabindex="0">
                     </span>
                     <div class="help-text" id="${el}-certificate-help">${msg("certificate.help")?html}</div>
                  </div>
               </div>

               <div class="set">

                  <div class="set-title">${msg("set.info")}</div>

                  <#-- issuer -->
                  <div class="form-field">
                     <label for="${el}-issuer">${msg("issuer.label")}:<span class="mandatory-indicator">*</span></label>
                     <input id="${el}-issuer" name="issuer" tabindex="0" type="text" value="">
                     <span class="help-icon">
                        <img id="${el}-issuer-help-icon" src="${url.context}/res/components/form/images/help.png" title="${msg("form.field.help")}" tabindex="0">
                     </span>
                     <div class="help-text" id="${el}-issuer-help">${msg("issuer.help")?html}</div>
                  </div>

                  <#-- shareEntrypointUrl -->
                  <div class="form-field">
                     <label for="${el}-shareEntrypointUrl">${msg("shareEntrypointUrl.label")}:</label>
                     <input id="${el}-shareEntrypointUrl" name="-" tabindex="0" type="text" readonly="true" disabled="true" value="">
                     <span class="help-icon">
                        <img id="${el}-shareEntrypointUrl-help-icon" src="${url.context}/res/components/form/images/help.png" title="${msg("form.field.help")}" tabindex="0">
                     </span>
                     <div class="help-text" id="${el}-shareEntrypointUrl-help">${msg("shareEntrypointUrl.help")?html}</div>
                  </div>

                  <div class="form-field">
                     <a href="${url.context}/proxy/alfresco/saml/sp/pubcert?a=true" class="download-link">${msg("downloadCertificate.label")}</a>
                     <a href="${url.context}/proxy/alfresco/saml/sp/metadata?a=true" class="download-link">${msg("downloadMetadata.label")}</a>
                  </div>
               </div>
            </div>

            <div class="form-buttons">
               <input id="${el}-save-button" type="button" value="${msg("save.button")}" tabindex="0" />
               <input id="${el}-reset-button" type="button" value="${msg("reset.button")}" tabindex="0" />
            </div>
         </form>
      </div>

   </div>
   </@>
</@>