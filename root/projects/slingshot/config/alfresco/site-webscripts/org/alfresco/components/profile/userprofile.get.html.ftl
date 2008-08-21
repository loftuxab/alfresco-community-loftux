<script type="text/javascript">//<![CDATA[
   var userProfile = new Alfresco.UserProfile("${args.htmlid}").setOptions(
   {
      userId: "${user.name}",
      profileId: "${profile.name}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<#assign editable = (user.name == profile.name)>
<div id="${args.htmlid}-body" class="profile">
   <div id="${args.htmlid}-readview" class="hidden">
      <#if editable>
      <div class="editcolumn">
         <div class="btn-edit"><button id="${args.htmlid}-button-edit" name="edit">${msg("button.editprofile")}</button></div>
      </div>
      </#if>
      <div class="viewcolumn">
         <div class="title"><#if editable>${msg("label.myprofile")}<#else>${profile.firstName!""} ${profile.lastName!""} ${msg("label.profile")}</#if></div>
         <div class="header-bar">${msg("label.about")}</div>
         <div class="photorow">
            <div class="photo">
               <img class="photoimg" src="${url.context}<#if profile.properties.avatar??>/proxy/alfresco/api/node/${profile.properties.avatar?replace('://','/')}/content/thumbnails/avatar?c=force<#else>/components/images/no-user-photo-64.png</#if>" alt="" />
            </div>
            <div class="namelabel">${profile.firstName!""} ${profile.lastName!""}</div>
            <#if profile.jobTitle?? && profile.jobTitle?length!=0><div class="fieldlabel">${profile.jobTitle?html}</div></#if>
            <#if profile.organization?? && profile.organization?length!=0><div class="fieldlabel">${profile.organization?html}</div></#if>
            <#if profile.location?? && profile.location?length!=0><div class="fieldlabel">${profile.location?html}</div></#if>
         </div>
         <#if biohtml?? && biohtml?length!=0>
         <div class="biorow">
            <hr/>
            <div>${biohtml}</div>
         </div>
         </#if>
         
         <div class="header-bar">${msg("label.contactinfo")}</div>
         <#if profile.email?? && profile.email?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.email")}:</span>
            <span class="fieldvalue">${profile.email?html}</span>
         </div>
         </#if>
         <#if profile.telephone?? && profile.telephone?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.telephone")}:</span>
            <span class="fieldvalue">${profile.telephone?html}</span>
         </div>
         </#if>
         <#if profile.mobilePhone?? && profile.mobilePhone?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.mobile")}:</span>
            <span class="fieldvalue">${profile.mobilePhone?html}</span>
         </div>
         </#if>
         <#if profile.skype?? && profile.skype?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.skype")}:</span>
            <span class="fieldvalue">${profile.skype?html}</span>
         </div>
         </#if>
         <#if profile.instantMsg?? && profile.instantMsg?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.im")}:</span>
            <span class="fieldvalue">${profile.instantMsg?html}</span>
         </div>
         </#if>
         
         <div class="header-bar">${msg("label.companyinfo")}</div>
         <#if profile.organization?? && profile.organization?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.name")}:</span>
            <span class="fieldvalue">${profile.organization?html}</span>
         </div>
         </#if>
         <#if (profile.companyAddress1?? && profile.companyAddress1?length!=0) ||
              (profile.companyAddress2?? && profile.companyAddress2?length!=0) ||
              (profile.companyAddress3?? && profile.companyAddress3?length!=0) ||
              (profile.companyPostcode?? && profile.companyPostcode?length!=0)>
         <div class="row">
            <span class="fieldlabelright">${msg("label.address")}:</span>
            <span class="fieldvalue"><#if profile.companyAddress1?? && profile.companyAddress1?length!=0>${profile.companyAddress1?html}<br /></#if>
               <#if profile.companyAddress2?? && profile.companyAddress2?length!=0>${profile.companyAddress2?html}<br /></#if>
               <#if profile.companyAddress3?? && profile.companyAddress3?length!=0>${profile.companyAddress3?html}<br /></#if>
               <#if profile.companyPostcode?? && profile.companyPostcode?length!=0>${profile.companyPostcode?html}</#if>
            </span>
         </div>
         </#if>
         <!--
         <div class="row">
            <span class="fieldlabelright">${msg("label.map")}:</span>
            <span class="fieldvalue"></span>
         </div>
         -->
         <#if profile.companyTelephone?? && profile.companyTelephone?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.telephone")}:</span>
            <span class="fieldvalue">${profile.companyTelephone?html}</span>
         </div>
         </#if>
         <#if profile.companyFax?? && profile.companyFax?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.fax")}:</span>
            <span class="fieldvalue">${profile.companyFax?html}</span>
         </div>
         </#if>
         <#if profile.companyemail?? && profile.companyemail?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.email")}:</span>
            <span class="fieldvalue">${profile.companyemail?html}</span>
         </div>
         </#if>
      </div>
   </div>
   
   <#if editable>
   <div id="${args.htmlid}-editview" class="hidden">
      <form id="${htmlid}-form" action="${url.serviceContext}/components/profile/userprofile" method="post">
      
      <div class="header-bar">${msg("label.about")}</div>
      <div class="drow">
         <div class="leftcolumn">
            <span class="label">${msg("label.firstname")}:</span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-firstName" value="${profile.firstName!""}" /></span>
         </div>
         <div class="rightcolumn">
            <span class="label">${msg("label.lastname")}:</span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-lastName" value="${profile.lastName!""}" /></span>
         </div>
      </div>
      <div class="drow">
         <div class="leftcolumn">
            <span class="label">${msg("label.title")}:</span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-jobtitle" value="${profile.jobTitle!""}" /></span>
         </div>
         <div class="rightcolumn">
            <span class="label">${msg("label.location")}:</span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-location" value="${profile.location!""}" /></span>
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
         <span class="input"><textarea id="${args.htmlid}-input-bio" name="${args.htmlid}-text-biography" rows="5" cols="60">${profile.biography!""}</textarea></span>
      </div>
      
      <div class="header-bar">${msg("label.photo")}</div>
      <div class="photorow">
         <div class="photo">
            <img class="photoimg" src="${url.context}<#if profile.properties.avatar??>/proxy/alfresco/api/node/${profile.properties.avatar?replace('://','/')}/content/thumbnails/avatar?c=force<#else>/components/images/no-user-photo-64.png</#if>" alt="" />
         </div>
         <div class="photobtn">
            <button id="${args.htmlid}-button-upload" name="upload">${msg("button.upload")}</button>
            <div class="phototxt">${msg("label.photoimagesize")}</div>
            <div class="phototxt">${msg("label.photonote")}</div>
         </div>
      </div>
      
      <div class="header-bar">${msg("label.contactinfo")}</div>
      <div class="row">
         <span class="label">${msg("label.telephone")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-telephone" value="${profile.telephone!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.mobile")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-mobile" value="${profile.mobilePhone!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.email")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-email" value="${profile.email!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.skype")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-skype" value="${profile.skype!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.im")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-instantmsg" value="${profile.instantMsg!""}" /></span>
      </div>
      
      <div class="header-bar">${msg("label.companyinfo")}</div>
      <div class="row">
         <span class="label">${msg("label.name")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-organization" value="${profile.organization!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.address")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companyaddress1" value="${profile.companyAddress1!""}" /></span>
      </div>
      <div class="row">
         <span class="label"></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companyaddress2" value="${profile.companyAddress2!""}" /></span>
      </div>
      <div class="row">
         <span class="label"></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companyaddress3" value="${profile.companyAddress3!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.postcode")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companypostcode" value="${profile.companyPostcode!""}" /></span>
      </div>
      <!--
      <div class="row">
         <span class="label">${msg("label.map")}:</span>
         <span class="check"><input type="checkbox" id="${args.htmlid}-input-showmap" /> ${msg("label.showmap")}</span>
      </div>
      -->
      <div class="row">
         <span class="label">${msg("label.telephone")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companytelephone" value="${profile.companyTelephone!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.fax")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companyfax" value="${profile.companyFax!""}" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.email")}:</span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${args.htmlid}-input-companyemail" value="${profile.companyEmail!""}" /></span>
      </div>
      
      <hr/>
      
      <div class="buttons">
         <button id="${args.htmlid}-button-save" name="save">${msg("button.savechanges")}</button>
         <button id="${args.htmlid}-button-cancel" name="cancel">${msg("button.cancel")}</button>
      </div>
      
      </form>
   </div>
   </#if>

</div>