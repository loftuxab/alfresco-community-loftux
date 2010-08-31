<#import "../spring.ftl" as spring />

<div class="interior-header">
    <h2>${msg('contact.form.title')}</h2>
    <p class="intheader-paragraph">
        ${msg('contact.form.intro')}
    </p>
</div>

<div class="interior-content">
    <p>
        ${msg('contact.form.text')}
    </p>

    <@spring.bind "feedback.*" />
    <form id="post-comment" action="" method="post">
        <fieldset class="blog-comment-fieldset">
            <input type="hidden" name="feedbackType" value="Contact Request"/>          
            <input type="hidden" name="successPage" value="${url.context}/thankyou.html"/>          
            <h3>${msg('comments.write.title')}</h3>
            <#if spring.status.error>
                <div class="contact-error"><p>${msg('comments.write.errors')}</p></div>
            </#if> 
            <ul>
                <li>
                    <@spring.formInput path="feedback.visitorName" attributes='class="bc-input"'/>          
                    <label for="bc-name">${msg('comments.write.name')}</label> *
                    <#if spring.status.error>
                        <span class="contact-error-value"><@spring.showErrors ', '/></span>
                    </#if>                
                </li>
                <li>
                    <@spring.formInput path="feedback.visitorEmail" attributes='class="bc-input"'/>                      
                    <label for="bc-email">${msg('comments.write.email')}</label> *
                    <#if spring.status.error>
                        <span class="contact-error-value"><@spring.showErrors ', '/></span>
                    </#if>                  
                </li>     
                <li>
                    <@spring.formInput path="feedback.visitorWebsite" attributes='class="bc-input"'/>                      
                    <label for="bc-website">${msg('comments.write.website')}</label>
                </li>
                <li>
                    <@spring.formInput path="feedback.subject" attributes='class="bc-input"'/>                      
                    <label for="bc-website">${msg('comments.write.subject')}</label>
                </li>
                <li>
                    <@spring.formTextarea path="feedback.comment" attributes='rows="6" cols="54" class="bc-textarea"'/>                      
                    <#if spring.status.error>
                        <span class="contact-error-value contact-error-comment"><@spring.showErrors ', '/></span>
                    </#if>                
                </li>  
                <li><input type="submit" value="${msg('comments.write.post')}" name="post" class="bc-submit" /></li>                      
            </ul>
        </fieldset>
    </form>
</div>
