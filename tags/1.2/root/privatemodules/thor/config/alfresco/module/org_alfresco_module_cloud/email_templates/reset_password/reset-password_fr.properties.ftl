<#if reset_password_url??>
<#assign label_reset_message="Nous avons reçu une demande de réinitialisation du mot de passe associé à cette adresse e-mail. Si vous avez fait cette demande, veuillez <a href='${reset_password_url}'>réinitialiser votre mot de passe</a> en utilisant notre serveur sécurisé.">
</#if>
<#assign label_reset_unknown="Nous avons reçu une demande de réinitialisation du mot de passe associé à cette adresse e-mail. Malheureusement, cette adresse e-mail n'existe pas dans la liste des utilisateurs enregistrés.">
<#if activate_account_url??>
<#assign label_reset_unactivated="Nous avons reçu une demande de réinitialisation du mot de passe associé à cette adresse e-mail. Il semblerait que votre compte n'ait pas encore été activé. <a href='${activate_account_url}'>Activez votre compte</a> maintenant pour télécharger, gérer et partager des fichiers, et pour collaborer avec vos collègues sur Alfresco.">
</#if>
<#assign label_reset_password="Réinitialiser le mot de passe">
<#assign label_reset_ignore="Si vous n'avez pas demandé la réinitialisation de votre mot de passe, ignorez simplement cet e-mail. Soyez assuré que votre compte est en sécurité.">
<#assign label_signed="L'équipe Alfresco">
<#assign label_sign_up="Créer un compte Alfresco">
<#if login_url??>
<#assign label_different_email="Si vous avez déjà un compte, vous pouvez aussi vous <a href='${login_url}'>connecter avec une adresse e-mail différente</a>.">
</#if>
<#assign label_info_requested="Vous recevez cet e-mail car vous avez demandé des informations à Alfresco.">
<#assign label_activate="Activer mon compte">