<#if reset_password_url??>
<#assign label_reset_message="Hemos recibido una solicitud para restablecer la contraseña asociada a esta dirección de email. Si realizó esta solicitud, <a href='${reset_password_url}'>restablezca su contraseña</a> a través de nuestro servidor seguro.">
</#if>
<#assign label_reset_unknown="Hemos recibido una solicitud para restablecer la contraseña asociada a esta dirección de email. Este email no existe en nuestra lista de usuarios registrados.">
<#if activate_account_url??>
<#assign label_reset_unactivated="Hemos recibido una solicitud para restablecer la contraseña asociada a esta dirección de email. Parece que todavía no ha activado su cuenta. <a href='${activate_account_url}'>Active su cuenta</a> ahora para cargar, gestionar y compartir ficheros, y para colaborar con sus colegas en Alfresco.">
</#if>
<#assign label_reset_password="Restablecer contraseña">
<#assign label_reset_ignore="Si no solicitó restablecer su contraseña, puede ignorar este email. Le garantizamos que su cuenta está protegida.">
<#assign label_signed="El equipo de Alfresco">
<#assign label_sign_up="Registrarse en Alfresco">
<#if login_url??>
<#assign label_different_email="Como alternativa, si ya tiene una cuenta, puede <a href='${login_url}'>registrarse con una cuenta de email distinta</a>.">
</#if>
<#assign label_info_requested="Ha recibido este email porque solicitó información sobre Alfresco.">
<#assign label_activate="Activar cuenta">