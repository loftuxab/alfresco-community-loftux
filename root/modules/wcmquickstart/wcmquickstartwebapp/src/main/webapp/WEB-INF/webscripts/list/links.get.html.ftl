<#if articles??>
    <div class="services-box">
        <#if articles.title??>
            <h3>${articles.title}</h3>
            <#if articles.description??><p>${articles.description}</p></#if>            
        </#if>   
        
        <#if articles.assets?size == 0>
            ${msg('list.none')}
        <#else>
            <ul class="services-box-list">
                <#list articles.assets as article>      
                    <li>
                        <a href="<@makeurl asset=article/>">${article.title!'no title'}</a>
                    </li>
                </#list>
            </ul>
        </#if>
    </div>
</#if>