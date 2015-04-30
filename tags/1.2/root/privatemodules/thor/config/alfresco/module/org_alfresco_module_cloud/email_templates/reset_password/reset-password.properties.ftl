<#if reset_password_url??>
<#assign label_reset_message="We received a request to reset the password associated with this e-mail address. If you made this request, please <a href='${reset_password_url}'>reset your password</a> using our secure server.">
</#if>
<#assign label_reset_unknown="We received a request to reset the password associated with this e-mail address. Unfortunately this email does not exist in our registered user list.">
<#if activate_account_url??>
<#assign label_reset_unactivated="We received a request to reset the password associated with this e-mail address. It seems you haven't activated your account yet. <a href='${activate_account_url}'>Activate your account</a> now to upload, manage and share files, collaborate with your colleagues on Alfresco.">
</#if>
<#assign label_reset_password="Reset Password">
<#assign label_reset_ignore="If you did not request to have your password reset you can safely ignore this email. Rest assured your account is safe.">
<#assign label_signed="Alfresco Team">
<#assign label_sign_up="Sign up to Alfresco">
<#if login_url??>
<#assign label_different_email="<a href='${login_url}'>Log In With A Different Email</a>">
</#if>
<#assign label_info_requested="This email was sent to you because you requested information from Alfresco.">
<#assign label_activate="Activate Account">