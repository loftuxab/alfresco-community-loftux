var successUrl = context.properties["alfRedirectUrl"];
if (successUrl == null)
{
	successUrl = url.context;
}
model.successUrl = successUrl;