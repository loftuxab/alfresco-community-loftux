<script type="text/javascript">//<![CDATA[
   new Alfresco.UserProfile("${args.htmlid}").setOptions(
   {
      userId: "${user.name}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="profile">
   
   <form id="${htmlid}-form" name="${htmlid}-form" method="POST">
   
   <div class="header-bar">${msg("label.info")}</div>
   <div class="drow">
      <div style="float:left">
         <span class="label">${msg("label.firstname")}:</span>
         <span class="input"><input type="text" size="30" id="${args.htmlid}-input-firstname" value="${user.firstName!""}" /></span>
      </div>
      <div style="float:right">
         <span class="label">${msg("label.lastname")}:</span>
         <span class="input"><input type="text" size="30" id="${args.htmlid}-input-lastname" value="${user.lastName!""}" /></span>
      </div>
   </div>
   <div class="drow">
      <div style="float:left">
         <span class="label">${msg("label.title")}:</span>
         <span class="input"><input type="text" size="30" id="${args.htmlid}-input-title" value="${user.jobTitle!""}" /></span>
      </div>
      <div style="float:right">
         <span class="label">${msg("label.location")}:</span>
         <span class="input"><input type="text" size="30" id="${args.htmlid}-input-location" value="${user.location!""}" /></span>
      </div>
   </div>
   <!--
   <div class="drow">
      <div style="float:left">
         <span class="label">${msg("label.timezone")}:</span>
         <span class="input"><input type="text" size="30" id="${args.htmlid}-input-timezone" /></span>
      </div>
      <div style="float:right">
      </div>
   </div>
   -->
   <div class="row">
      <span class="label">${msg("label.bio")}:</span>
      <span class="input"><textarea id="${args.htmlid}-input-bio" name="${args.htmlid}-input-bio" rows="4" cols="20">${user.biography!""}</textarea></span>
   </div>
   
   <div class="header-bar">${msg("label.photo")}:</div>
   <div class="row">
      <div class="photo"><img src="${url.context}/components/images/no-photo.png" width="64" height="64" alt="" /></div>
      <div class="photobtn"><button id="${args.htmlid}-button-upload" name="upload">${msg("button.upload")}</button></div>
   </div>
   
   <div class="header-bar">${msg("label.contact")}:</div>
   <div class="row">
      <span class="label">${msg("label.telephone")}:</span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-telephone" value="${user.telephone!""}" /></span>
   </div>
   <div class="row">
      <span class="label">${msg("label.mobile")}:</span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-mobile" value="${user.mobilePhone!""}" /></span>
   </div>
   <div class="row">
      <span class="label">${msg("label.email")}:</span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-email" value="${user.email!""}" /></span>
   </div>
   <div class="row">
      <span class="label">${msg("label.skype")}:</span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-skype" value="${user.skype!""}" /></span>
   </div>
   <div class="row">
      <span class="label">${msg("label.im")}:</span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-im" value="${user.instantMsg!""}" /></span>
   </div>
   
   <div class="header-bar">${msg("label.companyinfo")}</div>
   <div class="row">
      <span class="label">${msg("label.name")}:</span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-companyname" value="${user.organization!""}" /></span>
   </div>
   <div class="row">
      <span class="label">${msg("label.address")}:</span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-companyaddress1" value="${user.companyAddress1!""}" /></span>
   </div>
   <div class="row">
      <span class="label"></span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-companyaddress2" value="${user.companyAddress2!""}" /></span>
   </div>
   <div class="row">
      <span class="label"></span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-companyaddress3" value="${user.companyAddress3!""}" /></span>
   </div>
   <div class="row">
      <span class="label">${msg("label.postcode")}:</span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-companypostcode" value="${user.companyPostcode!""}" /></span>
   </div>
   <div class="row">
      <span class="label">${msg("label.map")}:</span>
      <span class="check"><input type="checkbox" id="${args.htmlid}-input-showmap" /> ${msg("label.showmap")}</span>
   </div>
   <div class="row">
      <span class="label">${msg("label.telephone")}:</span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-companytelephone" value="${user.companyTelephone!""}" /></span>
   </div>
   <div class="row">
      <span class="label">${msg("label.fax")}:</span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-companyfax" value="${user.companyFax!""}" /></span>
   </div>
   <div class="row">
      <span class="label">${msg("label.email")}:</span>
      <span class="input"><input type="text" size="30" id="${args.htmlid}-input-companyemail" value="${user.companyEmail!""}" /></span>
   </div>
   
   <hr/>
   
   <div class="buttons">
      <button id="${args.htmlid}-button-save" name="save">${msg("button.savechanges")}</button>
      <button id="${args.htmlid}-button-cancel" name="cancel">${msg("button.cancel")}</button>
   </div>
   
   </form>

</div>