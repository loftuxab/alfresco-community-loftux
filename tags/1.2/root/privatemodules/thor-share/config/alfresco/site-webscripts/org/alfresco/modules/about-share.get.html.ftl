<#assign el=args.htmlid?html>
<#assign aboutConfig=config.scoped["Edition"]["about"]>
<div id="${el}-dialog" class="about-share">
   <div class="bd">
      <div id="${el}-logo" class="logo-vanilla logo">
         <div class="about">
            <#assign split=serverVersion?index_of(" ")>
            <div class="header">Alfresco Cloud v${serverVersion?substring(0, split)?html}</div>
            <div>${serverVersion?substring(split+1)?html} schema ${serverSchema?html}</div>
            <div class="header"></div>
            <div></div>
            <div class="contributions-bg"></div>
            <div class="contributions-wrapper">
               <div id="${el}-contributions" class="contributions">
               </div>
            </div>
            <div class="copy" style="margin-top:5em">&copy; 2005-2015 Alfresco Software Inc. All rights reserved.</div>
            <div class="copy">
               <a href="http://www.alfresco.com" target="new">www.alfresco.com</a> |
               <a href="${config.scoped["Cloud"]["legal"].getChildValue("terms")}" target="_blank">Terms and Service</a> |
               <a href="${config.scoped["Cloud"]["legal"].getChildValue("privacy")}" target="_blank">Privacy Policy</a>
            </div>
         </div>
      </div>
   </div>
</div>