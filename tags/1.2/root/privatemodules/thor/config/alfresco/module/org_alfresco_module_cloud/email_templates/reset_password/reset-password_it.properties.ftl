<#if reset_password_url??>
<#assign label_reset_message="È stata ricevuta una richiesta di ripristino della password associata a questo indirizzo e-mail. Se la richiesta è stata effettuata, <a href='${reset_password_url}'>ripristinare la password</a> utilizzando il nostro server protetto.">
</#if>
<#assign label_reset_unknown="È stata ricevuta una richiesta di ripristino della password associata a questo indirizzo e-mail. Questa e-mail non esiste nel nostro elenco utenti registrati.">
<#if activate_account_url??>
<#assign label_reset_unactivated="È stata ricevuta una richiesta di ripristino della password associata a questo indirizzo e-mail. Probabilmente non è stato ancora attivato l'account. <a href='${activate_account_url}'>Attiva l'account</a> adesso per caricare, gestire, condividere file e per collaborare con i colleghi su Alfresco.">
</#if>
<#assign label_reset_password="Ripristina password">
<#assign label_reset_ignore="Se la richiesta di ripristino password non è stata effettuata, ignorare questa e-mail. Le garantiamo la massima protezione dell'account.">
<#assign label_signed="Alfresco Team">
<#assign label_sign_up="Registrati ad Alfresco">
<#if login_url??>
<#assign label_different_email="Se invece si dispone già di un account, è possibile <a href='${login_url}'>accedere con un altro indirizzo e-mail</a>.">
</#if>
<#assign label_info_requested="Questa e-mail è stata inviata in seguito alla richiesta di informazioni da Alfresco.">
<#assign label_activate="Attiva account">