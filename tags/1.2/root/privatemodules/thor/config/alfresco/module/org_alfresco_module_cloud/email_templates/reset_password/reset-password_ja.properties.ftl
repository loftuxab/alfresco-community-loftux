<#if reset_password_url??>
<#assign label_reset_message="このEメールアドレスに関連付けられたパスワードをリセットする要求を受け取りました。 この要求を作成した場合は、安全なサーバーを使用して<a href='${reset_password_url}'>パスワードをリセットしてください</a> 。">
</#if>
<#assign label_reset_unknown="このEメールアドレスに関連付けられたパスワードをリセットする要求を受け取りました。 申し訳ありませんが、このEメールは登録済みユーザーリストにありません。">
<#if activate_account_url??>
<#assign label_reset_unactivated="このEメールアドレスに関連付けられているパスワードをリセットするようリクエストを受け取りました。Alfrescoでファイルのアップロード、管理、共有を行ったり、同僚と共同作業を行うには、今すぐ<a href='${activate_account_url}'>お客様のアカウントを有効にする</a>必要があります。">
</#if>
<#assign label_reset_password="パスワードのリセット">
<#assign label_reset_ignore="パスワードのリセットを要求していない場合、このEメールは無視しても構いません。 アカウントは保護されていますので、ご安心ください。">
<#assign label_signed="Alfrescoチーム">
<#assign label_sign_up="Alfrescoに申し込む">
<#if login_url??>
<#assign label_different_email="または、アカウントをすでにお持ちの場合は、 <a href='${login_url}'>別のEメールアドレスでログインできます。</a>.">
</#if>
<#assign label_info_requested="このEメールは、情報要求に応じてAlfrescoから送信されました。">
<#assign label_activate="アカウントを有効にする">