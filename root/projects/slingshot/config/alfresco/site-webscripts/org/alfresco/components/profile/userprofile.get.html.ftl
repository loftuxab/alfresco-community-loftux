<script type="text/javascript">//<![CDATA[
   var userProfile = new Alfresco.UserProfile("${args.htmlid}").setOptions(
   {
      userId: "${user.name}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="profile">
   <div id="${args.htmlid}-readview" class="hidden">
      <div class="editcolumn">
         <a href="#" onclick="javascript:userProfile.onEditProfile();">${msg("label.editprofile")}</a>
      </div>
      <div class="viewcolumn">
         <div class="header-bar">${msg("label.info")}</div>
         <div class="photorow">
            <div class="photo">
               <img src="${url.context}<#if user.properties.avatar??>/proxy/alfresco/api/node/${user.properties.avatar?replace('://','/')}/content/thumbnails/avatar?qc=false&ph=false<#else>/components/images/no-photo.png</#if>" alt="" />
            </div>
            <div class="namelabel">${user.firstName!""} ${user.lastName!""}</div>
            <#if user.jobTitle?? && user.jobTitle?length!=0><div class="fieldlabel">${user.jobTitle?html}</div></#if>
            <#if user.organization?? && user.organization?length!=0><div class="fieldlabel">${user.organization?html}</div></#if>
            <#if user.location?? && user.location?length!=0><div class="fieldlabel">${user.location?html}</div></#if>
         </div>
         <#if user.biography?? && user.biography?length!=0>
         <div class="biorow">
            <hr/>
            <div>${user.biography?html}</div>
         </div>
         </#if>
         
         <div class="header-bar">${msg("label.contact")}:</div>
         <#if user.email?? && user.email?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.email")}:</span>
            <span class="fieldvalue">${user.email?html}</span>
         </div>
         </#if>
         <#if user.telephone?? && user.telephone?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.telephone")}:</span>
            <span class="fieldvalue">${user.telephone?html}</span>
         </div>
         </#if>
         <#if user.mobilePhone?? && user.mobilePhone?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.mobile")}:</span>
            <span class="fieldvalue">${user.mobilePhone?html}</span>
         </div>
         </#if>
         <#if user.skype?? && user.skype?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.skype")}:</span>
            <span class="fieldvalue">${user.skype?html}</span>
         </div>
         </#if>
         <#if user.instantMsg?? && user.instantMsg?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.im")}:</span>
            <span class="fieldvalue">${user.instantMsg?html}</span>
         </div>
         </#if>
         
         <div class="header-bar">${msg("label.companyinfo")}</div>
         <#if user.organization?? && user.organization?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.name")}:</span>
            <span class="fieldvalue">${user.organization?html}</span>
         </div>
         </#if>
         <#if (user.companyAddress1?? && user.companyAddress1?length!=0) ||
              (user.companyAddress2?? && user.companyAddress2?length!=0) ||
              (user.companyAddress3?? && user.companyAddress3?length!=0) ||
              (user.companyPostcode?? && user.companyPostcode?length!=0)>
         <div class="row">
            <span class="fieldlabelright">${msg("label.address")}:</span>
            <span class="fieldvalue"><#if user.companyAddress1?? && user.companyAddress1?length!=0>${user.companyAddress1?html}</#if>
               <#if user.companyAddress2?? && user.companyAddress2?length!=0><p>${user.companyAddress2?html}</p></#if>
               <#if user.companyAddress3?? && user.companyAddress3?length!=0><p>${user.companyAddress3?html}</p></#if>
               <#if user.companyPostcode?? && user.companyPostcode?length!=0><p>${user.companyPostcode?html}</p></#if>
            </span>
         </div>
         </#if>
         <!--
         <div class="row">
            <span class="fieldlabelright">${msg("label.map")}:</span>
            <span class="fieldvalue"></span>
         </div>
         -->
         <#if user.companyTelephone?? && user.companyTelephone?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.telephone")}:</span>
            <span class="fieldvalue">${user.companyTelephone?html}</span>
         </div>
         </#if>
         <#if user.companyFax?? && user.companyFax?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.fax")}:</span>
            <span class="fieldvalue">${user.companyFax?html}</span>
         </div>
         </#if>
         <#if user.companyemail?? && user.companyemail?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.email")}:</span>
            <span class="fieldvalue">${user.companyemail?html}</span>
         </div>
         </#if>
      </div>
   </div>
   
   <div id="${args.htmlid}-editview" class="hidden">
      <form id="${htmlid}-form" name="${htmlid}-form" action="${url.serviceContext}/components/profile/userprofile" method="POST">
      
      <div class="header-bar">${msg("label.info")}</div>
      <div class="drow">
         <div class="leftcolumn">
            <span class="label">${msg("label.firstname")}:</span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-firstName" value="${user.firstName!""}" /></span>
         </div>
         <div class="rightcolumn">
            <span class="label">${msg("label.lastname")}:</span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-lastName" value="${user.lastName!""}" /></span>
         </div>
      </div>
      <div class="drow">
         <div class="leftcolumn">
            <span class="label">${msg("label.title")}:</span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-jobtitle" value="${user.jobTitle!""}" /></span>
         </div>
         <div class="rightcolumn">
            <span class="label">${msg("label.location")}:</span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-location" value="${user.location!""}" /></span>
         </div>
      </div>
      <!--
      <div class="drow">
         <div class="leftcolumn">
            <span class="label">${msg("label.timezone")}:</span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-timezone" /></span>
         </div>
         <div class="rightcolumn">
         </div>
      </div>
      -->
      <div class="row">
         <span class="label">${msg("label.bio")}:</span>
         <span class="input"><textarea id="${args.htmlid}-input-bio" name="${args.htmlid}-text-biography" rows="5" cols="60">${user.biography!""}</textarea></span>
      </div>
      
      <div class="header-bar">${msg("label.photo")}:</div>
      <div class="row">
         <div class="photo">
            <img src="${url.context}<#if user.properties.avatar??>/proxy/alfresco/api/node/content/${user.properties.avatar?replace('://','/')}/avatar.jpg<#else>/components/images/no-photo.png</#if>" width="64" height="64" alt="" />
         </div>
         <div class="photobtn"><button id="${args.htmlid}-button-upload" name="upload">${msg("button.upload")}</button></div>
      </div>
      
      <div class="header-bar">${msg("label.contact")}:</div>
      <div class="row">
         <span class="label">${msg("label.telephone")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-telephone" value="${user.telephone!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.mobile")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-mobile" value="${user.mobilePhone!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.email")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-email" value="${user.email!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.skype")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-skype" value="${user.skype!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.im")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-instantmsg" value="${user.instantMsg!""}" /></span>
      </div>
      
      <div class="header-bar">${msg("label.companyinfo")}</div>
      <div class="row">
         <span class="label">${msg("label.name")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-organization" value="${user.organization!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.address")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companyaddress1" value="${user.companyAddress1!""}" /></span>
      </div>
      <div class="row">
         <span class="label"></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companyaddress2" value="${user.companyAddress2!""}" /></span>
      </div>
      <div class="row">
         <span class="label"></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companyaddress3" value="${user.companyAddress3!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.postcode")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companypostcode" value="${user.companyPostcode!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.map")}:</span>
         <span class="check"><input type="checkbox" id="${args.htmlid}-input-showmap" /> ${msg("label.showmap")}</span>
      </div>
      <div class="row">
         <span class="label">${msg("label.telephone")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companytelephone" value="${user.companyTelephone!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.fax")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companyfax" value="${user.companyFax!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.email")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companyemail" value="${user.companyEmail!""}" /></span>
      </div>
      
      <hr/>
      
      <div class="buttons">
         <button id="${args.htmlid}-button-save" name="save">${msg("button.savechanges")}</button>
         <button id="${args.htmlid}-button-cancel" name="cancel">${msg("button.cancel")}</button>
      </div>
      
      </form>
   </div>

</div>