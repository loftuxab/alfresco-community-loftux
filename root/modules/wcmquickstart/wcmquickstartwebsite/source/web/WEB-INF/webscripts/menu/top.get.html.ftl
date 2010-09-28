<#macro outputMenu sections>
    <ul>
		<#list sections as section>
            <#if ! section.excludeFromNav>
    			<li><a href="${url.context}${section.path}">${section.title!section.name}</a>
    				<@outputMenu sections=section.sections/>	
    			</li>
    		</#if>	
		</#list>
	</ul>
</#macro>

<div id="myslidemenu" class="jqueryslidemenu">
    <ul class="primary-menu">
		<li><a href="${url.context}/" accesskey="1">${msg('nav.home')}</a></li>
		<#list webSite.sections as section>
		    <#if ! section.excludeFromNav>
    			<li><a href="${url.context}${section.path}" accesskey="${section_index+2}">${section.title!section.name}</a>
    				<@outputMenu sections=section.sections/>	
    			</li>
    		</#if>	
		</#list>
	</ul>
</div>