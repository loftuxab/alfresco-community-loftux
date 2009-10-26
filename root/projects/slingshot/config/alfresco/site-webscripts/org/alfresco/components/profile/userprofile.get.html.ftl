<script type="text/javascript">//<![CDATA[
   var userProfile = new Alfresco.UserProfile("${args.htmlid}").setOptions(
   {
      userId: "${user.name}",
      profile: {
         name: "<#if profile.name??>${profile.name?html}</#if>",
         lastName: "<#if profile.lastName??>${profile.lastName?html}</#if>",
         firstName: "<#if profile.firstName??>${profile.firstName?html}</#if>",
         jobtitle: "<#if profile.jobTitle??>${profile.jobTitle?html}</#if>",
         location: "<#if profile.location??>${profile.location?html}</#if>",
         bio: "<#if profile.biography??>${profile.biography?html}</#if>",
         telephone: "<#if profile.telephone??>${profile.telephone?html}</#if>",
         mobile: "<#if profile.mobilePhone??>${profile.mobilePhone?html}</#if>",
         email :"<#if profile.email??>${profile.email?html}</#if>",
         skype: "<#if profile.skype??>${profile.skype?html}</#if>",
         instantmsg: "<#if profile.instantMsg??>${profile.instantMsg?html}</#if>",
         organization: "<#if profile.organization??>${profile.organization?html}</#if>",
         companyaddress1: "<#if profile.companyAddress1??>${profile.companyAddress1?html}</#if>",
         companyaddress2: "<#if profile.companyAddress2??>${profile.companyAddress2?html}</#if>",
         companyaddress3:"<#if profile.companyAddress3??>${profile.companyAddress3?html}</#if>",
         companypostcode: "<#if profile.companyPostcode??>${profile.companyPostcode?html}</#if>",
         companytelephone: "<#if profile.companyTelephone??>${profile.companyTelephone?html}</#if>",
         companyfax: "<#if profile.companyFax??>${profile.companyFax?html}</#if>",
         companyemail: "<#if profile.companyEmail??>${profile.companyEmail?html}</#if>"
      }
   }).setMessages(
      ${messages}
   );
//]]></script>

