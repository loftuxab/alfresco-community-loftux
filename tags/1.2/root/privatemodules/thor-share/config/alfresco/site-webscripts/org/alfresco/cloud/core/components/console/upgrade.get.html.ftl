<#assign el=args.htmlid?html/>
<div class="cloud-upgrade">
   <div class="share-form">
      <div class="form-manager">
         <h1>${msg("header.upgrade")}</h1>
      </div>
      <div class="form-container">
         <div class="form-fields">
            <div class="form-field">
               ${msg("text.upgrade")}
            </div>
            <div class="form-field">
               <span id="${el}-upgrade" class="yui-button alfresco-attention-yui-button">
                  <span class="first-child">
                     <a href="http://www.alfresco.com/products/cloud/upgrade" target="_blank" tabindex="0">${msg("button.upgrade")}</a>
                  </span>
               </span>
            </div>
         </div>
      </div>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
new Alfresco.cloud.component.Upgrade("${args.htmlid?js_string}").setOptions({}).setMessages(${messages});
//]]></script>