
<script type="text/javascript">//<![CDATA[
   new Alfresco.AddEmailInvite("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="inviteusersbyemail">
   <div class="title">${msg("addemail.title")}</div>
   <div class="body">      
      <div class="user">
         <div class="labels">
            <span class="fname">${msg("addemail.firstname")}:</span>
            <span class="lname">${msg("addemail.lastname")}:</span>
            <span class="email">${msg("addemail.email")}:</span>      
         </div>            
         <div class="inputs">
            <span class="fnamei"><input type="text" id="${args.htmlid}-firstname" /> </span>
            <span class="lnamei"><input type="text" id="${args.htmlid}-lastname" /> </span>
            <span class="emaili"><input type="text" id="${args.htmlid}-email" /> </span>
         </div>
         <div class="badd">
            <span id="${args.htmlid}-add-email-button" class="yui-button"> 
               <span class="first-child"> 
                  <button type="button">${msg("addemail.add")} &gt;&gt;</button> 
               </span> 
            </span>
         </div>
      </div>
   </div> 
</div>
