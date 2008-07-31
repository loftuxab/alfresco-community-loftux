<#assign activePage = page.url.templateArgs.pageid?lower_case!"">
<div id="${args.htmlid}-body" class="members-bar">

   <div class="member-link"><a href="#site-members" <#if activePage == "site-members">class="activePage"</#if>>${msg("link.site-members")}</a></div>
   <div class="separator">|</div>
   <div class="member-link"><a href="#sent-invites" <#if activePage == "sent-invites">class="activePage"</#if>>${msg("link.sent-invites")}</a></div>
   <div class="separator">|</div>
   <div class="member-link"><a href="invite" <#if activePage == "invite">class="activePage"</#if>>${msg("link.invite")}</a></div>

</div>