<#assign el=args.htmlid>
<#assign editable = (user.name == profile.name)>
<#assign displayname=profile.firstName>
<#if profile.lastName??><#assign displayname=displayname + " " + profile.lastName></#if>
<div id="${el}-body" class="profile">
   <div id="${el}-readview" class="hidden">
      <#if editable>
      <div class="editcolumn">
         <div class="btn-edit"><button id="${el}-button-edit" name="edit">${msg("button.editprofile")}</button></div>
      </div>
      </#if>
      <div class="viewcolumn">
         <div class="header-bar">${msg("label.about")}</div>
         <div class="photorow">
            <div class="photo">
               <img class="photoimg" src="${url.context}<#if profile.properties.avatar??>/proxy/alfresco/api/node/${profile.properties.avatar?replace('://','/')}/content/thumbnails/avatar?c=force<#else>/components/images/no-user-photo-64.png</#if>" alt="" />
            </div>
            <div class="namelabel">${displayname?html}</div>
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
         <#if profile.companyEmail?? && profile.companyEmail?length!=0>
         <div class="row">
            <span class="fieldlabelright">${msg("label.email")}:</span>
            <span class="fieldvalue">${profile.companyEmail?html}</span>
         </div>
         </#if>
      </div>
   </div>
   
   <#if editable>
   <div id="${el}-editview" class="hidden">
      <form id="${htmlid}-form" action="${url.context}/service/components/profile/userprofile" method="post">
      
      <div class="header-bar">${msg("label.about")}</div>
      <div class="drow">
         <div class="reqcolumn">&nbsp;*</div>
         <div class="rightcolumn">
            <span class="label"><label for="${el}-input-lastName">${msg("label.lastname")}:</label></span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-lastName" value="" /></span>
         </div>
         <div class="leftcolumn">
            <span class="label"><label for="${el}-input-firstName">${msg("label.firstname")}:</label></span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-firstName" value="" />&nbsp;*</span>
         </div>
      </div>
      <div class="drow">
         <div class="reqcolumn">&nbsp;</div>         
         <div class="leftcolumn">
            <span class="label"><label for="${el}-input-jobtitle">${msg("label.jobtitle")}:</label></span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-jobtitle" value="" /></span>
         </div>
         <div class="rightcolumn">
            <span class="label"><label for="${el}-input-location">${msg("label.location")}:</label></span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-location" value="" /></span>
         </div>
      </div>
      <!--
      <div class="drow">
         <div class="leftcolumn">
            <span class="label">${msg("label.timezone")}:</span>
            <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-timezone" /></span>
         </div>
         <div class="rightcolumn">
         </div>
      </div>
      -->
      <div class="row">
         <span class="label"><label for="${el}-input-bio">${msg("label.bio")}:</label></span>
         <span class="input"><textarea id="${el}-input-bio" name="${el}-text-biography" rows="5" cols="60"></textarea></span>
      </div>
      
      <div class="header-bar">${msg("label.photo")}</div>
      <div class="photorow">
         <div class="photo">
            <img class="photoimg" src="${url.context}<#if profile.properties.avatar??>/proxy/alfresco/api/node/${profile.properties.avatar?replace('://','/')}/content/thumbnails/avatar?c=force<#else>/components/images/no-user-photo-64.png</#if>" alt="" />
         </div>
         <div class="photobtn">
            <button id="${el}-button-upload" name="upload">${msg("button.upload")}</button>
            <div class="phototxt">${msg("label.photoimagesize")}</div>
            <div class="phototxt">${msg("label.photonote")}</div>
         </div>
      </div>
      
      <div class="header-bar">${msg("label.contactinfo")}</div>
      <div class="row">
         <span class="label"><label for="${el}-input-telephone">${msg("label.telephone")}:</label></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-telephone" value="" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${el}-input-mobile">${msg("label.mobile")}:</label></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-mobile" value="" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${el}-input-email">${msg("label.email")}:</label></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-email" value="" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${el}-input-skype">${msg("label.skype")}:</label></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-skype" value="" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${el}-input-instantmsg">${msg("label.im")}:</label></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-instantmsg" value="" /></span>
      </div>
      
      <div class="header-bar">${msg("label.companyinfo")}</div>
      <div class="row">
         <span class="label"><label for="${el}-input-organization">${msg("label.name")}:</label></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-organization" value="" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${el}-input-companyaddress1">${msg("label.address")}:</label></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-companyaddress1" value="" /></span>
      </div>
      <div class="row">
         <span class="label"></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-companyaddress2" value="" /></span>
      </div>
      <div class="row">
         <span class="label"></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-companyaddress3" value="" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${el}-input-companypostcode">${msg("label.postcode")}:</label></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-companypostcode" value="" /></span>
      </div>
      <!--
      <div class="row">
         <span class="label">${msg("label.map")}:</span>
         <span class="check"><input type="checkbox" id="${el}-input-showmap" /> ${msg("label.showmap")}</span>
      </div>
      -->
      <div class="row">
         <span class="label"><label for="${el}-input-companytelephone">${msg("label.telephone")}:</label></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-companytelephone" value="" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${el}-input-companyfax">${msg("label.fax")}:</label></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-companyfax" value="" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${el}-input-companyemail">${msg("label.email")}:</label></span>
         <span class="input"><input type="text" maxlength="256" size="30" id="${el}-input-companyemail" value="" /></span>
      </div>
      
      <hr/>
      
      <div class="buttons">
         <button id="${el}-button-save" name="save">${msg("button.savechanges")}</button>
         <button id="${el}-button-cancel" name="cancel">${msg("button.cancel")}</button>
      </div>
      
      </form>
   </div>
   </#if>

</div>