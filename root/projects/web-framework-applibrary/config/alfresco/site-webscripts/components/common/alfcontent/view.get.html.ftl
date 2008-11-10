<span class="profile-label">User Name</span>
<a class="profile-name" href="${link}">
	${firstName} ${lastName}
</a>

<br/>

<#if title?exists>
	<span class="profile-label">Title</span>
	${title}
	<br/>
</#if>

<#if organisation?exists>
	<span class="profile-label">Organisation</span>
	${organisation}
	<br/>
</#if>

<#if jobtitle?exists>
	<span class="profile-label">Job Title</span>
	${jobtitle}
	<br/>
</#if>

<#if email?exists>
	<span class="profile-label">Email</span>
	${email}
	<br/>
</#if>

<br/>

<span class="profile-label">Avatar</span>
<br/>
<img src="${url.context}/proxy/alfresco/${avatar}" />

