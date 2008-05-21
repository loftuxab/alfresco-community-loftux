var successUrl = url.context;

if(typeof alfRedirectUrl!="undefined")
{
	successUrl = alfRedirectUrl;
}

model.successUrl = successUrl;
