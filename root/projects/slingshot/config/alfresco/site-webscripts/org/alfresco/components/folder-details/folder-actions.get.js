<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/document-details/document-actions.get.js">

// Repository Url
var repositoryUrl = null,
   repositoryConfig = config.scoped["DocumentLibrary"]["repository-url"];
if (repositoryConfig !== null)
{
   repositoryUrl = repositoryConfig.value;
}

model.repositoryUrl = repositoryUrl;