<#if reset_password_url??>
<#assign label_reset_message="Wir haben eine Anfrage erhalten, das mit dieser E-Mail-Adresse verbundene Passwort zurückzusetzen. Wenn Sie diese Anfrage gesendet haben, <a href='${reset_password_url}'>setzen Sie Ihr Passwort zurück</a>, indem Sie unseren sicheren Server verwenden.">
</#if>
<#assign label_reset_unknown="Wir haben eine Anfrage erhalten, das mit dieser E-Mail-Adresse verbundene Passwort zurückzusetzen. Leider existiert diese E-Mail-Adresse nicht in unserer Liste der registrierten Benutzer.">
<#if activate_account_url??>
<#assign label_reset_unactivated="Wir haben eine Anfrage erhalten, das mit dieser E-Mail-Adresse verbundene Passwort zurückzusetzen. Sie scheinen Ihr Konto noch nicht aktiviert zu haben. <a href='${activate_account_url}'>Aktivieren Sie Ihr Konto</a> jetzt, um Dateien hochzuladen, zu verwalten und freizugeben und mit Ihren Kollegen auf Alfresco zusammenzuarbeiten.">
</#if>
<#assign label_reset_password="Passwort zurücksetzen">
<#assign label_reset_ignore="Wenn Sie keine Anfrage zum Zurücksetzen Ihres Passworts gesendet haben, können Sie diese E-Mail ohne Bedenken ignorieren. Sie können sicher sein, dass Ihr Konto geschützt ist.">
<#assign label_signed="Das Alfresco-Team">
<#assign label_sign_up="Registrieren Sie sich bei Alfresco.">
<#if login_url??>
<#assign label_different_email="Wenn Sie bereits über ein Konto verfügen, können Sie sich alternativ <a href='${login_url}'>mit einer anderen E-Mail-Adresse anmelden.</a>.">
</#if>
<#assign label_info_requested="Diese E-Mail wurde an Sie gesendet, weil Sie Informationen von Alfresco angefordert haben.">
<#assign label_activate="Konto aktivieren">